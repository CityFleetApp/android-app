package com.citifleet.model;

import com.citifleet.R;

/**
 * Created by vika on 05.04.16.
 */
public enum ReportType {
    POLICE(R.drawable.ic_police_pin), TLC(R.drawable.ic_tlc_pin), ACCIDENT(R.drawable.ic_accident_pin),
    TRAFIC_JAM(R.drawable.ic_traffic_pin), HAZARD(R.drawable.ic_hazard_pin), ROAD_CLOSURE(R.drawable.ic_road_closure_pin);
    private int pinIconResId;

    ReportType( int pinIconResId) {
        this.pinIconResId = pinIconResId;
    }


    public int getPinIconResId() {
        return pinIconResId;
    }

}
