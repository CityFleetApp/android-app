package com.cityfleet.view.main.benefits;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.Benefit;
import com.cityfleet.util.Constants;
import com.cityfleet.util.RecycleViewClickListener;
import com.cityfleet.util.SpacesItemDecoration;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 12.03.16.
 */
public class BenefitsFragment extends BaseFragment implements BenefitPresenter.BenefitView {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.benefitsList)
    RecyclerView benefitsList;
    @Bind(R.id.emptyView)
    TextView emptyView;
    private BenefitPresenter presenter;
    private BenefitsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.benefit_list, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.benefits);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        benefitsList.setLayoutManager(layoutManager);
        adapter = new BenefitsAdapter(getActivity());
        benefitsList.setAdapter(adapter);
        benefitsList.addItemDecoration(new SpacesItemDecoration((int) getResources().getDimension(R.dimen.benefit_list_space)));
        presenter = new BenefitPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadBenefits();
        benefitsList.addOnItemTouchListener(new RecycleViewClickListener(getActivity(), onItemClickListener));
        adapter.registerAdapterDataObserver(dataObserver);
        return view;
    }

    private RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            benefitsList.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        }
    };
    private RecycleViewClickListener.OnItemClickListener onItemClickListener = new RecycleViewClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Benefit benefit = adapter.getBenefit(position);
            if (TextUtils.isEmpty(benefit.getBarcode())) {
                PromocodeDialogFragment fragment = new PromocodeDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PROMOCODE_STRING, benefit.getPromocode());
                bundle.putString(Constants.BENEFIT_NAME, benefit.getName());
                fragment.setArguments(bundle);
                ((BaseActivity) getActivity()).changeFragment(fragment, true);
            } else {
                BarcodeDialogFragment fragment = new BarcodeDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BARCODE_STRING, benefit.getBarcode());
                bundle.putString(Constants.BENEFIT_NAME, benefit.getName());
                fragment.setArguments(bundle);
                ((BaseActivity) getActivity()).changeFragment(fragment, true);
            }
        }
    };

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.unregisterAdapterDataObserver(dataObserver);
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
    public void updateBenefitsList(final List<Benefit> benefits) {
        if (benefits.size() > 0) {
            adapter.setBenefitList(benefits);
            adapter.notifyDataSetChanged();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            benefitsList.setVisibility(View.GONE);
        }
    }
}
