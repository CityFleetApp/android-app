package com.cityfleet.model;

import org.parceler.Parcel;

/**
 * Created by vika on 04.04.16.
 */
@Parcel
public class Photo {
    private int id;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
