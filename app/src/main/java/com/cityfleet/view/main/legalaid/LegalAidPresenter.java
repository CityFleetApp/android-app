package com.cityfleet.view.main.legalaid;

import android.util.Log;

import com.cityfleet.model.LegalAidLocation;
import com.cityfleet.model.LegalAidPerson;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.LegalAidType;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 17.03.16.
 */
public class LegalAidPresenter {
    private LegalAidDetailView view;
    private NetworkManager networkManager;

    public LegalAidPresenter(LegalAidDetailView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }

    public void loadLocations() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<LegalAidLocation>> locationCall = networkManager.getNetworkClient().getLegalAidLocations();
            locationCall.enqueue(locationsCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void loadPersons(LegalAidType type, int locationId) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<LegalAidPerson>> personCall = getPersonCallByType(type, locationId);
            personCall.enqueue(personCallback);
        } else {
            view.onNetworkError();
        }
    }

    private Call<List<LegalAidPerson>> getPersonCallByType(LegalAidType type, int locationId) {
        switch (type) {
            case DMV_LAWYERS:
                return networkManager.getNetworkClient().getLegalAidDmvLawyers(locationId);
            case TLC_LAWYERS:
                return networkManager.getNetworkClient().getLegalAidTlcLawyers(locationId);
            case ACCOUNTANTS:
                return networkManager.getNetworkClient().getLegalAidAccounting(locationId);
            case BROKERS:
                return networkManager.getNetworkClient().getLegalAidInsurance(locationId);
        }
        return null;
    }

    Callback<List<LegalAidLocation>> locationsCallback = new Callback<List<LegalAidLocation>>() {
        @Override
        public void onResponse(Call<List<LegalAidLocation>> call, Response<List<LegalAidLocation>> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onLocationLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<LegalAidLocation>> call, Throwable t) {
            Log.e(LegalAidPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    Callback<List<LegalAidPerson>> personCallback = new Callback<List<LegalAidPerson>>() {
        @Override
        public void onResponse(Call<List<LegalAidPerson>> call, Response<List<LegalAidPerson>> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onPersonsLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<LegalAidPerson>> call, Throwable t) {
            Log.e(LegalAidPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface LegalAidDetailView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onLocationLoaded(List<LegalAidLocation> locations);

        void onPersonsLoaded(List<LegalAidPerson> persons);
    }

}
