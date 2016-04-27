package com.cityfleet.model;

/**
 * Created by vika on 15.04.16.
 */
public enum ChatMessageTypes {
    POST_MESSAGE("post_message"), RECEIVE_MESSAGE("receive_message"), ROOM_INVITATION("room_invitation"), READ_ROOM("read_room");
    private String name;

    ChatMessageTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
