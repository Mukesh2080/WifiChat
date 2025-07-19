package com.mukesh.wifichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private MessageAdapter adapter;
    private final List<Message> messages = new ArrayList<>();

    private String peerIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent serviceIntent = new Intent(this, MessageReceiverService.class);
        startService(serviceIntent);

        peerIp = getIntent().getStringExtra("peer_ip");

        recyclerView = findViewById(R.id.recycler_view);
        inputMessage = findViewById(R.id.edit_text_input);
        adapter = new MessageAdapter(messages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.button_send).setOnClickListener(v -> {
            String messageText = inputMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                inputMessage.setText("");
            }
        });
    }

    private void sendMessage(String messageText) {
        messages.add(new Message(messageText, true, System.currentTimeMillis(),""));
        adapter.notifyItemInserted(messages.size() - 1);

        // TODO: send over TCP
        new Thread(() -> {
            try {
                Socket socket = new Socket(peerIp, 9876);
                OutputStream os = socket.getOutputStream();
                os.write(messageText.getBytes(StandardCharsets.UTF_8));
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void receiveMessage(String message) {
        runOnUiThread(() -> {
            messages.add(new Message(message, false,System.currentTimeMillis(),"")); // false = received
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);
        });
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String senderIp = intent.getStringExtra("senderIp");

            if (peerIp != null && peerIp.equals(senderIp)) {
                receiveMessage(message);
            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.mukesh.MESSAGE_RECEIVED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(messageReceiver, filter,Context.RECEIVER_EXPORTED);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(messageReceiver);
    }



}
