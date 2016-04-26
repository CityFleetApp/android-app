package com.citifleet.view.main.marketplace;

import android.util.Log;

import com.citifleet.model.JobOffer;
import com.citifleet.model.PagesResult;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.util.Constants;

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
    private int totalCount;

    public JobOffersPresenter(NetworkManager networkManager, JobOffersView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void loadJobOffersList(int currentTotalCount, int page, boolean isAllSelected) {
        if (currentTotalCount < totalCount) {
            if (networkManager.isConnectedOrConnecting()) {
                view.startLoading();
                String isAvailable = isAllSelected ? null : "1";
                Call<PagesResult<JobOffer>> call = networkManager.getNetworkClient().getOffers(page, isAvailable);
                call.enqueue(jobsCallback);
            } else {
                view.stopLoading();
                view.onNetworkError();
            }
        }
    }

    public void onAllJobOffersBtnClicked() {
        totalCount = 0;
        view.clearList();
        view.resetScrollListener();
        loadJobOffersList(Constants.DEFAULT_UNSELECTED_POSITION, 1, true);
    }

    public void onAvailableJobOffersBtnClicked() {
        totalCount = 0;
        view.clearList();
        view.resetScrollListener();
        loadJobOffersList(Constants.DEFAULT_UNSELECTED_POSITION, 1, false);
    }


    private Callback<PagesResult<JobOffer>> jobsCallback = new Callback<PagesResult<JobOffer>>() {

        @Override
        public void onResponse(Call<PagesResult<JobOffer>> call, retrofit2.Response<PagesResult<JobOffer>> response) {
            view.stopLoading();
            if (!response.isSuccessful()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            } else {
                jobOfferList = response.body().getResults();
                totalCount = response.body().getCount();
                view.onListLoaded(jobOfferList);
                view.setAvailableJobsCount(response.body().getAvailable());
            }
        }

        @Override
        public void onFailure(Call<PagesResult<JobOffer>> call, Throwable t) {
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

        void clearList();

        void onListLoaded(List<JobOffer> jobOfferList);

        void setAvailableJobsCount(int count);

        void resetScrollListener();
    }
}
