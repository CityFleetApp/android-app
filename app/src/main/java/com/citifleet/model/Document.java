package com.citifleet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vika on 22.03.16.
 */
public class Document {
    private int id;
    private String file;
    @SerializedName("expiry_date")
    private String expiryDate;
    @SerializedName("plate_number")
    private String plateNumber;
    @SerializedName("document_type")
    private int documentType;

    @Expose
    private boolean isImageUpdated = false;
    @Expose
    private boolean isExpiryDateUpdated = false;
    @Expose
    private boolean isPlateNumberUpdated = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public boolean isImageUpdated() {
        return isImageUpdated;
    }

    public void setImageUpdated(boolean imageUpdated) {
        isImageUpdated = imageUpdated;
    }

    public boolean isExpiryDateUpdated() {
        return isExpiryDateUpdated;
    }

    public void setExpiryDateUpdated(boolean expiryDateUpdated) {
        isExpiryDateUpdated = expiryDateUpdated;
    }

    public boolean isPlateNumberUpdated() {
        return isPlateNumberUpdated;
    }

    public void setPlateNumberUpdated(boolean plateNumberUpdated) {
        isPlateNumberUpdated = plateNumberUpdated;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getDocumentType() {
        return documentType;
    }

    public void setDocumentType(int documentType) {
        this.documentType = documentType;
    }
}
