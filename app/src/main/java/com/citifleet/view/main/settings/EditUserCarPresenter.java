package com.citifleet.view.main.settings;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.citifleet.model.CarOption;
import com.citifleet.model.PostingType;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.util.ScaleImageHelper;

import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 31.03.16.
 */
public class EditUserCarPresenter {
    private NetworkManager networkManager;
    private EditUserCarView view;

    public EditUserCarPresenter(EditUserCarView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }

    public void init() {
        if (networkManager.isConnectedOrConnecting()) {
            Call<List<CarOption>> makesCall = networkManager.getNetworkClient().getCarMakes();
            makesCall.enqueue(makeCallback);
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

    private Callback<List<CarOption>> makeCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onMakesLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(EditUserCarPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> modelCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onModelLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(EditUserCarPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };

    private Callback<List<CarOption>> colorCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onColorLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(EditUserCarPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> typesCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onTypeLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(EditUserCarPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };

    public interface EditUserCarView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onMakesLoaded(List<CarOption> makeList);

        void onModelLoaded(List<CarOption> modelList);

        void onTypeLoaded(List<CarOption> typeList);

        void onColorLoaded(List<CarOption> colorList);

    }
}
