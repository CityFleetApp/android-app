package com.citifleet.view.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citifleet.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashScreenFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splashscreen_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.loginBtn)
    public void onLoginBtnClick() {

    }

    @OnClick(R.id.registerBtn)
    public void onRegisterBtnClick() {
    }
}
