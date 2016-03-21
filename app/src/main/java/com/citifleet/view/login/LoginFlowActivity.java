package com.citifleet.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.citifleet.R;
import com.citifleet.gcm.RegistrationIntentService;
import com.citifleet.util.Constants;
import com.citifleet.util.GcmRegistrationTypes;
import com.citifleet.util.PrefUtil;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.main.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginFlowActivity extends BaseActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(PrefUtil.getToken(this))) {
            registerForGcmIfNeeded();
            startMainScreen();
        } else {
            setContentView(R.layout.activity_base);
            changeFragment(new SplashScreenFragment(), false);
            checkPlayServices();
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

    @Override
    protected Fragment getInitFragment() {
        return null;
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

}
