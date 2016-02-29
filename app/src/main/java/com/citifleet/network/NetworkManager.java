package com.citifleet.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.citifleet.R;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    private       NetworkService      networkClient;
    private final ConnectivityManager connectivityManager;

    public NetworkManager(final Context context, ConnectivityManager mConnectivityManager) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
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

    public void setNetworkClient(NetworkService networkClient) {
        this.networkClient = networkClient;
    }

    public boolean isConnectedOrConnecting() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
