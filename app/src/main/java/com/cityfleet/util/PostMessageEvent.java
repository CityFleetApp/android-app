package com.cityfleet.util;

import com.cityfleet.model.ChatMessageToSend;

/**
 * Created by vika on 18.04.16.
 */
public class PostMessageEvent {
    private ChatMessageToSend chatMessageToSend;

    public PostMessageEvent(ChatMessageToSend chatMessageToSend) {
        this.chatMessageToSend = chatMessageToSend;
    }

    public ChatMessageToSend getChatMessageToSend() {
        return chatMessageToSend;
    }
}
