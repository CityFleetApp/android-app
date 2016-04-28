package com.cityfleet.model;

import com.cityfleet.util.Constants;
import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vika on 24.03.16.
 */
@Parcel
public class GeneralGood {
    private int id;
    private String item;
    private String condition;
    private String description;
    private String price;
    private int[] dimensions = new int[2];
    private List<Photo> photos = new ArrayList<Photo>();
    @Expose
    private int conditionId = Constants.DEFAULT_UNSELECTED_POSITION;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public int getConditionId() {
        return conditionId;
    }

    public void setConditionId(int conditionId) {
        this.conditionId = conditionId;
    }
}