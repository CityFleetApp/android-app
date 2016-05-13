package com.cityfleet.util;

/**
 * Created by vika on 13.05.16.
 */
public class MarketplaceSearchEvent {
    private String search;

    public MarketplaceSearchEvent(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

}
