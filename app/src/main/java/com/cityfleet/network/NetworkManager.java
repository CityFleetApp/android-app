package com.cityfleet.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.cityfleet.R;
import com.cityfleet.util.PrefUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    private NetworkService networkClient;
    private final ConnectivityManager connectivityManager;

    public NetworkManager(final Context context, ConnectivityManager mConnectivityManager) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request;
                if (!TextUtils.isEmpty(PrefUtil.getToken(context))) {
                    request = chain.request().newBuilder().addHeader("Authorization", context.getString(R.string.token_header) + PrefUtil.getToken(context)).
                            addHeader("User-Agent", context.getString(R.string.app_name)).build();
                    Log.d(NetworkManager.class.getName(), "Token " + PrefUtil.getToken(context));
                } else {
                    request = chain.request();
                }
                return chain.proceed(request);
            }
        };
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(loggingInterceptor).addInterceptor(headerInterceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.endpoint))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        networkClient = retrofit.create(NetworkService.class);
        this.connectivityManager = mConnectivityManager;
    }

    public NetworkService getNetworkClient() {
        return networkClient;
    }


    public boolean isConnectedOrConnecting() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
