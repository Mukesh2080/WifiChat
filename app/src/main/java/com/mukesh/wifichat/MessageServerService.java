package com.mukesh.wifichat;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageServerService extends Service {
    private static final int SERVER_PORT = 9876;
    private boolean running = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(this::startServer).start();
        return START_STICKY;
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (running) {
                Socket client = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String message = reader.readLine();

                Log.d("MessageServer", "Received: " + message);

                // TODO: Decrypt and notify UI
                showNotification(message);

                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() ->
                Toast.makeText(this, "Message received: " + msg, Toast.LENGTH_LONG).show()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

