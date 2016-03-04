package com.citifleet.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citifleet.R;
import com.citifleet.view.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.homeBtn)
    void onHomeBtnClick() {
        ((BaseActivity) getActivity()).onBackPressed();
    }

    @OnClick(R.id.profileBtn)
    void onProfileBtnClick() {

    }

    @OnClick(R.id.postingBtn)
    void onPostingBtnClick() {

    }

    @OnClick(R.id.earningsBtn)
    void onEarningsBtnClick() {

    }

    @OnClick(R.id.inviteDriverBtn)
    void onInviteDriverBtnClick() {

    }

    @OnClick(R.id.chatBtn)
    void onChatBtnBtnClick() {

    }

    @OnClick(R.id.benefitsBtn)
    void onBenefitsBtnClick() {

    }

    @OnClick(R.id.legalAidBtn)
    void onLegalAidBtnBtnClick() {

    }

    @OnClick(R.id.helpBtn)
    void onHelpBtnBtnClick() {

    }

    @OnClick(R.id.signOutBtn)
    void onSignoutBtnClick() {

    }
}
