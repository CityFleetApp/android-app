package com.citifleet.view.main.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 31.03.16.
 */
public class EditUserCarFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_user_profile_car, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.edit_account));
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
