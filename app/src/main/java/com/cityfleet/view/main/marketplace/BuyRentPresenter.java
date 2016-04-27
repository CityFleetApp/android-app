package com.cityfleet.view.main.marketplace;

import android.util.Log;

import com.cityfleet.model.Car;
import com.cityfleet.model.CarPostingType;
import com.cityfleet.model.PagesResult;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by vika on 23.03.16.
 */
public class BuyRentPresenter {
    private NetworkManager networkManager;
    private BuyRentView view;
    private int totalCount;

    public BuyRentPresenter(NetworkManager networkManager, BuyRentView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadCarList(CarPostingType type, int currentTotalCount, int page) {
        if (currentTotalCount < totalCount) {
            if (networkManager.isConnectedOrConnecting()) {
                view.startLoading();
                Call<PagesResult<Car>> call;
                if (type == CarPostingType.RENT) {
                    call = networkManager.getNetworkClient().getCarsForRent(page);
                } else {
                    call = networkManager.getNetworkClient().getCarsForSale(page);
                }
                call.enqueue(carCallback);
            } else {
                view.stopLoading();
                view.onNetworkError();
            }
        }
    }

    private Callback<PagesResult<Car>> carCallback = new Callback<PagesResult<Car>>() {

        @Override
        public void onResponse(Call<PagesResult<Car>> call, retrofit2.Response<PagesResult<Car>> response) {
            view.stopLoading();
            if (!response.isSuccessful()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            } else {
                totalCount = response.body().getCount();
                view.onListLoaded(response.body().getResults());
            }
        }

        @Override
        public void onFailure(Call<PagesResult<Car>> call, Throwable t) {
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
