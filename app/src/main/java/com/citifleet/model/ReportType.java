package com.citifleet.model;

import com.citifleet.R;

/**
 * Created by vika on 05.04.16.
 */
public enum ReportType {
    POLICE(R.drawable.ic_police_pin, R.drawable.ic_policeman), TLC(R.drawable.ic_tlc_pin, R.drawable.ic_police), ACCIDENT(R.drawable.ic_accident_pin, R.drawable.ic_crash),
    TRAFIC_JAM(R.drawable.ic_traffic_pin, R.drawable.ic_traffic), HAZARD(R.drawable.ic_hazard_pin, R.drawable.ic_alert),
    ROAD_CLOSURE(R.drawable.ic_road_closure_pin, R.drawable.ic_stop);
    private int pinIconResId;
    private int smallIconResId;

    ReportType(int pinIconResId, int smallIconResId) {
        this.pinIconResId = pinIconResId;
        this.smallIconResId = smallIconResId;
    }

    public int getSmallIconResId() {
        return smallIconResId;
    }

    public int getPinIconResId() {
        return pinIconResId;
    }

}
