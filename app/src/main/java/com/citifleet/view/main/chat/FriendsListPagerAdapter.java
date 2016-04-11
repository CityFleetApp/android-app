package com.citifleet.view.main.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by vika on 11.04.16.
 */
public class FriendsListPagerAdapter extends FragmentPagerAdapter {
    private String[] titles;

    public FriendsListPagerAdapter(FragmentManager manager, String[] titles) {
        super(manager);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return new FriendsListFragment();
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
