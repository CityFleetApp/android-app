package com.citifleet.view.main.marketplace;

import android.util.Log;

import com.citifleet.model.GeneralGood;
import com.citifleet.model.JobOffer;
import com.citifleet.model.Notification;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by vika on 29.03.16.
 */
public class JobOffersPresenter {
    private NetworkManager networkManager;
    private JobOffersView view;
    private List<JobOffer> jobOfferList;
    private List<JobOffer> availableJobOfferList;

    public JobOffersPresenter(NetworkManager networkManager, JobOffersView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadJobOffersList() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<JobOffer>> call = networkManager.getNetworkClient().getOffers();
            call.enqueue(jobsCallback);
        } else {
            view.stopLoading();
            view.onNetworkError();
        }
    }

    public void onAllJobOffersBtnClicked() {
        if (jobOfferList != null) {
            view.onListLoaded(jobOfferList);
        }
    }

    public void onAvailableJobOffersBtnClicked() {
        if (jobOfferList != null) {
            availableJobOfferList = new ArrayList<JobOffer>();
            for (JobOffer jobOffer : jobOfferList) {
//                if (n.isUnseen()) {
//                    unreadNotifications.add(n);
//                }
            }
            view.onListLoaded(availableJobOfferList);
        }
    }

    private Callback<List<JobOffer>> jobsCallback = new Callback<List<JobOffer>>() {

        @Override
        public void onResponse(Call<List<JobOffer>> call, retrofit2.Response<List<JobOffer>> response) {
            view.stopLoading();
            if (!response.isSuccessful()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            } else {
                jobOfferList = response.body();
                view.onListLoaded(jobOfferList);
            }
        }

        @Override
        public void onFailure(Call<List<JobOffer>> call, Throwable t) {
            Log.e(JobOffersPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface JobOffersView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onListLoaded(List<JobOffer> jobOfferList);
    }
}
