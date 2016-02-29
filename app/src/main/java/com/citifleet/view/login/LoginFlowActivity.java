package com.citifleet.view.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.citifleet.R;
import com.citifleet.view.BaseActivity;

public class LoginFlowActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected Fragment getInitFragment() {
        return new SplashScreenFragment();
    }
}
