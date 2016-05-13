package com.cityfleet.view.main.marketplace;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.GeneralGood;
import com.cityfleet.util.Constants;
import com.cityfleet.util.EndlessStaggeredScollListener;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.main.chat.SearchEditText;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by vika on 24.03.16.
 */
public class GeneralGoodsFragment extends BaseFragment implements GeneralGoodsPresenter.GeneralGoodsView, GeneralGoodsAdapter.OnItemClickListener, SearchEditText.EditTextImeBackListener {
    @Bind(R.id.marketplaceList)
    RecyclerView marketplaceList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.searchBar)
    SearchEditText searchBar;
    @Bind(R.id.searchBtn)
    ImageButton searchBtn;
    @Bind(R.id.emptyView)
    TextView emptyView;
    private GeneralGoodsAdapter adapter;
    private GeneralGoodsPresenter presenter;
    private EndlessStaggeredScollListener scrollListener;
    private String searchWord;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_general_goods, container, false);
        ButterKnife.bind(this, view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constants.BUY_RENT_COLUMNS_COUNT, StaggeredGridLayoutManager.VERTICAL);
        marketplaceList.setLayoutManager(layoutManager);
        adapter = new GeneralGoodsAdapter(getActivity());
        marketplaceList.setAdapter(adapter);
        setScrollListener(layoutManager);
        presenter = new GeneralGoodsPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadGeneralGoodsList(Constants.DEFAULT_UNSELECTED_POSITION, 1, searchWord);
        adapter.setClickListener(this);
        title.setText(R.string.general_goods_for_sale);
        adapter.registerAdapterDataObserver(adapterDataObserver);
        searchBar.setOnEditTextImeBackListener(this);

        return view;
    }

    private void search(String searchWord) {
        this.searchWord = searchWord;
        adapter.clearList();
        adapter.notifyDataSetChanged();
        scrollListener.reset();
        presenter.loadGeneralGoodsList(Constants.DEFAULT_UNSELECTED_POSITION, 1, searchWord);
    }

    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            marketplaceList.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    };

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        if (searchBar.getVisibility() == View.VISIBLE) {
            showSearch(false);
            search(null);
            hideKeyboard();
        } else {
            getActivity().onBackPressed();
        }
    }

    @OnEditorAction(R.id.searchBar)
    protected boolean onSearchClicked(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search(searchBar.getText().toString());
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

    private void setScrollListener(StaggeredGridLayoutManager llm) {
        scrollListener = new EndlessStaggeredScollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                presenter.loadGeneralGoodsList(adapter.getItemCount(), current_page, searchWord);
            }
        };
        marketplaceList.addOnScrollListener(scrollListener);
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
        adapter.unregisterAdapterDataObserver(adapterDataObserver);
        ButterKnife.unbind(this);
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = getString(R.string.default_error_mes);
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListLoaded(List<GeneralGood> generalGoodList) {
        if (generalGoodList.size() > 0) {
            adapter.setList(generalGoodList);
            adapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            marketplaceList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(GeneralGood generalGood, View view) {
        Fragment detailFragment = new GeneralGoodDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.GENERAL_GOODS_TAG, Parcels.wrap(generalGood));
        detailFragment.setArguments(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            detailFragment.setSharedElementEnterTransition(new DetailsTransition());
            detailFragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            detailFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(view, getString(R.string.general_goods_transition_name))
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit();
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
        search(searchBar.getText().toString());
        hideKeyboard();
    }
}
