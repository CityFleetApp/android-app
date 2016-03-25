package com.citifleet.view.main.benefits;

import android.util.Log;

import com.citifleet.CitiFleetApp;
import com.citifleet.model.Benefit;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 12.03.16.
 */
public class BenefitPresenter {
    private NetworkManager networkManager;
    private BenefitView view;

    public BenefitPresenter(NetworkManager networkManager, BenefitView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadBenefits() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<Benefit>> call = CitiFleetApp.getInstance().getNetworkManager().getNetworkClient().getBenefits();
            call.enqueue(callback);
        } else {
            view.stopLoading();
            view.onNetworkError();
        }
    }

    Callback<List<Benefit>> callback = new Callback<List<Benefit>>() {
        @Override
        public void onResponse(Call<List<Benefit>> call, Response<List<Benefit>> response) {
            view.stopLoading();
            if (!response.isSuccessful()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            } else {
                view.updateBenefitsList(response.body());
            }
        }

        @Override
        public void onFailure(Call<List<Benefit>> call, Throwable t) {
            Log.e(Benefit.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface BenefitView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void updateBenefitsList(List<Benefit> benefits);
    }
}
