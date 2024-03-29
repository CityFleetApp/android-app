package com.cityfleet;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import com.cityfleet.network.NetworkManager;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class CityFleetApp extends Application {

    private NetworkManager networkManager;
    private static CityFleetApp instance;
    private boolean isChatFragmentRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret));
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontRegular))
                .setFontAttrId(R.attr.fontPath)
                .build());
        instance = this;
    }

    public NetworkManager getNetworkManager() {
        if (networkManager == null) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkManager = new NetworkManager(getApplicationContext(), manager);
        }
        return networkManager;
    }

    public static CityFleetApp getInstance() {
        return instance;
    }

    public boolean isChatFragmentRunning() {
        return isChatFragmentRunning;
    }


}
