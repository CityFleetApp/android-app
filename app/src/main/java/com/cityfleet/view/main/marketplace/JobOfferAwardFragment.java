package com.cityfleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 25.04.16.
 */
public class JobOfferAwardFragment extends BaseFragment {
    @Bind({R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5})
    List<ImageView> stars;
    @Bind(R.id.paidOnTimeSwitcher)
    SwitchCompat switcher;
    @Bind(R.id.submitBtn)
    Button submitBtn;
    private int jobOfferId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_offer_award, container, false);
        ButterKnife.bind(this, view);
        jobOfferId = getArguments().getInt(Constants.JOB_OFFER_ID_TAG, 0);
        return view;
    }

    @OnClick({R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5})
    void onStarClick(ImageView view) {
        int position = 0;
        for (int i = 0; i < stars.size(); i++) {
            if (view.equals(stars.get(i))) {
                position = i;
            }
        }
        for (int i = 0; i <= position; i++) {
            stars.get(i).setSelected(true);
        }
        for (int i = position + 1; i < stars.size(); i++) {
            stars.get(i).setSelected(false);
        }
    }

    @OnClick(R.id.submitBtn)
    void onSubmitBtnClicked() {
        if (stars.get(0).isSelected()) {
            completeJob();
        } else {
            Toast.makeText(getContext(), getString(R.string.rating_empty), Toast.LENGTH_SHORT).show();
        }
    }

    private void completeJob() {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            Call<Void> call = networkManager.getNetworkClient().completeJob(jobOfferId, getRating(), switcher.isChecked());
            call.enqueue(completeCallback);
        } else {
            Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
        }
    }

    private Callback<Void> completeCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                ((BaseActivity) getActivity()).goToTop();
            } else {
                String error = NetworkErrorUtil.gerErrorMessage(response);
                if (TextUtils.isEmpty(error)) {
                    error = getString(R.string.default_error_mes);
                }
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.d(JobInfoFragment.class.getName(), t.getMessage());
            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private int getRating() {
        int rating = 0;
        for (ImageView star : stars) {
            if (star.isSelected()) {
                rating++;
            }
        }
        return rating;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
