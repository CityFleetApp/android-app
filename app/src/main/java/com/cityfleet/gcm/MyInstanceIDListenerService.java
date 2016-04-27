package com.cityfleet.gcm;

import android.content.Intent;

import com.cityfleet.util.Constants;
import com.cityfleet.util.GcmRegistrationTypes;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by vika on 21.03.16.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra(Constants.GCM_REGISTRATION_TYPE_TAG, GcmRegistrationTypes.UPDATE);
        startService(intent);
    }
}
