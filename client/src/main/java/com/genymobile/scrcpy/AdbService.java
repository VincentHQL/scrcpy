package com.genymobile.scrcpy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;
import se.vidstige.jadb.server.AdbDeviceResponder;
import se.vidstige.jadb.server.AdbResponder;
import se.vidstige.jadb.server.AdbServer;

public class AdbService extends Service implements AdbResponder {
    private static final String TAG = "AdbService";
    private final AdbServer server;
    private final List<DeviceResponder> devices = new ArrayList<>();

    public AdbService() {
        server = new AdbServer(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            server.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            server.stop();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCommand(String command) {

    }

    @Override
    public int getVersion() {
        return 31;
    }

    @Override
    public List<AdbDeviceResponder> getDevices() {
        return new ArrayList<>(devices);
    }

    private static class DeviceResponder implements AdbDeviceResponder {
        // 需要保存一个与adbd 的连接

        @Override
        public String getSerial() {
            return null;
        }

        @Override
        public String getType() {
            return null;
        }

        @Override
        public void filePushed(RemoteFile path, int mode, ByteArrayOutputStream buffer) throws JadbException {

        }

        @Override
        public void filePulled(RemoteFile path, ByteArrayOutputStream buffer) throws JadbException, IOException {

        }

        @Override
        public void shell(String command, DataOutputStream stdout, DataInput stdin) throws IOException {

        }

        @Override
        public void enableIpCommand(String ip, DataOutputStream outputStream) throws IOException {

        }

        @Override
        public List<RemoteFile> list(String path) throws IOException {
            return null;
        }
    }

}