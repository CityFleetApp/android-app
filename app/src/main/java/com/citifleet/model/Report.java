package com.citifleet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vika on 05.04.16.
 */
public class Report {
    private int id;
    @SerializedName("report_type")
    private int reportType;
    private double lat;
    private double lng;

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
