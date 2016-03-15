package com.citifleet.view;

import android.support.v4.app.Fragment;

import com.citifleet.CitiFleetApp;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by vika on 15.03.16.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = CitiFleetApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
