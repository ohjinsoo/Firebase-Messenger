package edu.illinois.finalproject.Chatrooms;

/**
 * Created by ohjin on 12/5/2017.
 */

public class Chat {
    private String chatKey;
    private String title;
    private String lastMessage;
    private long timestamp;

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Chat(String chatKey, String title, String lastMessage, long timestamp) {
        this.chatKey = chatKey;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public Chat() {

    }
}
