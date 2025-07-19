package com.mukesh.wifichat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageReceiverService extends Service {
    private static final int PORT = 9876;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running = true;
    private OnMessageReceivedListener listener;

    public void MessageReceiver(OnMessageReceivedListener listener) {
        this.listener = listener;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    private void startServer() {
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                while (running) {
                    Socket client = serverSocket.accept();
                    String senderIp = client.getInetAddress().getHostAddress();
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String receivedText = in.readLine();
                    Log.d("Receiver", "Received: " + receivedText);
                    if (receivedText != null && !receivedText.isEmpty()) {
                        Log.d("Server", "Received: " + receivedText);
                        Intent broadcastIntent = new Intent("com.mukesh.MESSAGE_RECEIVED");
                        broadcastIntent.putExtra("message", receivedText);
                        broadcastIntent.putExtra("senderIp", senderIp);
                        sendBroadcast(broadcastIntent);
                    }

                    client.close();
                }
            } catch (IOException e) {
                Log.e("Receiver", "Server error", e);
            }
        });
        serverThread.start();
    }

    @Override
    public void onDestroy() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            Log.e("Receiver", "Error closing server", e);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

