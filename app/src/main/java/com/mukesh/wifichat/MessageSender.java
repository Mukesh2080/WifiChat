package com.mukesh.wifichat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender {
    public static void sendMessage(String ip, String message) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(ip, 9876);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(message);
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

