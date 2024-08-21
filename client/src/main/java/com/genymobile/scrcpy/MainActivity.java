package com.genymobile.scrcpy;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AssetManager assetManager = getAssets();

        findViewById(R.id.btn).setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JadbConnection connection = new JadbConnection();
                    JadbDevice device = connection.getAnyDevice();
                    try {
                        InputStream inputStream = assetManager.open("scrcpy-server.jar");
                        device.push(inputStream, 1,664, new RemoteFile("/data/local/tmp/"));
                    } catch (IOException | JadbException e) {
                        throw new RuntimeException(e);
                    }

                }
            }).start();
        });
    }


}