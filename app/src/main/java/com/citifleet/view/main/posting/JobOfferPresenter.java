package com.citifleet.view.main.posting;

import android.util.Log;

import com.citifleet.model.CarOption;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

/**
 * Created by vika on 28.03.16.
 */
public class JobOfferPresenter {
    private NetworkManager networkManager;
    private PostingJobOfferView view;

    public JobOfferPresenter(NetworkManager networkManager, PostingJobOfferView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadVehicleAndJobTypes() {
        if (networkManager.isConnectedOrConnecting()) {
            Call<List<CarOption>> jobTypes = networkManager.getNetworkClient().getJobTypes();
            jobTypes.enqueue(jobTypesListener);
            Call<List<CarOption>> vehicleTypes = networkManager.getNetworkClient().getVehicleTypes();
            vehicleTypes.enqueue(vehiclesTypeListener);
        } else {
            view.onNetworkError();
        }
    }

    public void postJobOffer(String datetime, String pickupAddress, String destination, double fare, double gratuity, int vehicleType, boolean suite,
                             int jobType, String instructions) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<Void> postingCall = networkManager.getNetworkClient().postJobOffer(datetime, pickupAddress, destination, fare, gratuity, vehicleType, suite, jobType, instructions);
            postingCall.enqueue(postingListener);
        } else {
            view.onNetworkError();
        }
    }

    private Callback<Void> postingListener = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onPostCreatesSuccessfully();
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.e(JobOfferPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> jobTypesListener = new Callback<List<CarOption>>() {

        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onJobTypesLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(JobOfferPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };

    private Callback<List<CarOption>> vehiclesTypeListener = new Callback<List<CarOption>>() {

        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onVehicleTypesLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(JobOfferPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };

    public interface PostingJobOfferView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onVehicleTypesLoaded(List<CarOption> carOptions);

        void onJobTypesLoaded(List<CarOption> carOptions);

        void onPostCreatesSuccessfully();
    }
}
