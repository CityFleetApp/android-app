package com.cityfleet.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.util.Constants;
import com.cityfleet.util.GcmRegistrationTypes;
import com.cityfleet.util.PrefUtil;
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
        String userToken = intent.getStringExtra(Constants.PREFS_TOKEN);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String gcmToken = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + gcmToken);

            switch (type) {
                case REGISTER:
                    registerForGcm(gcmToken);
                    subscribeTopics(gcmToken);
                    PrefUtil.setPrefGcmTokenSent(this, true);
                    break;
                case UNREGISTER:
                    unregisterFromGcm(gcmToken, userToken);
                    PrefUtil.setPrefGcmTokenSent(this, false);
                    break;
                case UPDATE:
                    updateToken(gcmToken);
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
        Call<Void> call = CityFleetApp.getInstance().getNetworkManager().getNetworkClient().registerForGcm(token, deviceId, true);
        call.execute();
    }

    private void unregisterFromGcm(String gcmToken, String userToken) throws IOException {
        Call<Void> call = CityFleetApp.getInstance().getNetworkManager().getNetworkClient().unregisterFromGcm(getString(R.string.token_header) + userToken, gcmToken);
        call.execute();
    }

    private void updateToken(String token) throws IOException {
        Call<Void> call = CityFleetApp.getInstance().getNetworkManager().getNetworkClient().updateToken(token);
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
