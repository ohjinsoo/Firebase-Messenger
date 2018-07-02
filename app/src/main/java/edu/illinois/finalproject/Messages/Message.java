package edu.illinois.finalproject.Messages;

/**
 * Created by ohjin on 12/4/2017.
 */

public class Message {
    private String text;
    private String name;
    private long timestamp;

    public Message(String text, String name, long timestamp) {
        this.text = text;
        this.name = name;
        this.timestamp = timestamp;
    }

    public Message () {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
