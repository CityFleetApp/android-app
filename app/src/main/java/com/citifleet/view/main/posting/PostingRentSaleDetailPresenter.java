package com.citifleet.view.main.posting;

import android.util.Log;

import com.citifleet.model.CarOption;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 24.03.16.
 */
public class PostingRentSaleDetailPresenter {
    private NetworkManager networkManager;
    private PostingRentSaleDetailView view;

    public PostingRentSaleDetailPresenter(NetworkManager networkManager, PostingRentSaleDetailView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void init() {
        if (networkManager.isConnectedOrConnecting()) {
            Call<List<CarOption>> makesCall = networkManager.getNetworkClient().getCarMakes();
            makesCall.enqueue(makeCallback);
            Call<List<CarOption>> seatsCall = networkManager.getNetworkClient().getCarSeats();
            seatsCall.enqueue(seatsCallback);
            Call<List<CarOption>> fuelCall = networkManager.getNetworkClient().getCarFuels();
            fuelCall.enqueue(fuelCallback);
            Call<List<CarOption>> colorCall = networkManager.getNetworkClient().getCarColors();
            colorCall.enqueue(colorCallback);
            Call<List<CarOption>> typesCall = networkManager.getNetworkClient().getCarTypes();
            typesCall.enqueue(typesCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void getCarModelsByMakeId(int makeId) {
        if (networkManager.isConnectedOrConnecting()) {
            Call<List<CarOption>> modelCall = networkManager.getNetworkClient().getCardModels(makeId);
            modelCall.enqueue(modelCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<List<CarOption>> makeCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccess()) {
                view.onMakesLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    Callback<List<CarOption>> modelCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccess()) {
                view.onModelLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    Callback<List<CarOption>> seatsCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccess()) {
                view.onSeatsLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    Callback<List<CarOption>> fuelCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccess()) {
                view.onFuelLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    Callback<List<CarOption>> colorCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccess()) {
                view.onColorLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    Callback<List<CarOption>> typesCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccess()) {
                view.onTypeLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };

    public interface PostingRentSaleDetailView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onMakesLoaded(List<CarOption> makeList);

        void onModelLoaded(List<CarOption> modelList);

        void onTypeLoaded(List<CarOption> typeList);

        void onFuelLoaded(List<CarOption> fuelList);

        void onColorLoaded(List<CarOption> colorList);

        void onSeatsLoaded(List<CarOption> seatsList);

    }
}
