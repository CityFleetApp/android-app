package com.citifleet.view.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.citifleet.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterFragment extends Fragment {
    @Bind(R.id.fullNameEt)
    EditText fullNameEt;
    @Bind(R.id.usernameEt)
    EditText usernameEt;
    @Bind(R.id.phoneEt)
    EditText phoneEt;
    @Bind(R.id.hackLicenseEt)
    EditText hackLicenseEt;
    @Bind(R.id.emailEt)
    EditText emailEt;
    @Bind(R.id.passwordEt)
    EditText passwordEt;
    @Bind(R.id.confirmPasswordEt)
    EditText confirmPasswordEt;
    @Bind(R.id.signupBtn)
    Button   signupBtn;
    @Bind(R.id.toolbar)
    Toolbar  toolbar;
    @Bind(R.id.title)
    TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.register);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.signupBtn)
    void onSignupBtnClick() {

    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }
}
