package com.citifleet.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.util.Constants;
import com.citifleet.util.GcmRegistrationTypes;
import com.citifleet.util.PrefUtil;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by vika on 21.03.16.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GcmRegistrationTypes type = (GcmRegistrationTypes) intent.getSerializableExtra(Constants.GCM_REGISTRATION_TYPE_TAG);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + token);

            switch (type) {
                case REGISTER:
                    registerForGcm(token);
                    subscribeTopics(token);
                    PrefUtil.setPrefGcmTokenSent(this, true);
                    break;
                case UNREGISTER:
                    unregisterFromGcm(token);
                    PrefUtil.setPrefGcmTokenSent(this, false);
                    break;
                case UPDATE:
                    updateToken(token);
                    PrefUtil.setPrefGcmTokenSent(this, true);
                    break;
            }

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            PrefUtil.setPrefGcmTokenSent(this, false);
        }
    }

    private void registerForGcm(String token) throws IOException {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Call<Void> call = CitiFleetApp.getInstance().getNetworkManager().getNetworkClient().registerForGcm(token, deviceId);
        call.execute();
    }

    private void unregisterFromGcm(String token) throws IOException {
        Call<Void> call = CitiFleetApp.getInstance().getNetworkManager().getNetworkClient().unregisterFromGcm(token);
        call.execute();
    }

    private void updateToken(String token) throws IOException {
        Call<Void> call = CitiFleetApp.getInstance().getNetworkManager().getNetworkClient().updateToken(token);
        call.execute();
    }

    private void subscribeTopics(String token) throws IOException {
        //TODO
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}
