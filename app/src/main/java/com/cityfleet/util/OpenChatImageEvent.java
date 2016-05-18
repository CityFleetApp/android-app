package com.cityfleet.util;

/**
 * Created by vika on 18.05.16.
 */
public class OpenChatImageEvent {
    private String imageUrl;

    public OpenChatImageEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
