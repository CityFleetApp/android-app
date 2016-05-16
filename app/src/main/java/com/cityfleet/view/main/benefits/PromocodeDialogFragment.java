package com.cityfleet.view.main.benefits;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 16.05.16.
 */
public class PromocodeDialogFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.promocode)
    TextView promocode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.promocode_benefit, null);
        ButterKnife.bind(this, v);
        String promocodeString = getArguments().getString(Constants.PROMOCODE_STRING);
        String benefitName = getArguments().getString(Constants.BENEFIT_NAME);
        title.setText(benefitName);
        promocode.setText(promocodeString);
        return v;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }
}
