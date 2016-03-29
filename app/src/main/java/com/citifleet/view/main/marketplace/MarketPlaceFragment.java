package com.citifleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;
import com.squareup.leakcanary.RefWatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 12.03.16.
 */
public class MarketPlaceFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.marketplace);
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

    @OnClick(R.id.carsForRentBtn)
    void onCarsForRentBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new BuyRentTabbedFragment(), true);
    }

    @OnClick(R.id.generalGoodsForSaleBtn)
    void generalGoodsForSaleBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new GeneralGoodsFragment(), true);
    }

    @OnClick(R.id.jobsOfferBtn)
    void jobsOfferBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new JobOffersFragment(), true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = CitiFleetApp.getInstance().getRefWatcher();
        refWatcher.watch(this);
    }
}
