package com.cityfleet.view.main.marketplace;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.util.MarketplaceSearchEvent;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.main.chat.SearchEditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by vika on 23.03.16.
 */
public class BuyRentTabbedFragment extends BaseFragment implements SearchEditText.EditTextImeBackListener {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.searchBar)
    SearchEditText searchBar;
    @Bind(R.id.searchBtn)
    ImageButton searchBtn;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.closeBtn)
    ImageButton closeBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_rent_sale, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.marketplace);
        RentSalePagerAdapter adapter = new RentSalePagerAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.rent_sale_marketplace));
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        searchBar.setOnEditTextImeBackListener(this);
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        if (searchBar.getVisibility() == View.VISIBLE) {
            showSearch(false);
            EventBus.getDefault().post(new MarketplaceSearchEvent(""));
            hideKeyboard();
        } else {
            getActivity().onBackPressed();
        }
    }

    @OnEditorAction(R.id.searchBar)
    protected boolean onSearchClicked(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            EventBus.getDefault().post(new MarketplaceSearchEvent(searchBar.getText().toString()));
            hideKeyboard();
            return true;
        }

        return false;
    }

    @OnClick(R.id.searchBtn)
    void onSearchBtnClick() {
        showSearch(true);
        showKeyboard();
    }

    @OnClick(R.id.closeBtn)
    void onCloseBtnClicked() {
        ((BaseActivity) getActivity()).goToTop();
    }

    private void showSearch(boolean showSearch) {
        title.setVisibility(showSearch ? View.GONE : View.VISIBLE);
        searchBtn.setVisibility(showSearch ? View.GONE : View.VISIBLE);
        searchBar.setVisibility(showSearch ? View.VISIBLE : View.GONE);
        closeBtn.setVisibility(showSearch ? View.GONE : View.VISIBLE);
        if (!showSearch) {
            searchBar.getText().clear();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBar.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    private void showKeyboard() {
        searchBar.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void onImeBack(SearchEditText ctrl, String text) {
        EventBus.getDefault().post(new MarketplaceSearchEvent(searchBar.getText().toString()));
        hideKeyboard();
    }
}
