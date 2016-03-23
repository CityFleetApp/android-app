package com.citifleet.view.main.marketplace;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by vika on 23.03.16.
 */
public class RentSalePagerAdapter extends FragmentPagerAdapter {
    private String[] titles;

    public RentSalePagerAdapter(FragmentManager manager, String[] titles) {
        super(manager);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return new MarketPlaceBuyRentFragment();
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}