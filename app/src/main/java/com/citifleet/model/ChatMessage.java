package com.citifleet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vika on 18.04.16.
 */
public class ChatMessage {
    private String text;
    private int room;
    private String created;
    @SerializedName("author_info")
    private ChatFriend author;

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

    public ChatFriend getAuthor() {
        return author;
    }

    public void setAuthor(ChatFriend author) {
        this.author = author;
    }
}
