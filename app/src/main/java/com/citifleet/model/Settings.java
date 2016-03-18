package com.citifleet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vika on 18.03.16.
 */
public class Settings {
    @SerializedName("notifications_enabled")
    private boolean notificationsEnabled;
    @SerializedName("chat_privacy")
    boolean chatPrivacy;
    boolean visible;

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isChatPrivacy() {
        return chatPrivacy;
    }

    public void setChatPrivacy(boolean chatPrivacy) {
        this.chatPrivacy = chatPrivacy;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
