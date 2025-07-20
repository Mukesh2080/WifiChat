package com.mukesh.wifichat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements PeerUpdateListener {

    private ListView peerListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> peerList;
    private PeerDiscoveryService peerDiscoveryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startService(new Intent(this, MessageServerService.class));
        Intent serviceIntent = new Intent(this, MessageReceiverService.class);
        startService(serviceIntent);

        peerListView = findViewById(R.id.peerListView);
        peerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, peerList);
        peerListView.setAdapter(adapter);
        peerListView.setOnItemClickListener((parent, view, position, id) -> {
            String peer = peerList.get(position);
            String ip = peer.split(" ")[0]; // extract IP only
            MessageSender.sendMessage(ip, "Hello from " + Build.MODEL);
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("peer_ip", ip);
            intent.putExtra("name", extractNames(peerList).get(position));
            startActivity(intent);

        });

        // Start the Peer Discovery
        peerDiscoveryService = new PeerDiscoveryService(this);
        peerDiscoveryService.setPeerUpdateListener(this);
        peerDiscoveryService.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (peerDiscoveryService != null) {
            peerDiscoveryService.stop();
        }
    }

    // Called when new peers are discovered
    @Override
    public void onPeerListUpdated(ArrayList<String> updatedPeers) {
        runOnUiThread(() -> {
            peerList.clear();
            peerList.addAll(updatedPeers);
            adapter.notifyDataSetChanged();
        });
    }
    public List<String> extractNames(List<String> originalList) {
        return originalList.stream()
                .map(item -> {
                    Matcher matcher = Pattern.compile("\\((.*?)\\)").matcher(item);
                    return matcher.find() ? matcher.group(1) : null;
                })
                .filter(name -> name != null)
                .collect(Collectors.toList());
    }
}
