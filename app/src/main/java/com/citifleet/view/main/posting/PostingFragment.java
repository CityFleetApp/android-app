package com.citifleet.view.main.posting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 18.03.16.
 */
public class PostingFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.posting_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.post);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }


    @OnClick(R.id.vehiclesBtn)
    void onVehiclesBtnClick() {
        ((BaseActivity) getActivity()).changeFragment(new PostintRentSaleFragment(), true);
    }

    @OnClick(R.id.jobsOfferBtn)
    void onJobOffersBtnClick() {

    }

    @OnClick(R.id.generalGoodsBtn)
    void onGeneralGoodsBtnClick() {

    }

    @OnClick(R.id.managePostsBtn)
    void onManagePostsBtn() {

    }
}
