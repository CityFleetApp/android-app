package com.cityfleet.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by vika on 17.03.16.
 */
@Parcel
public class Notification {
    private String title;
    private String type;
    private String message;
    private String created;
    private boolean unseen;
    private int id;
    @SerializedName("ref_type")
    private String refType;
    @SerializedName("ref_id")
    private int refId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }
}
