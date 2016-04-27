package com.cityfleet.view.main.marketplace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cityfleet.model.CarPostingType;
import com.cityfleet.util.Constants;

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
        Fragment fragment = new BuyRentDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.POSTING_TYPE_TAG, position == 0 ? CarPostingType.SALE : CarPostingType.RENT);
        fragment.setArguments(bundle);
        return fragment;
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