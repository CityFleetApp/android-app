package com.citifleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.citifleet.R;
import com.citifleet.util.Constants;
import com.citifleet.util.RecycleViewClickListener;
import com.citifleet.util.SpacesItemDecoration;
import com.citifleet.view.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 23.03.16.
 */
public class MarketPlaceBuyRentFragment extends BaseFragment {
    @Bind(R.id.marketplaceList)
    RecyclerView marketplaceList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private BuyRentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_buy_rent, container, false);
        ButterKnife.bind(this, view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constants.BUY_RENT_COLUMNS_COUNT, StaggeredGridLayoutManager.VERTICAL);
        marketplaceList.setLayoutManager(layoutManager);
        adapter = new BuyRentAdapter(getActivity());
        marketplaceList.setAdapter(adapter);
        marketplaceList.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.benefit_list_space)));
        marketplaceList.addOnItemTouchListener(new RecycleViewClickListener(getActivity(), onItemClickListener));
        return view;
    }

    private RecycleViewClickListener.OnItemClickListener onItemClickListener = new RecycleViewClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
