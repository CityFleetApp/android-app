package com.citifleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.citifleet.R;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 24.03.16.
 */
public class GeneralGoodsFragment extends BaseFragment {
    @Bind(R.id.marketplaceList)
    RecyclerView marketplaceList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private GeneralGoodsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_buy_rent, container, false);
        ButterKnife.bind(this, view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constants.BUY_RENT_COLUMNS_COUNT, StaggeredGridLayoutManager.VERTICAL);
        marketplaceList.setLayoutManager(layoutManager);
        adapter = new GeneralGoodsAdapter(getActivity());
        marketplaceList.setAdapter(adapter);
//        presenter = new BuyRentPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
//        presenter.loadCarList(type);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

//    @Override
//    public void startLoading() {
//        progressBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void stopLoading() {
//        progressBar.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onFailure(String error) {
//        if (getActivity() != null) {
//            if (error == null) {
//                error = getString(R.string.default_error_mes);
//            }
//            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onNetworkError() {
//        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onListLoaded(List<Car> carList) {
//        adapter.setList(carList);
//        adapter.notifyDataSetChanged();
//    }
}
