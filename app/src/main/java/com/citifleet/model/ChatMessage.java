package com.citifleet.model;

/**
 * Created by vika on 15.04.16.
 */
public class ChatMessage {
    private String method=ChatMessageTypes.POST_MESSAGE.getName();
    private String text;
    private int room;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }
}
