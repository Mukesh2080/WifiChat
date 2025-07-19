package com.mukesh.wifichat;

public class Message {
    private final String text;
    private final boolean isSentByMe;
    private final long timestamp;
    private String status="";
    private String messageId;


    public Message(String text, boolean isSentByMe, long timestamp, String messageId,String status) {
        this.text = text;
        this.isSentByMe = isSentByMe;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.status = status;
    }

    public String getText() { return text; }
    public boolean isSentByMe() { return isSentByMe; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return this.status; }
    public void setStatus(String status) {  this.status = status; }

}

