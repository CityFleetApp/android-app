package com.cityfleet.model;

import com.cityfleet.R;

/**
 * Created by vika on 05.04.16.
 */
public enum ReportType {
    POLICE(R.drawable.ic_police_pin, R.drawable.ic_policeman, R.string.police),
    TLC(R.drawable.ic_tlc_pin, R.drawable.ic_police, R.string.tlc),
    ACCIDENT(R.drawable.ic_accident_pin, R.drawable.ic_crash, R.string.accident),
    TRAFIC_JAM(R.drawable.ic_traffic_pin, R.drawable.ic_traffic, R.string.trafic_jam),
    HAZARD(R.drawable.ic_hazard_pin, R.drawable.ic_alert, R.string.hazard),
    ROAD_CLOSURE(R.drawable.ic_road_closure_pin, R.drawable.ic_stop, R.string.road_closure);
    private int pinIconResId;
    private int smallIconResId;
    private int nameRes;

    ReportType(int pinIconResId, int smallIconResId, int name) {
        this.pinIconResId = pinIconResId;
        this.smallIconResId = smallIconResId;
        this.nameRes = name;
    }

    public int getNameRes() {
        return nameRes;
    }

    public int getSmallIconResId() {
        return smallIconResId;
    }

    public int getPinIconResId() {
        return pinIconResId;
    }

}
