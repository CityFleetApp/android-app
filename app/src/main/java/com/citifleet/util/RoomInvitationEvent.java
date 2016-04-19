package com.citifleet.util;

import com.citifleet.model.ChatRoom;

/**
 * Created by vika on 19.04.16.
 */
public class RoomInvitationEvent {
    private ChatRoom chatRoom;

    public RoomInvitationEvent(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }
}
