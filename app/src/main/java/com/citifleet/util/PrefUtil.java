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
//    public static String getGcmToken(Context context) {
//        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
//        return settings.getString(Constants.PREFS_GCM_TOKEN, null);
//    }
//
//    public static void setGcmToken(Context context, String token) {
//        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
//        settings.edit().putString(Constants.PREFS_GCM_TOKEN, token).commit();
//    }
    public static boolean getPrefIsGcmTokenSent(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(Constants.PREFS_SEND_TOKEN_TO_SERVER, false);
    }

    public static void setPrefGcmTokenSent(Context context, boolean isSent) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().putBoolean(Constants.PREFS_SEND_TOKEN_TO_SERVER, isSent).commit();
    }

    public static void clearAllPrefs(Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }
}
