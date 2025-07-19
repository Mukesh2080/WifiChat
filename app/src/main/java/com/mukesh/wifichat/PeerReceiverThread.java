package com.mukesh.wifichat;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class PeerReceiverThread extends Thread {
    private static final int PORT = 8888;
    private final Set<String> peers = new HashSet<>();
    private boolean running = true;
    private final PeerUpdateListener listener;

    public interface PeerUpdateListener {
        void onNewPeer(String ip, String name);
    }

    public PeerReceiverThread(PeerUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"))) {
            socket.setBroadcast(true);
            byte[] buffer = new byte[1500];

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength());
                if (msg.startsWith("HELLO:")) {
                    String name = msg.split(":", 2)[1];
                    String senderIP = packet.getAddress().getHostAddress();
                    if (peers.add(senderIP)) {
                        listener.onNewPeer(senderIP, name);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("PeerReceiver", "Error receiving", e);
        }
    }

    public void stopReceiving() {
        running = false;
    }
}

