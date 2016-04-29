package com.cityfleet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vika on 29.04.16.
 */
public class JobOfferCompletedPushNotification {
    private int id;
    private String type;
    private String title;
    @SerializedName("offer_title")
    private String offerTitle;
    @SerializedName("driver_name")
    private String driverName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }
}
