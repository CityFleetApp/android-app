package com.citifleet;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import com.citifleet.network.NetworkManager;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class CitiFleetApp extends Application {
    private        NetworkManager networkManager;
    private static CitiFleetApp   instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
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

    public static CitiFleetApp getInstance() {
        return instance;
    }
}
