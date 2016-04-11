package com.citifleet.view.main.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
 * Created by vika on 11.04.16.
 */
public class FriendsListTabbedFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewPager)
    ViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_list_tabbed_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.contacts);
        FriendsListPagerAdapter adapter = new FriendsListPagerAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.rent_sale_marketplace));
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.user));
        tabs.getTabAt(1).setIcon(ContextCompat.getDrawable(getContext(), R.drawable.clock));
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


}

