package com.citifleet.model;

import com.citifleet.util.Constants;
import com.google.gson.annotations.Expose;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vika on 23.03.16.
 */
@Parcel
public class Car {
    private int id;
    private String make;
    private String model;
    private String type;
    private String color;
    private int year;
    private String fuel;
    private int seats;
    private String price;
    private String description;
    private boolean rent;
    private int[] dimensions = new int[2];
    private List<String> photos = new ArrayList<String>();

    @Expose
    private int makeId = Constants.DEFAULT_UNSELECTED_POSITION;
    @Expose
    private int modelId = Constants.DEFAULT_UNSELECTED_POSITION;
    @Expose
    private int typeId = Constants.DEFAULT_UNSELECTED_POSITION;
    @Expose
    private int colorId = Constants.DEFAULT_UNSELECTED_POSITION;
    @Expose
    private int fuelId = Constants.DEFAULT_UNSELECTED_POSITION;
    @Expose
    private int seatsId = Constants.DEFAULT_UNSELECTED_POSITION;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRent() {
        return rent;
    }

    public void setRent(boolean rent) {
        this.rent = rent;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public int getMakeId() {
        return makeId;
    }

    public void setMakeId(int makeId) {
        this.makeId = makeId;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public int getFuelId() {
        return fuelId;
    }

    public void setFuelId(int fuelId) {
        this.fuelId = fuelId;
    }

    public int getSeatsId() {
        return seatsId;
    }

    public void setSeatsId(int seatsId) {
        this.seatsId = seatsId;
    }
}
