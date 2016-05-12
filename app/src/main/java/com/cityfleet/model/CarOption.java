package com.cityfleet.model;

/**
 * Created by vika on 24.03.16.
 */
public class CarOption {
    private int id;
    private String name;

    public CarOption() {
    }

    public CarOption(int id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
