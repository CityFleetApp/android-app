package com.citifleet.view.main.marketplace;

import android.util.Log;

import com.citifleet.model.GeneralGood;
import com.citifleet.model.PagesResult;
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
    private int totalCount;

    public GeneralGoodsPresenter(NetworkManager networkManager, GeneralGoodsView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadGeneralGoodsList(int currentTotalCount, int page) {
        if (currentTotalCount < totalCount) {
            if (networkManager.isConnectedOrConnecting()) {
                view.startLoading();
                Call<PagesResult<GeneralGood>> call = networkManager.getNetworkClient().getGoods(page);
                call.enqueue(goodsCallback);
            } else {
                view.stopLoading();
                view.onNetworkError();
            }
        }
    }

    private Callback<PagesResult<GeneralGood>> goodsCallback = new Callback<PagesResult<GeneralGood>>() {

        @Override
        public void onResponse(Call<PagesResult<GeneralGood>> call, retrofit2.Response<PagesResult<GeneralGood>> response) {
            view.stopLoading();
            if (!response.isSuccessful()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            } else {
                totalCount = response.body().getCount();
                view.onListLoaded(response.body().getResults());
            }
        }

        @Override
        public void onFailure(Call<PagesResult<GeneralGood>> call, Throwable t) {
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
