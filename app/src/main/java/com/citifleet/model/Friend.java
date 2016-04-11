package com.citifleet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vika on 11.04.16.
 */
public class Friend {
    private int id;
    @SerializedName("full_name")
    private String name;
    @SerializedName("phone")
    private String phoneNumber;
    @SerializedName("avatar_url")
    private String photo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
