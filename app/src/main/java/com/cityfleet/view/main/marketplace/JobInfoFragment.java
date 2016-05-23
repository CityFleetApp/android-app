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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.JobOffer;
import com.cityfleet.model.JobOfferStatus;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.Constants;
import com.cityfleet.util.PrefUtil;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.main.profile.ProfileFragment;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 29.03.16.
 */
public class JobInfoFragment extends BaseFragment {
    private SimpleDateFormat intputDateTimeFormatter = new SimpleDateFormat(Constants.INPUT_DATETIME_FORMAT, Locale.ENGLISH);
    private SimpleDateFormat outputTimeFormatter = new SimpleDateFormat(Constants.OUTPUT_TIME_FORMAT, Locale.ENGLISH);
    private SimpleDateFormat outputDateFormatter = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT, Locale.ENGLISH);
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.titleLbl)
    TextView titleText;
    @Bind(R.id.dateLbl)
    TextView dateLbl;
    @Bind(R.id.timeLbl)
    TextView timeLbl;
    @Bind(R.id.pickupAddressLbl)
    TextView pickupAddressLbl;
    @Bind(R.id.dropOffLbl)
    TextView dropOffLbl;
    @Bind(R.id.payLbl)
    TextView payLbl;
    @Bind(R.id.tollsLbl)
    TextView tollsLbl;
    @Bind(R.id.gratuityLbl)
    TextView gratuityLbl;
    @Bind(R.id.vehicleTypeLbl)
    TextView vehicleTypeLbl;
    @Bind(R.id.suitTieLbl)
    TextView suiteTieLbl;
    @Bind(R.id.companyPersonalLbl)
    TextView companyPersonalLbl;
    @Bind(R.id.jobTypeLbl)
    TextView jobTypeLbl;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.requestBtn)
    Button requestBtn;
    @Bind(R.id.jobAwardedLbl)
    TextView jobAwardedLbl;
    @Bind(R.id.authorLbl)
    TextView authorLbl;
    @Bind(R.id.closeBtn)
    ImageButton closeBtn;
    private JobOffer jobOffer;
    private int jobId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_info_fragment, container, false);
        ButterKnife.bind(this, view);
        closeBtn.setVisibility(View.VISIBLE);
        if (getArguments().containsKey(Constants.JOB_OFFER_TAG)) {
            jobOffer = Parcels.unwrap(getArguments().getParcelable(Constants.JOB_OFFER_TAG));
        } else {
            jobId = getArguments().getInt(Constants.JOB_OFFER_ID_TAG);
        }
        title.setText(R.string.job_info);
        if (jobOffer != null) {
            init();
        } else {
            loadJobInfo();
        }
        return view;
    }

    @OnClick(R.id.authorBtn)
    void onAuthorBtnClicked() {
        if (jobOffer != null) {
            ((BaseActivity) getActivity()).changeFragment(ProfileFragment.getInstanceForFriend(jobOffer.getOwner(), getContext()), true);
        }
    }
    @OnClick(R.id.closeBtn)
    void onCloseBtnClicked() {
        ((BaseActivity) getActivity()).goToTop();
    }
    private void init() {
        String time = "";
        String date = "";
        try {
            Date datetime = intputDateTimeFormatter.parse(jobOffer.getDateTime());
            time = outputTimeFormatter.format(datetime);
            date = outputDateFormatter.format(datetime);
        } catch (ParseException e) {
            Log.e(JobOffersAdapter.class.getName(), e.getMessage());
        }
        titleText.setText(jobOffer.getTitle());
        dateLbl.setText(date);
        timeLbl.setText(time);
        pickupAddressLbl.setText(jobOffer.getPickupAddress());
        dropOffLbl.setText(jobOffer.getDestination());
        payLbl.setText(getString(R.string.dollar_price, jobOffer.getFare()));
        tollsLbl.setText(getString(R.string.dollar_price, jobOffer.getTolls()));
        gratuityLbl.setText(getString(R.string.dollar_price, jobOffer.getGratuity()));
        vehicleTypeLbl.setText(jobOffer.getVehicleType());
        suiteTieLbl.setText(jobOffer.isSuite() ? getString(R.string.yes) : getString(R.string.no));
        authorLbl.setText(jobOffer.getOwnerName());
        companyPersonalLbl.setText(jobOffer.getPersonal());
        jobTypeLbl.setText(jobOffer.getJobType());
        if(jobOffer.getOwner()== PrefUtil.getId(getContext())){
            requestBtn.setVisibility(View.GONE);
            return;
        }
        if (jobOffer.getStatus().equalsIgnoreCase(JobOfferStatus.COVERED.name()) && jobOffer.isAwarded()) {
            jobAwardedLbl.setVisibility(View.VISIBLE);
            requestBtn.setText(R.string.complete_job);
        } else if (jobOffer.getStatus().equalsIgnoreCase(JobOfferStatus.COVERED.name()) || jobOffer.getStatus().equalsIgnoreCase(JobOfferStatus.AVAILABLE.name()) && jobOffer.isRequested()) {
            requestBtn.setVisibility(View.GONE);
        } else {
            jobAwardedLbl.setVisibility(View.GONE);
            requestBtn.setText(R.string.request_job);
        }
    }

    @OnClick(R.id.requestBtn)
    void onRequestBtnClick() {
        if (jobOffer.getStatus().equalsIgnoreCase(JobOfferStatus.COVERED.name()) && jobOffer.isAwarded()) {
            JobOfferAwardFragment fragment = new JobOfferAwardFragment();
            Bundle args = new Bundle();
            args.putInt(Constants.JOB_OFFER_ID_TAG, jobOffer.getId());
            args.putString(Constants.JOB_OFFER_TITLE_TAG, jobOffer.getTitle());
            fragment.setArguments(args);
            ((BaseActivity) getActivity()).changeFragment(fragment, true);
        } else {
            NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
            if (networkManager.isConnectedOrConnecting()) {
                Call<Void> call = networkManager.getNetworkClient().requestJob(jobOffer.getId());
                call.enqueue(requestCallback);
            } else {
                Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void loadJobInfo() {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            Call<JobOffer> call = networkManager.getNetworkClient().getJobOfferInfo(jobId);
            call.enqueue(infoCallback);
        } else {
            Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
        }
    }

    private Callback<Void> requestCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                getActivity().onBackPressed();
                Toast.makeText(getContext(), R.string.job_request_sent, Toast.LENGTH_SHORT).show();
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

    private Callback<JobOffer> infoCallback = new Callback<JobOffer>() {
        @Override
        public void onResponse(Call<JobOffer> call, Response<JobOffer> response) {
            if (response.isSuccessful()) {
                jobOffer = response.body();
                init();
            } else {
                String error = NetworkErrorUtil.gerErrorMessage(response);
                if (TextUtils.isEmpty(error)) {
                    error = getString(R.string.default_error_mes);
                }
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<JobOffer> call, Throwable t) {
            Log.d(JobInfoFragment.class.getName(), t.getMessage());
            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
