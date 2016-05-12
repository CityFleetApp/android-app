package com.cityfleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 23.03.16.
 */
public class BuyRentTabbedFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.searchBar)
    EditText searchBar;
    @Bind(R.id.searchBtn)
    ImageButton searchBtn;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_rent_sale, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.marketplace);
        RentSalePagerAdapter adapter = new RentSalePagerAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.rent_sale_marketplace));
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        if (searchBar.getVisibility() == View.VISIBLE) {
            showSearch(false);
        } else {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.searchBtn)
    void onSearchBtnClick() {
        showSearch(true);
    }

    private void showSearch(boolean showSearch) {
        title.setVisibility(showSearch ? View.GONE : View.VISIBLE);
        searchBtn.setVisibility(showSearch ? View.GONE : View.VISIBLE);
        searchBar.setVisibility(showSearch ? View.VISIBLE : View.GONE);
        if (!showSearch) {
            searchBar.getText().clear();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
