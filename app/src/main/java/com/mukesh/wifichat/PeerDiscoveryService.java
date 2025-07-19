package com.mukesh.wifichat;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class PeerDiscoveryService {

    private static final int PORT = 8888;
    private static final String BROADCAST_MESSAGE = "HELLO_WIFI_PEER";
    private static final long BROADCAST_INTERVAL = 3000;

    private final Context context;
    private final Set<String> peerSet = new HashSet<>();
    private final Handler handler = new Handler();
    private boolean running = false;

    private PeerUpdateListener listener; // 🔸 Add this

    public PeerDiscoveryService(Context context) {
        this.context = context.getApplicationContext();
    }

    // 🔸 Add this method so MainActivity can set the listener
    public void setPeerUpdateListener(PeerUpdateListener listener) {
        this.listener = listener;
    }

    public void start() {
        running = true;
        startBroadcasting();
        startReceiving();
    }

    public void stop() {
        running = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void startBroadcasting() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!running) return;
                broadcastPresence();
                handler.postDelayed(this, BROADCAST_INTERVAL);
            }
        }, BROADCAST_INTERVAL);
    }

    private void broadcastPresence() {
        new Thread(() -> {
            try {
                String deviceName = android.os.Build.MODEL; // or let user set it
                String messageToSend = BROADCAST_MESSAGE + "::" + deviceName;
                byte[] data = messageToSend.getBytes();

                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);

                for (InetAddress broadcastAddress : getBroadcastAddresses()) {
                    DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAddress, PORT);
                    socket.send(packet);
                }

                socket.close();
            } catch (Exception e) {
                Log.e("PeerDiscovery", "Broadcast error: ", e);
            }
        }).start();
    }

    private void startReceiving() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(PORT)) {
                socket.setBroadcast(true);

                byte[] buffer = new byte[1500];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength());

                    if (message.startsWith(BROADCAST_MESSAGE)) {
                        String peerAddress = packet.getAddress().getHostAddress();
                        String[] parts = message.split("::");
                        String deviceName = parts.length > 1 ? parts[1] : "Unknown";

                        String peerId = peerAddress + " (" + deviceName + ")";

                        if (peerSet.add(peerId)) {
                            notifyPeerUpdate();
                        }
                    }

                }
            } catch (Exception e) {
                Log.e("PeerDiscovery", "Receive error: ", e);
            }
        }).start();
    }

    private void notifyPeerUpdate() {
        if (listener != null) {
            ArrayList<String> peers = new ArrayList<>(peerSet);
            listener.onPeerListUpdated(peers);
        }
    }

    private ArrayList<InetAddress> getBroadcastAddresses() {
        ArrayList<InetAddress> broadcastList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();

                if (!iface.isUp() || iface.isLoopback()) continue;

                for (InterfaceAddress addr : iface.getInterfaceAddresses()) {
                    InetAddress broadcast = addr.getBroadcast();
                    if (broadcast != null) {
                        broadcastList.add(broadcast);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("PeerDiscovery", "Error getting broadcast addresses", e);
        }

        return broadcastList;
    }
}
