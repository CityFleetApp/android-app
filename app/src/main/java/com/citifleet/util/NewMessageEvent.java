package com.citifleet.util;

import com.citifleet.model.ChatMessage;

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
