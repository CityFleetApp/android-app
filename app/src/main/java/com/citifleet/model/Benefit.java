package com.citifleet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vika on 12.03.16.
 */
public class Benefit {
    @SerializedName("image_thumbnail")
    private String image;
    private String name;
    private String barcode;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
