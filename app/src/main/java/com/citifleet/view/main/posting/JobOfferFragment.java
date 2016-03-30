package com.citifleet.view.main.posting;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.CarOption;
import com.citifleet.model.JobOffer;
import com.citifleet.util.Constants;
import com.citifleet.util.DecimalDigitsInputFilter;
import com.citifleet.view.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 21.03.16.
 */
public class JobOfferFragment extends BaseFragment implements JobOfferPresenter.PostingJobOfferView {
    SimpleDateFormat formatter = new SimpleDateFormat(Constants.OUTPUT_TIME_FORMAT);
    SimpleDateFormat outputFormatter = new SimpleDateFormat(Constants.INPUT_DATETIME_FORMAT);
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
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.postBtn)
    Button postBtn;
    @Bind(R.id.jobTypeText)
    TextView jobTypeText;
    @Bind(R.id.instructionsET)
    EditText instructionsET;
    private Calendar selectedDateTimeCalendar = Calendar.getInstance();
    //  private String selectedDate, selectedTime;
    private boolean selectedSuiteTie;
    private int selectedJobType = Constants.DEFAULT_UNSELECTED_POSITION, selectedVehicleType = Constants.DEFAULT_UNSELECTED_POSITION;
    private List<String> stringOptions;
    private JobOfferPresenter presenter;
    private List<CarOption> vehicleTypes;
    private List<CarOption> jobTypes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_offer_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.job_offer);
        presenter = new JobOfferPresenter(CitiFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadVehicleAndJobTypes();
        fareEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(Constants.JOB_OFFER_MAX_FARE)});
        gratuityEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(Constants.JOB_OFFER_MAX_FARE)});
        return view;
    }

    @OnClick(R.id.dateBtn)
    void onDateBtnClick() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), datePickerListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(cal.getTime().getTime() - Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        datePicker.show();
    }

    @OnClick(R.id.postBtn)
    void onPostBtnClick() {
        if (dateText.equals(getString(R.string.select_date)) || timeText.equals(getString(R.string.select_time)) ||
                TextUtils.isEmpty(pickupText.getText()) || TextUtils.isEmpty(destinationText.getText())
                || TextUtils.isEmpty(fareEt.getText()) || selectedJobType == Constants.DEFAULT_UNSELECTED_POSITION ||
                selectedVehicleType == Constants.DEFAULT_UNSELECTED_POSITION || TextUtils.isEmpty(instructionsET.getText())) {
            Toast.makeText(getActivity(), R.string.posting_empty, Toast.LENGTH_SHORT).show();
        } else {
            String datetime = outputFormatter.format(selectedDateTimeCalendar.getTime());
            double fare = Double.parseDouble(fareEt.getText().toString());
            double gratuity = TextUtils.isEmpty(gratuityEt.getText().toString()) ? 0 : Double.parseDouble(gratuityEt.getText().toString());
            presenter.postJobOffer(datetime, pickupText.getText().toString(), destinationText.getText().toString(), fare, gratuity, selectedVehicleType, selectedSuiteTie, selectedJobType,
                    instructionsET.getText().toString());
        }
    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateText.setText(getString(R.string.job_date, monthOfYear, dayOfMonth, year));
            selectedDateTimeCalendar.set(Calendar.YEAR, year);
            selectedDateTimeCalendar.set(Calendar.MONTH, monthOfYear);
            selectedDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };

    @OnClick(R.id.timeBtn)
    void onTimeBtnClick() {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog datePicker = new TimePickerDialog(getContext(), timePickerListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        datePicker.show();
    }

    @OnClick(R.id.vehicleTypeBtn)
    void onVehicleTypeBtnClick() {
        if (vehicleTypes != null && !vehicleTypes.isEmpty()) {
            showListDialog(getString(R.string.select_vehicle_type), vehicleTypes, vehicleTypeClickListener);
        }
    }

    @OnClick(R.id.jobTypeBtn)
    void onJobTypeClicked() {
        if (jobTypes != null && !jobTypes.isEmpty()) {
            showListDialog(getString(R.string.select_job_type), jobTypes, jobTypeClickListener);
        }
    }

    private DialogInterface.OnClickListener vehicleTypeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            selectedVehicleType = vehicleTypes.get(which).getId();
            vehicleTypeText.setText(vehicleTypes.get(which).getName());
        }
    };

    private DialogInterface.OnClickListener jobTypeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            selectedJobType = jobTypes.get(which).getId();
            jobTypeText.setText(jobTypes.get(which).getName());
        }
    };

    private void showListDialog(String title, List<CarOption> options, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        List<String> stringOptions = new ArrayList<String>();
        for (CarOption carOption : options) {
            stringOptions.add(carOption.getName());
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stringOptions);
        builder.setCancelable(true);
        builder.setAdapter(arrayAdapter, listener);
        builder.show();
    }

    @OnClick(R.id.suiteBtn)
    void onSuiteBtnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.select_suite_tie));
        stringOptions = new ArrayList<String>();
        stringOptions.add(getString(R.string.yes));
        stringOptions.add(getString(R.string.no));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stringOptions);
        builder.setCancelable(true);
        builder.setAdapter(arrayAdapter, suiteListener);
        builder.show();
    }

    private DialogInterface.OnClickListener suiteListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            selectedSuiteTie = (which == 0);
            suiteText.setText(stringOptions.get(which));
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            selectedDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTimeCalendar.set(Calendar.MINUTE, minute);
            String time = formatter.format(selectedDateTimeCalendar.getTime());
            timeText.setText(time);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }


    @Override
    public void startLoading() {
        postBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        postBtn.setEnabled(true);
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
    public void onVehicleTypesLoaded(List<CarOption> carOptions) {
        vehicleTypes = carOptions;
    }

    @Override
    public void onJobTypesLoaded(List<CarOption> carOptions) {
        jobTypes = carOptions;
    }

    @Override
    public void onPostCreatesSuccessfully() {
        getActivity().onBackPressed();
    }


}
