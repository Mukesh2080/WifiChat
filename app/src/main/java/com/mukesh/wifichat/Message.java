package com.mukesh.wifichat;

public class Message {
    private final String text;
    private final boolean isSentByMe;
    private final long timestamp;
    private String status;

    public Message(String text, boolean isSentByMe, long timestamp, String status) {
        this.text = text;
        this.isSentByMe = isSentByMe;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getText() { return text; }
    public boolean isSentByMe() { return isSentByMe; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return this.status; }

}

