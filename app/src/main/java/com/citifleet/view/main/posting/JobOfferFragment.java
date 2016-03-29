package com.citifleet.view.main.posting;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.view.BaseFragment;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 21.03.16.
 */
public class JobOfferFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.dateText)
    TextView dateText;
    @Bind(R.id.timeText)
    TextView timeText;
    @Bind(R.id.pickupText)
    TextView pickupText;
    @Bind(R.id.destinationText)
    TextView destinationText;
    @Bind(R.id.fareET)
    EditText fareEt;
    @Bind(R.id.gratuityET)
    EditText gratuityEt;
    @Bind(R.id.vehicleTypeText)
    TextView vehicleTypeText;
    @Bind(R.id.suiteText)
    TextView suiteText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_offer_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.job_offer);
        return view;
    }

    @OnClick(R.id.dateBtn)
    void onDateBtnClick() {
        Calendar dateAndTime = Calendar.getInstance();
        dateAndTime.add(Calendar.DAY_OF_YEAR, 1);
    }

    @OnClick(R.id.timeBtn)
    void onTimeBtnClick() {

    }

    @OnClick(R.id.pickupBtn)
    void onPickupBtnClick() {

    }

    @OnClick(R.id.destinationBtn)
    void onDestinationBtnClick() {

    }

    @OnClick(R.id.vehicleTypeBtn)
    void vehicleTypeBtn() {

    }

    @OnClick(R.id.suiteBtn)
    void suiteBtnClick() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }


}
