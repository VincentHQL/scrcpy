package com.genymobile.scrcpy.monitor;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import com.genymobile.scrcpy.AsyncProcessor;
import com.genymobile.scrcpy.util.IO;
import com.genymobile.scrcpy.util.Ln;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lightweight HTTP server using LocalServerSocket
 */
public class MonitorServer implements AsyncProcessor {
    private static final String SOCKET_NAME_PREFIX = "scrcpy_monitor";

    private final Router router;
    private LocalServerSocket serverSocket;
    private final ExecutorService executorService;
    private final AtomicBoolean stopped = new AtomicBoolean();
    private Thread acceptThread;

    private final int scid;

    public MonitorServer(int scid, Router router) {
        this.scid = scid;
        this.router = router;
        this.executorService = Executors.newCachedThreadPool();
    }

    private String getSocketName(int scid) {
        if (scid == -1) {
            // If no SCID is set, use "scrcpy" to simplify using scrcpy-server alone
            return SOCKET_NAME_PREFIX;
        }

        return SOCKET_NAME_PREFIX + String.format("_%08x", scid);
    }


    @Override
    public void start(TerminationListener listener) {
        acceptThread = new Thread(() -> {
            try {
                serverSocket = new LocalServerSocket(getSocketName(scid));
                while (!stopped.get()) {
                    LocalSocket clientSocket = serverSocket.accept();
                    executorService.execute(() -> handleClient(clientSocket));
                }
            } catch (IOException e) {
                // Broken pipe is expected on close, because the socket is closed by the client
                if (!IO.isBrokenPipe(e)) {
                    Ln.e("monitor error", e);
                }
            } finally {
                Ln.d("monitor stopped");
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        // in
                    }
                }
                executorService.shutdown();
                listener.onTerminated(true);
            }
        }, "monitor");
        acceptThread.start();
    }

    /**
     * Stop the HTTP server
     */
    @Override
    public void stop() {
        if (acceptThread != null) {
            stopped.set(true);
        }
    }

    @Override
    public void join() throws InterruptedException {
        if (acceptThread != null) {
            acceptThread.join();
        }

        if (executorService != null) {
            boolean await = executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            if (await) {
                Ln.e("executor service await failed!");
            }
        }
    }

    /**
     * Handle a client connection
     */
    private void handleClient(LocalSocket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            // Parse request
            HttpRequest request = HttpRequest.parse(inputStream);
            Ln.i("new request: " + request.getUri());

            // Handle request
            HttpResponse response;
            try {
                response = router.handle(request);
            } catch (Exception e) {
                Ln.e("Error handling request", e);
                response = HttpResponse.newFixedLengthResponse(
                        HttpResponse.Status.INTERNAL_ERROR,
                        "text/plain",
                        "Internal Server Error: " + e.getMessage()
                );
            }

            // Send response
            response.send(outputStream);

        } catch (IOException e) {
            Ln.e("Error handling client", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                Ln.e("Error closing client socket", e);
            }
        }
    }
}
