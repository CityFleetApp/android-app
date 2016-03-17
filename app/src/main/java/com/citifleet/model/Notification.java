package com.citifleet.model;

/**
 * Created by vika on 17.03.16.
 */
public class Notification {
    private String title;
    private String message;
    private String created;
    private boolean unseen;
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isUnseen() {
        return unseen;
    }

    public void setUnseen(boolean unseen) {
        this.unseen = unseen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
