package com.citifleet.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {
    public static String getToken(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(Constants.PREFS_TOKEN, null);
    }

    public static void setToken(Context context, String token) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().putString(Constants.PREFS_TOKEN, token).commit();
    }
}
