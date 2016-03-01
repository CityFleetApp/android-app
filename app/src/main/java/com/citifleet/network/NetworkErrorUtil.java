package com.citifleet.network;

import android.util.Log;

import com.citifleet.model.LoginInfo;

import org.json.JSONObject;

import java.util.Iterator;

import retrofit2.Response;

public class NetworkErrorUtil {
    public static String gerErrorMessage(Response<LoginInfo> response) {
        String errorMes = null;
        try {
            String jsonError = response.errorBody().string();
            Log.e(NetworkErrorUtil.class.getName(), jsonError);
            JSONObject object = new JSONObject(jsonError);
            Iterator<String> keys = object.keys();
            if (keys.hasNext()) {
                String key = (String) keys.next();
                errorMes = (String) object.getJSONArray(key).get(0);
            }
        } catch (Exception e) {
            Log.e(NetworkErrorUtil.class.getName(), e.getMessage());
        }
        return errorMes;
    }
}
