package com.citifleet.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.citifleet.R;
import com.citifleet.util.PrefUtil;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.main.MainActivity;

public class LoginFlowActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(PrefUtil.getToken(this))) {
            startMainScreen();
        } else {
            setContentView(R.layout.activity_base);
            changeFragment(new SplashScreenFragment(), false);
        }
    }

    public void startMainScreen() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected Fragment getInitFragment() {
        return null;
    }
}
