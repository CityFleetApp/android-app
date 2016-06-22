package com.cityfleet.model;

import org.parceler.Parcel;

/**
 * Created by vika on 15.03.16.
 */
@Parcel
public class UserImages {
    int id;
    String file;
    String thumbnail;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
