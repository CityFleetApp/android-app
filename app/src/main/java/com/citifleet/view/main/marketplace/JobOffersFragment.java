package com.citifleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.JobOffer;
import com.citifleet.util.Constants;
import com.citifleet.util.DividerItemDecoration;
import com.citifleet.view.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 28.03.16.
 */
public class JobOffersFragment extends BaseFragment implements JobOffersPresenter.JobOffersView, JobOffersAdapter.OnItemClickListener {
    @Bind(R.id.jobsListView)
    RecyclerView jobsListView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.allBtnLbl)
    TextView allBtnLbl;
    @Bind(R.id.availableBtnLbl)
    TextView availableBtnLbl;
    private JobOffersAdapter adapter;
    private JobOffersPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_job_offers, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.job_offers));
        jobsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new JobOffersAdapter(getContext(), this);
        jobsListView.setAdapter(adapter);
        jobsListView.addItemDecoration(new DividerItemDecoration(getContext()));
        presenter = new JobOffersPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadJobOffersList();
        toggleSelectionOfFilters(true);
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListLoaded(List<JobOffer> jobOfferList) {
        adapter.setList(jobOfferList);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.allBtn)
    void onAllBtnClick() {
        presenter.onAllJobOffersBtnClicked();
        toggleSelectionOfFilters(true);
    }

    @OnClick(R.id.availableBtn)
    void unreadBtnClick() {
        presenter.onAvailableJobOffersBtnClicked();
        toggleSelectionOfFilters(false);
    }

    private void toggleSelectionOfFilters(boolean isAllShown) {
        allBtnLbl.setAlpha(isAllShown ? Constants.ENABLED_LAYOUT_ALPHA : Constants.DISABLED_LAYOUT_ALPHA);
        availableBtnLbl.setAlpha(isAllShown ? Constants.DISABLED_LAYOUT_ALPHA : Constants.ENABLED_LAYOUT_ALPHA);
    }

    @Override
    public void onItemClick(JobOffer item) {

    }
}
