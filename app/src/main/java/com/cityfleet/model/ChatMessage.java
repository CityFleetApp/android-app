package com.cityfleet.model;

import java.util.List;

/**
 * Created by vika on 18.04.16.
 */
public class ChatMessage {
    private String text;
    private int room;
    private String created;
    private int author;
    private List<ChatFriend> participants;

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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public List<ChatFriend> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ChatFriend> participants) {
        this.participants = participants;
    }
}
