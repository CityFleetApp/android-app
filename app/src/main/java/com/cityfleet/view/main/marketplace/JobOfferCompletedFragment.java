package com.cityfleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
 * Created by vika on 29.04.16.
 */
public class JobOfferCompletedFragment extends BaseFragment {
    @Bind({R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5})
    List<ImageView> stars;
    @Bind(R.id.submitBtn)
    Button submitBtn;
    @Bind(R.id.jobCompletedLbl)
    TextView jobCompletedLbl;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.closeBtn)
    ImageButton closeBtn;
    private int jobOfferId;
    private String jobTitle;
    private String jobExecutor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_offer_completed, container, false);
        ButterKnife.bind(this, view);
        closeBtn.setVisibility(View.VISIBLE);
        jobOfferId = getArguments().getInt(Constants.JOB_OFFER_ID_TAG, 0);
        jobTitle = getArguments().getString(Constants.JOB_OFFER_TITLE_TAG);
        jobExecutor = getArguments().getString(Constants.JOB_OFFER_EXECUTOR_TAG);
        jobCompletedLbl.setText(getString(R.string.completed_by_smb, jobTitle, jobExecutor));
        title.setText(R.string.complete);
        return view;
    }

    @OnClick(R.id.closeBtn)
    void onCloseBtnClicked() {
        ((BaseActivity) getActivity()).goToTop();
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
            rateExecutor();
        } else {
            Toast.makeText(getContext(), getString(R.string.rate_executor), Toast.LENGTH_SHORT).show();
        }
    }

    private void rateExecutor() {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            Call<Void> call = networkManager.getNetworkClient().rateDriver(jobOfferId, getRating());
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
