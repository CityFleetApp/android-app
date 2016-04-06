package com.citifleet.view.main.marketplace;

import android.util.Log;

import com.citifleet.model.GeneralGood;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by vika on 28.03.16.
 */
public class GeneralGoodsPresenter {
    private NetworkManager networkManager;
    private GeneralGoodsView view;

    public GeneralGoodsPresenter(NetworkManager networkManager, GeneralGoodsView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadGeneralGoodsList() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<GeneralGood>> call = networkManager.getNetworkClient().getGoods();
            call.enqueue(goodsCallback);
        } else {
            view.stopLoading();
            view.onNetworkError();
        }
    }

    private Callback<List<GeneralGood>> goodsCallback = new Callback<List<GeneralGood>>() {

        @Override
        public void onResponse(Call<List<GeneralGood>> call, retrofit2.Response<List<GeneralGood>> response) {
            view.stopLoading();
            if (!response.isSuccess()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            } else {
                view.onListLoaded(response.body());
            }
        }

        @Override
        public void onFailure(Call<List<GeneralGood>> call, Throwable t) {
            Log.e(BuyRentPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface GeneralGoodsView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onListLoaded(List<GeneralGood> generalGoodList);
    }
}
