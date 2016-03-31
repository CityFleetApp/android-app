package com.citifleet.model;

import com.citifleet.util.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by vika on 31.03.16.
 */
@Parcel
public class UserEditInfo {
    private String bio;
    private String username;
    private String phone;
    @SerializedName("car_make")
    private int carMake = Constants.DEFAULT_UNSELECTED_POSITION;
    @SerializedName("car_model")
    private int carModel = Constants.DEFAULT_UNSELECTED_POSITION;
    @SerializedName("car_year")
    private int carYear = Constants.DEFAULT_UNSELECTED_POSITION;
    @SerializedName("car_type")
    private int carType = Constants.DEFAULT_UNSELECTED_POSITION;
    @SerializedName("car_color")
    private int carColor = Constants.DEFAULT_UNSELECTED_POSITION;
    @SerializedName("car_make_display")
    private String carMakeDisplay;
    @SerializedName("car_model_display")
    private String carModelDisplay;
    @SerializedName("car_color_display")
    private String carColorDisplay;
    @SerializedName("car_type_display")
    private String carTypeDisplay;
    @Expose
    private boolean isBioChanged = false;
    @Expose
    private boolean isUsernameChanged = false;
    @Expose
    private boolean isPhoneChanged = false;
    @Expose
    private boolean isCarMakeChanged = false;
    @Expose
    private boolean isCarModelChanged = false;
    @Expose
    private boolean isCarColorChanged = false;
    @Expose
    private boolean isCarTypeChanged = false;
    @Expose
    private boolean isCarYearChanged = false;


    public UserEditInfo() {
    }

    public UserEditInfo(UserEditInfo info) {
        bio = info.getBio();
        username = info.getUsername();
        phone = info.getPhone();
        carMake = info.getCarMake();
        carModel = info.getCarModel();
        carYear = info.getCarYear();
        carType = info.getCarType();
        carColor = info.getCarColor();
        carMakeDisplay = info.getCarMakeDisplay();
        carModelDisplay = info.getCarModelDisplay();
        carColorDisplay = info.getCarColorDisplay();
        carTypeDisplay = info.getCarTypeDisplay();
        isBioChanged = info.isBioChanged;
        isUsernameChanged = info.isUsernameChanged;
        isPhoneChanged = info.isPhoneChanged;
        isCarMakeChanged = info.isCarMakeChanged;
        isCarModelChanged = info.isCarModelChanged;
        isCarTypeChanged = info.isCarTypeChanged;
        isCarColorChanged = info.isCarColorChanged;
        isCarYearChanged = info.isCarYearChanged;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isBioChanged() {
        return isBioChanged;
    }

    public void setBioChanged(boolean bioChanged) {
        isBioChanged = bioChanged;
    }

    public boolean isUsernameChanged() {
        return isUsernameChanged;
    }

    public void setUsernameChanged(boolean usernameChanged) {
        isUsernameChanged = usernameChanged;
    }

    public boolean isPhoneChanged() {
        return isPhoneChanged;
    }

    public void setPhoneChanged(boolean phoneChanged) {
        isPhoneChanged = phoneChanged;
    }

    public int getCarMake() {
        return carMake;
    }

    public void setCarMake(int carMake) {
        this.carMake = carMake;
    }

    public int getCarModel() {
        return carModel;
    }

    public void setCarModel(int carModel) {
        this.carModel = carModel;
    }

    public int getCarYear() {
        return carYear;
    }

    public void setCarYear(int carYear) {
        this.carYear = carYear;
    }

    public int getCarType() {
        return carType;
    }

    public void setCarType(int carType) {
        this.carType = carType;
    }

    public int getCarColor() {
        return carColor;
    }

    public void setCarColor(int carColor) {
        this.carColor = carColor;
    }

    public String getCarMakeDisplay() {
        return carMakeDisplay;
    }

    public void setCarMakeDisplay(String carMakeDisplay) {
        this.carMakeDisplay = carMakeDisplay;
    }

    public String getCarModelDisplay() {
        return carModelDisplay;
    }

    public void setCarModelDisplay(String carModelDisplay) {
        this.carModelDisplay = carModelDisplay;
    }

    public String getCarColorDisplay() {
        return carColorDisplay;
    }

    public void setCarColorDisplay(String carColorDisplay) {
        this.carColorDisplay = carColorDisplay;
    }

    public String getCarTypeDisplay() {
        return carTypeDisplay;
    }

    public void setCarTypeDisplay(String carTypeDisplay) {
        this.carTypeDisplay = carTypeDisplay;
    }

    public boolean isCarMakeChanged() {
        return isCarMakeChanged;
    }

    public void setCarMakeChanged(boolean carMakeChanged) {
        isCarMakeChanged = carMakeChanged;
    }

    public boolean isCarModelChanged() {
        return isCarModelChanged;
    }

    public void setCarModelChanged(boolean carModelChanged) {
        isCarModelChanged = carModelChanged;
    }

    public boolean isCarColorChanged() {
        return isCarColorChanged;
    }

    public void setCarColorChanged(boolean carColorChanged) {
        isCarColorChanged = carColorChanged;
    }

    public boolean isCarTypeChanged() {
        return isCarTypeChanged;
    }

    public void setCarTypeChanged(boolean carTypeChanged) {
        isCarTypeChanged = carTypeChanged;
    }

    public boolean isCarYearChanged() {
        return isCarYearChanged;
    }

    public void setCarYearChanged(boolean carYearChanged) {
        isCarYearChanged = carYearChanged;
    }
}
