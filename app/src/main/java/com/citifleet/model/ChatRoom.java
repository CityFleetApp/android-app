package com.citifleet.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vika on 12.04.16.
 */
public class ChatRoom {
    private int id;
    private String name;
    private String label;
    @SerializedName("participants_info")
    private List<ChatFriend> participants;
    @SerializedName("last_message")
    private String lastMessage;
    @SerializedName("last_message_timestamp")
    private String lastMessageTimestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ChatFriend> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ChatFriend> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
