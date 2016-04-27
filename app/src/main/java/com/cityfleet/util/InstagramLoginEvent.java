package com.cityfleet.util;

/**
 * Created by vika on 11.03.16.
 */
public class InstagramLoginEvent {
    private String token;

    public InstagramLoginEvent(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
