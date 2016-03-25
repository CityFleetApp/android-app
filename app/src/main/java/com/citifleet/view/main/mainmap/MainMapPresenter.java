package com.citifleet.view.main.mainmap;

import android.util.Log;

import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.view.login.LoginPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMapPresenter {
    private NetworkManager networkManager;
    private MainMapView    view;

    public MainMapPresenter(NetworkManager networkManager, MainMapView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void sendReport(int reportType, double lat, double longt) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<Object> reportCall = networkManager.getNetworkClient().report(reportType, lat, longt);
            reportCall.enqueue(reportCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<Object> reportCallback = new Callback<Object>() {
        @Override
        public void onResponse(Call<Object> call, Response<Object> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onReportSuccess();
            } else {
                view.onReportFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Object> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onReportFailure(t.getMessage());
        }
    };

    public interface MainMapView {
        void startLoading();

        void stopLoading();

        void onReportFailure(String error);

        void onReportSuccess();

        void onNetworkError();
    }
}
