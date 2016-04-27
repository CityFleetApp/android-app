package com.cityfleet.util;

import com.cityfleet.model.ChatRoom;

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
