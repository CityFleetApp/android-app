package com.cityfleet.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.cityfleet.R;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.main.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginFlowActivity extends BaseActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        checkPlayServices();
    }

    @Override
    protected Fragment getInitFragment() {
        return new MainLoginSignupFragment();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d(LoginFlowActivity.class.getName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void startMainScreen() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
