package com.cityfleet.view.main.marketplace;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.Car;
import com.cityfleet.model.CarPostingType;
import com.cityfleet.util.Constants;
import com.cityfleet.util.EndlessStaggeredScollListener;
import com.cityfleet.view.BaseFragment;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 23.03.16.
 */
public class BuyRentDetailFragment extends BaseFragment implements BuyRentPresenter.BuyRentView, BuyRentAdapter.OnItemClickListener {
    @Bind(R.id.marketplaceList)
    RecyclerView marketplaceList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.emptyView)
    TextView emptyView;
    private BuyRentAdapter adapter;
    private BuyRentPresenter presenter;
    private CarPostingType type;
    private EndlessStaggeredScollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_buy_rent, container, false);
        ButterKnife.bind(this, view);
        type = (CarPostingType) getArguments().getSerializable(Constants.POSTING_TYPE_TAG);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constants.BUY_RENT_COLUMNS_COUNT, StaggeredGridLayoutManager.VERTICAL);
        marketplaceList.setLayoutManager(layoutManager);
        adapter = new BuyRentAdapter(getActivity());
        marketplaceList.setAdapter(adapter);
        adapter.setClickListener(this);
        setScrollListener(layoutManager);
        presenter = new BuyRentPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadCarList(type, Constants.DEFAULT_UNSELECTED_POSITION, 1);
        adapter.registerAdapterDataObserver(dataObserver);

        return view;
    }

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            marketplaceList.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.unregisterAdapterDataObserver(dataObserver);
        ButterKnife.unbind(this);
    }

    private void setScrollListener(StaggeredGridLayoutManager llm) {
        scrollListener = new EndlessStaggeredScollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                presenter.loadCarList(type, adapter.getItemCount(), current_page);
            }
        };
        marketplaceList.addOnScrollListener(scrollListener);
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
    public void onListLoaded(List<Car> carList) {
        if (carList.size() > 0) {
            adapter.setList(carList);
            adapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            marketplaceList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(Car car, View imageView) {
        Fragment detailFragment = new RentSaleItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CAR_RENT_SALE_TAG, Parcels.wrap(car));
        detailFragment.setArguments(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            detailFragment.setSharedElementEnterTransition(new DetailsTransition());
            detailFragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            detailFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, getString(R.string.rent_sale_transition_name))
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit();

    }
}
