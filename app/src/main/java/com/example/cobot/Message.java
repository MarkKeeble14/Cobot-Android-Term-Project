package com.example.cobot;

/*
    A class to represent a message.
 */
public class Message {
    private String id;
    private String content;
    private String sender;
    private String time;
    private String date;

    // All parameter constructor.
    public Message(String id, String content, String sender, String time, String date) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.time = time;
        this.date = date;
    }

    // No parameter constructor
    public Message() {}

    // To String
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    // Getters and setters below
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
