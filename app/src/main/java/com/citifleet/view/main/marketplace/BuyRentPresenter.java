package com.citifleet.view.main.marketplace;

import android.util.Log;

import com.citifleet.model.Car;
import com.citifleet.model.PostingType;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by vika on 23.03.16.
 */
public class BuyRentPresenter {
    private NetworkManager networkManager;
    private BuyRentView view;

    public BuyRentPresenter(NetworkManager networkManager, BuyRentView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadCarList(PostingType type) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<Car>> call;
            if (type == PostingType.RENT) {
                call = networkManager.getNetworkClient().getCarsForRent();
            } else {
                call = networkManager.getNetworkClient().getCarsForSale();
            }
            call.enqueue(carCallback);
        } else {
            view.stopLoading();
            view.onNetworkError();
        }
    }

    private Callback<List<Car>> carCallback = new Callback<List<Car>>() {

        @Override
        public void onResponse(Call<List<Car>> call, retrofit2.Response<List<Car>> response) {
            view.stopLoading();
            if (!response.isSuccess()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            } else {
                view.onListLoaded(response.body());
            }
        }

        @Override
        public void onFailure(Call<List<Car>> call, Throwable t) {
            Log.e(BuyRentPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface BuyRentView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onListLoaded(List<Car> carList);
    }
}
