package com.cityfleet.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by vika on 16.03.16.
 */
@Parcel
public class LegalAidPerson {
    String name;
    @SerializedName("years_of_experience")
    int yearsOfExperience;
    int rating;
    String phone;
    String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
