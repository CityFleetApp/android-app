package com.cityfleet.view.main.posting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.CarPostingType;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 18.03.16.
 */
public class PostintRentSaleFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_rent_sale_fragment, container, false);
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


    @OnClick(R.id.rentBtn)
    void onRentBtnClick() {
        showDetailFragment(CarPostingType.RENT);
    }

    @OnClick(R.id.saleBtn)
    void onSaleBtnClick() {
        showDetailFragment(CarPostingType.SALE);
    }

    private void showDetailFragment(CarPostingType type) {
        PostingRentSaleDetailFragment fragment = new PostingRentSaleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.POSTING_TYPE_TAG, type);
        fragment.setArguments(bundle);
        ((BaseActivity) getActivity()).changeFragment(fragment, true);
    }
}
