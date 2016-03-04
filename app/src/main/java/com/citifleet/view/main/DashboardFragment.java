package com.citifleet.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.citifleet.R;
import com.citifleet.util.CircleTransform;
import com.citifleet.view.BaseActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class DashboardFragment extends Fragment {
    @Bind(R.id.profileImage)
    ImageView profileImage;
    @Bind(R.id.bigProfileImage)
    ImageView bigProfileImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        ButterKnife.bind(this, view);
        int frameSize = getResources().getDimensionPixelSize(R.dimen.profile_image_frame);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.profile_image_blur_radius_percent, outValue, true);
        int blurradius = (int) (screenWidth * outValue.getFloat());
        Picasso.with(getActivity()).load(R.drawable.testprofile).transform(new CircleTransform(frameSize)).into(profileImage);
        Picasso.with(getActivity()).load(R.drawable.testprofile).transform(new BlurTransformation(getContext(), blurradius)).into(bigProfileImage);
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
