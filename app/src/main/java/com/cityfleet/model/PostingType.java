package com.cityfleet.model;

/**
 * Created by vika on 01.04.16.
 */
public enum PostingType {
    RENT_SALE_CAR("car"), GENERAL_GOOD("goods"), JOB_OFFER("offer");
    private String apiName;

    PostingType(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }
}
