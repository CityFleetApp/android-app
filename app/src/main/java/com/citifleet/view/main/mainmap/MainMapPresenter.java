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

    public void confirmDenyReport(int reportId, boolean isConfirmReport) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<Void> call;
            if (isConfirmReport) {
                call = networkManager.getNetworkClient().confirmReport(reportId);
            } else {
                call = networkManager.getNetworkClient().denyReport(reportId);
            }
            call.enqueue(confirmDenyReportCallback);
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

    private Callback<Void> confirmDenyReportCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            view.stopLoading();
            if (!response.isSuccess()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<Report>> nearbyReportsCallback = new Callback<List<Report>>() {
        @Override
        public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.onReportsNearbyLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<Report>> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    private Callback<Object> reportCallback = new Callback<Object>() {
        @Override
        public void onResponse(Call<Object> call, Response<Object> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.onPostReportSuccess();
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Object> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface MainMapView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onPostReportSuccess();

        void onNetworkError();

        void onReportsNearbyLoaded(List<Report> reportList);
    }
}
