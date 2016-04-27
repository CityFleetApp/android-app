package com.cityfleet.util;

import com.cityfleet.model.ChatMessage;

/**
 * Created by vika on 18.04.16.
 */
public class NewMessageEvent {
    private ChatMessage chatMessage;

    public NewMessageEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
