package com.citifleet;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import com.citifleet.network.NetworkManager;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class CitiFleetApp extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "9neWixCKXdBBR3hLYCq8BAxS8";
    private static final String TWITTER_SECRET = "JLZSfqPXiiNCMOmHzU3eK85DD0VqmoUkmVNNp92LSyx9GPyGRS";

    private NetworkManager networkManager;
    private static CitiFleetApp instance;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontRegular))
                .setFontAttrId(R.attr.fontPath)
                .build());
        instance = this;
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
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
