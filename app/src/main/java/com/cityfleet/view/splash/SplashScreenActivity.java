package com.cityfleet.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.cityfleet.R;
import com.cityfleet.gcm.RegistrationIntentService;
import com.cityfleet.util.Constants;
import com.cityfleet.util.GcmRegistrationTypes;
import com.cityfleet.util.PrefUtil;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.login.LoginFlowActivity;
import com.cityfleet.view.main.MainActivity;

/**
 * Created by vika on 18.05.16.
 */
public class SplashScreenActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected Fragment getInitFragment() {
        return new SplashScreenFragment();
    }

    protected void startAppAfterSplashScreen() {
        if (!TextUtils.isEmpty(PrefUtil.getToken(this))) {
            registerForGcmIfNeeded();
            startMainScreen();
        } else {
            startLoginScreen();
        }
    }

    private void registerForGcmIfNeeded() {
        if (!PrefUtil.getPrefIsGcmTokenSent(this)) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putExtra(Constants.GCM_REGISTRATION_TYPE_TAG, GcmRegistrationTypes.REGISTER);
            startService(intent);
        }
    }

    public void startMainScreen() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void startLoginScreen() {
        Intent i = new Intent(this, LoginFlowActivity.class);
        startActivity(i);
        finish();
    }
}
