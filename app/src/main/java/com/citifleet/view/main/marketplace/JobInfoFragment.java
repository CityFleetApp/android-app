package com.citifleet.view.main.marketplace;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.JobOffer;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseFragment;

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
    private JobOffer jobOffer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_info_fragment, container, false);
        ButterKnife.bind(this, view);
        jobOffer = Parcels.unwrap(getArguments().getParcelable(Constants.JOB_OFFER_TAG));
        title.setText(R.string.job_info);
        String time = "";
        String date = "";
        try {
            Date datetime = intputDateTimeFormatter.parse(jobOffer.getDateTime());
            time = outputTimeFormatter.format(datetime);
            date = outputDateFormatter.format(datetime);
        } catch (ParseException e) {
            Log.e(JobOffersAdapter.class.getName(), e.getMessage());
        }
        dateLbl.setText(date);
        timeLbl.setText(time);
        pickupAddressLbl.setText(jobOffer.getPickupAddress());
        dropOffLbl.setText(jobOffer.getDestination());
        payLbl.setText(getString(R.string.dollar_price, jobOffer.getFare()));
        tollsLbl.setText("");
        gratuityLbl.setText(jobOffer.getGratuity());
        vehicleTypeLbl.setText(jobOffer.getVehicleType());
        suiteTieLbl.setText(jobOffer.isSuite() ? getString(R.string.yes) : getString(R.string.no));
        companyPersonalLbl.setText("");
        jobTypeLbl.setText(jobOffer.getJobType());
        return view;
    }

    @OnClick(R.id.requestBtn)
    void onRequestBtnClick() {
        NetworkManager networkManager = CitiFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            Call<Void> call = networkManager.getNetworkClient().requestJob(jobOffer.getId());
            call.enqueue(callback);
        } else {
            Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
        }
    }

    private Callback<Void> callback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccess()) {
                getActivity().onBackPressed();
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
