package com.citifleet.view.main.marketplace;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.GeneralGood;
import com.citifleet.util.Constants;
import com.citifleet.util.EndlessStaggeredScollListener;
import com.citifleet.view.BaseFragment;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 24.03.16.
 */
public class GeneralGoodsFragment extends BaseFragment implements GeneralGoodsPresenter.GeneralGoodsView, GeneralGoodsAdapter.OnItemClickListener {
    @Bind(R.id.marketplaceList)
    RecyclerView marketplaceList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.title)
    TextView title;
    private GeneralGoodsAdapter adapter;
    private GeneralGoodsPresenter presenter;
    private EndlessStaggeredScollListener scrollListener;

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
        presenter = new GeneralGoodsPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadGeneralGoodsList(Constants.DEFAULT_UNSELECTED_POSITION, 1);
        adapter.setClickListener(this);

        title.setText(R.string.general_goods_for_sale);
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    private void setScrollListener(StaggeredGridLayoutManager llm) {
        scrollListener = new EndlessStaggeredScollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                presenter.loadGeneralGoodsList(adapter.getItemCount(), current_page);
            }
        };
        marketplaceList.addOnScrollListener(scrollListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
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
        adapter.setList(generalGoodList);
        adapter.notifyDataSetChanged();
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
}
