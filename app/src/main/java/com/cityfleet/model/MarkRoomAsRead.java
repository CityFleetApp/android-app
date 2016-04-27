package com.cityfleet.model;

/**
 * Created by vika on 21.04.16.
 */
public class MarkRoomAsRead {
    private String method = ChatMessageTypes.READ_ROOM.getName();
    private int room;

    public MarkRoomAsRead(int room) {
        this.room = room;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }
}
