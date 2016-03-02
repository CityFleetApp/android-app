package com.citifleet.view.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.citifleet.R;
import com.citifleet.view.BaseActivity;

public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected Fragment getInitFragment() {
        return new MainMapFragment();
    }
}
