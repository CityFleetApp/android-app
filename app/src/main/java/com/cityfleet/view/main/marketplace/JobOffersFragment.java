package com.cityfleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.JobOffer;
import com.cityfleet.util.Constants;
import com.cityfleet.util.DividerItemDecoration;
import com.cityfleet.util.EndlessRecyclerOnScrollListener;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import org.parceler.Parcels;

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
    @Bind(R.id.emptyView)
    TextView emptyView;
    @Bind(R.id.closeBtn)
    ImageButton closeBtn;
    private JobOffersAdapter adapter;
    private JobOffersPresenter presenter;
    private EndlessRecyclerOnScrollListener scrollListener;

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
        presenter = new JobOffersPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadJobOffersList(Constants.DEFAULT_UNSELECTED_POSITION, 1, true);
        toggleSelectionOfFilters(true);
        setScrollListener((LinearLayoutManager) jobsListView.getLayoutManager());
        adapter.registerAdapterDataObserver(adapterDataObserver);
        return view;
    }

    @OnClick(R.id.closeBtn)
    void onCloseBtnClicked() {
        ((BaseActivity) getActivity()).goToTop();
    }

    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            jobsListView.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    };

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    private void setScrollListener(LinearLayoutManager llm) {
        scrollListener = new EndlessRecyclerOnScrollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                boolean isAllAvailable = allBtnLbl.getAlpha() == Constants.ENABLED_LAYOUT_ALPHA;
                presenter.loadJobOffersList(adapter.getItemCount(), current_page, isAllAvailable);
            }
        };
        jobsListView.addOnScrollListener(scrollListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.unregisterAdapterDataObserver(adapterDataObserver);
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
    public void clearList() {
        adapter.clearList();
    }

    @Override
    public void onListLoaded(List<JobOffer> jobOfferList) {
        if (jobOfferList.size() > 0) {
            adapter.setList(jobOfferList);
            adapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            jobsListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAvailableJobsCount(int count) {
        availableBtnLbl.setText(getString(R.string.jobs_available, count));
    }

    @Override
    public void resetScrollListener() {
        scrollListener.reset();
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
        JobInfoFragment fragment = new JobInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.JOB_OFFER_TAG, Parcels.wrap(item));
        fragment.setArguments(args);
        ((BaseActivity) getActivity()).changeFragment(fragment, true);
    }
}
