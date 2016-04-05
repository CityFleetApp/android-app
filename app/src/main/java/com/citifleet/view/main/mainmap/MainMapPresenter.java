package com.citifleet.view.main.mainmap;

import android.util.Log;

import com.citifleet.model.Report;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.view.login.LoginPresenter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMapPresenter {
    private NetworkManager networkManager;
    private MainMapView view;

    public MainMapPresenter(NetworkManager networkManager, MainMapView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadReportsNearby(double lat, double longt) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<Report>> reportCall = networkManager.getNetworkClient().getReportsNearby(lat, longt);
            reportCall.enqueue(nearbyReportsCallback);
        } else {
            view.onNetworkError();
        }
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

    Callback<List<Report>> nearbyReportsCallback = new Callback<List<Report>>() {
        @Override
        public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onReportsNearbyLoaded(response.body());
            } else {
                view.onPostReportFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<Report>> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onPostReportFailure(t.getMessage());
        }
    };
    Callback<Object> reportCallback = new Callback<Object>() {
        @Override
        public void onResponse(Call<Object> call, Response<Object> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onPostReportSuccess();
            } else {
                view.onPostReportFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Object> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onPostReportFailure(t.getMessage());
        }
    };

    public interface MainMapView {
        void startLoading();

        void stopLoading();

        void onPostReportFailure(String error);

        void onPostReportSuccess();

        void onNetworkError();

        void onReportsNearbyLoaded(List<Report> reportList);
    }
}
