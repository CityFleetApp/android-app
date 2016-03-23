package com.citifleet.view.main.benefits;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.Benefit;
import com.citifleet.util.Constants;
import com.citifleet.util.RecycleViewClickListener;
import com.citifleet.util.SpacesItemDecoration;
import com.citifleet.view.BaseFragment;

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
        presenter = new BenefitPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadBenefits();
        benefitsList.addOnItemTouchListener(new RecycleViewClickListener(getActivity(), onItemClickListener));
        return view;
    }

    private RecycleViewClickListener.OnItemClickListener onItemClickListener = new RecycleViewClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            FragmentManager fm = getChildFragmentManager();
            BarcodeDialogFragment fragment = (BarcodeDialogFragment) fm.findFragmentByTag(Constants.BENEFITS_BARCODE_TAG);
            if (fragment == null) {
                fragment = new BarcodeDialogFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BARCODE_STRING, adapter.getBarcodeString(position));
            fragment.setArguments(bundle);
            fm.beginTransaction().add(R.id.childFragmentContainer, fragment, Constants.BENEFITS_BARCODE_TAG).addToBackStack(null).commit();
        }
    };

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.popBackStack();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            getActivity().onBackPressed();
        }
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
    public void updateBenefitsList(final List<Benefit> benefits) {
        adapter.setBenefitList(benefits);
        adapter.notifyDataSetChanged();
    }
}
