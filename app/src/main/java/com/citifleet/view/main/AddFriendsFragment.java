package com.citifleet.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFriendsFragment extends Fragment {
    @Bind(R.id.title)
    TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.add_friends_title);
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

    @OnClick(R.id.contactsBtn)
    void onContactsBtnClick() {

    }

    @OnClick(R.id.facebookBtn)
    void onFacebookBtnClick() {

    }

    @OnClick(R.id.twitterBtn)
    void onTwitterBtnClick() {

    }

    @OnClick(R.id.instagramBtn)
    void onInstagramBtnClick() {

    }
}
