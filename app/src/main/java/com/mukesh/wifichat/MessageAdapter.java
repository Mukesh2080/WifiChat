package com.mukesh.wifichat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> messageList;

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isSentByMe() ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SENT) {
            View view = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).timeTextView.setText(formatTimestamp(message.getTimestamp()));
            ((SentViewHolder) holder).textView.setText(message.getText());
            switch(message.getStatus().toUpperCase()) {
                case "SENT":
                    ((SentViewHolder) holder).read_receipt.setImageResource(R.drawable.ic_single_tick_24);
                    break;
                case "DELIVERED":
                    ((SentViewHolder) holder).read_receipt.setImageResource(R.drawable.ic_doubletick_24);
                    break;
                case "READ":
                    ((SentViewHolder) holder).read_receipt.setImageResource(R.drawable.ic_doubletick_24);
                    break;
            }
        } else {
            ((ReceivedViewHolder) holder).timeTextView.setText(formatTimestamp(message.getTimestamp())+" "+message.getStatus());

            ((ReceivedViewHolder) holder).textView.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView textView,timeTextView;
        ImageView read_receipt;
        SentViewHolder(View view) {
            super(view);
            read_receipt = view.findViewById(R.id.read_receipt);
            textView = view.findViewById(R.id.text_message_body);
            timeTextView = view.findViewById(R.id.time_stmp);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView textView,timeTextView;
        ReceivedViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.text_message_body);
            timeTextView = view.findViewById(R.id.time_stmp);
        }
    }

    private String formatTimestamp(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timeMillis));
    }

}
