package com.citifleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.Car;
import com.citifleet.model.PostingType;
import com.citifleet.util.Constants;
import com.citifleet.util.RecycleViewClickListener;
import com.citifleet.view.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vika on 23.03.16.
 */
public class BuyRentDetailFragment extends BaseFragment implements BuyRentPresenter.BuyRentView {
    @Bind(R.id.marketplaceList)
    RecyclerView marketplaceList;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private BuyRentAdapter adapter;
    private BuyRentPresenter presenter;
    private PostingType type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marketplace_buy_rent, container, false);
        ButterKnife.bind(this, view);
        type = (PostingType) getArguments().getSerializable(Constants.POSTING_TYPE_TAG);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constants.BUY_RENT_COLUMNS_COUNT, StaggeredGridLayoutManager.VERTICAL);
        marketplaceList.setLayoutManager(layoutManager);
        adapter = new BuyRentAdapter(getActivity());
        marketplaceList.setAdapter(adapter);
        //marketplaceList.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.marketplace_space)));
       // marketplaceList.addOnItemTouchListener(new RecycleViewClickListener(getActivity(), onItemClickListener));
        presenter = new BuyRentPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadCarList(type);
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
    public void onListLoaded(List<Car> carList) {
        adapter.setList(carList);
        adapter.notifyDataSetChanged();
    }
}
