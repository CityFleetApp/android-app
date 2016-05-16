package com.cityfleet.view.main.posting;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
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

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.CarOption;
import com.cityfleet.model.JobOffer;
import com.cityfleet.util.Constants;
import com.cityfleet.util.DecimalDigitsInputFilter;
import com.cityfleet.view.BaseFragment;

import org.parceler.Parcels;

import java.text.ParseException;
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
    SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.OUTPUT_DATE_FORMAT);
    SimpleDateFormat outputFormatter = new SimpleDateFormat(Constants.INPUT_DATETIME_FORMAT);
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.titleText)
    EditText titleText;
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
    @Bind(R.id.tollsET)
    EditText tollsEt;
    @Bind(R.id.vehicleTypeText)
    TextView vehicleTypeText;
    @Bind(R.id.companyPersonalText)
    TextView companyPersonalText;
    @Bind(R.id.suiteText)
    TextView suiteText;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.postBtn)
    Button postBtn;
    @Bind(R.id.updateBtn)
    Button updateBtn;
    @Bind(R.id.deleteBtn)
    Button deleteBtn;
    @Bind(R.id.jobTypeText)
    TextView jobTypeText;
    @Bind(R.id.instructionsET)
    EditText instructionsET;

    private List<String> stringOptions;
    private List<CarOption> vehicleTypes;
    private List<CarOption> jobTypes;

    private Calendar selectedDateTimeCalendar = Calendar.getInstance();
    private boolean isEditMode = false;
    private JobOffer jobOffer;

    private JobOfferPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.job_offer_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.job_offer);
        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.JOB_OFFER_TAG)) {
            isEditMode = true;
            jobOffer = Parcels.unwrap(getArguments().getParcelable(Constants.JOB_OFFER_TAG));
            initWithEditValues();
        } else {
            jobOffer = new JobOffer();
        }
        presenter = new JobOfferPresenter(CityFleetApp.getInstance().getNetworkManager(), this);
        presenter.loadVehicleAndJobTypes();
        fareEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(Constants.JOB_OFFER_MAX_FARE)});
        gratuityEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(Constants.JOB_OFFER_MAX_FARE)});
        tollsEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(Constants.JOB_OFFER_MAX_FARE)});
        return view;
    }

    private void initWithEditValues() {
        try {
            Date date = outputFormatter.parse(jobOffer.getDateTime());
            selectedDateTimeCalendar.setTime(date);
            dateText.setText(dateFormat.format(date));
            timeText.setText(formatter.format(date));
        } catch (ParseException e) {
            Log.e(JobOfferFragment.class.getName(), e.getMessage());
        }
        titleText.setText(jobOffer.getTitle());
        pickupText.setText(jobOffer.getPickupAddress());
        destinationText.setText(jobOffer.getDestination());
        fareEt.setText(String.valueOf(jobOffer.getFare()));
        gratuityEt.setText(String.valueOf(jobOffer.getGratuity()));
        tollsEt.setText(String.valueOf(jobOffer.getTolls()));
        String[] companyPersonal = getResources().getStringArray(R.array.company_personal_types);
        companyPersonalText.setText(jobOffer.getPersonal());
        jobOffer.setChoices(jobOffer.getPersonal().equalsIgnoreCase(companyPersonal[0]) ? 1 : 2);
        suiteText.setText(jobOffer.isSuite() ? getString(R.string.yes) : getString(R.string.no));
        instructionsET.setText(jobOffer.getInstructions());
        vehicleTypeText.setText(jobOffer.getVehicleType());
        jobTypeText.setText(jobOffer.getJobType());
        postBtn.setVisibility(View.GONE);
        deleteBtn.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.dateBtn)
    void onDateBtnClick() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), datePickerListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    @OnClick({R.id.postBtn, R.id.updateBtn})
    void onPostBtnClick() {
        if (TextUtils.isEmpty(titleText.getText()) || dateText.equals(getString(R.string.select_date)) || timeText.equals(getString(R.string.select_time)) ||
                TextUtils.isEmpty(pickupText.getText()) || TextUtils.isEmpty(destinationText.getText())
                || TextUtils.isEmpty(fareEt.getText()) || jobOffer.getJobTypeId() == Constants.DEFAULT_UNSELECTED_POSITION ||
                jobOffer.getVehicleTypeId() == Constants.DEFAULT_UNSELECTED_POSITION || TextUtils.isEmpty(instructionsET.getText()) || TextUtils.isEmpty(companyPersonalText.getText())) {
            Toast.makeText(getActivity(), R.string.posting_empty, Toast.LENGTH_SHORT).show();
        } else {
            double fare = Double.parseDouble(fareEt.getText().toString());
            jobOffer.setTitle(titleText.getText().toString());
            jobOffer.setPickupAddress(pickupText.getText().toString());
            jobOffer.setDestination(destinationText.getText().toString());
            jobOffer.setFare(fare);
            if (!TextUtils.isEmpty(tollsEt.getText())) {
                jobOffer.setTolls(Double.parseDouble(tollsEt.getText().toString()));
            }
            jobOffer.setGratuity(gratuityEt.getText().toString());
            jobOffer.setInstructions(instructionsET.getText().toString());
            String datetime = outputFormatter.format(selectedDateTimeCalendar.getTime());
            jobOffer.setDateTime(datetime);
            presenter.postJobOffer(jobOffer, isEditMode);
        }
    }

    @OnClick(R.id.deleteBtn)
    void onDeleteBtnClick() {
        presenter.deleteJobOffer(jobOffer.getId());
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectedDateTimeCalendar.set(Calendar.SECOND, 0);
            selectedDateTimeCalendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() > selectedDateTimeCalendar.getTimeInMillis()) {
                dateText.setText(getString(R.string.job_date, monthOfYear + 1, dayOfMonth, year));
                selectedDateTimeCalendar.set(Calendar.YEAR, year);
                selectedDateTimeCalendar.set(Calendar.MONTH, monthOfYear);
                selectedDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            } else {
                Toast.makeText(getContext(), R.string.invalid_date, Toast.LENGTH_SHORT).show();
            }
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

    @OnClick(R.id.companyPersonalBtn)
    void onCompanyPersonalClick() {
        String[] companyPersonalTypes = getResources().getStringArray(R.array.company_personal_types);
        List<CarOption> companyPersonal = new ArrayList<CarOption>();
        companyPersonal.add(new CarOption(0, companyPersonalTypes[0]));
        companyPersonal.add(new CarOption(1, companyPersonalTypes[1]));
        showListDialog(getString(R.string.select_company_personal), companyPersonal, companyPersonalClickListener);
    }

    private DialogInterface.OnClickListener vehicleTypeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            jobOffer.setVehicleTypeId(vehicleTypes.get(which).getId());
            vehicleTypeText.setText(vehicleTypes.get(which).getName());
        }
    };

    private DialogInterface.OnClickListener jobTypeClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            jobOffer.setJobTypeId(jobTypes.get(which).getId());
            jobTypeText.setText(jobTypes.get(which).getName());
        }
    };
    private DialogInterface.OnClickListener companyPersonalClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String[] companyPersonalTypes = getResources().getStringArray(R.array.company_personal_types);
            companyPersonalText.setText(companyPersonalTypes[which]);
            jobOffer.setChoices(which + 1);
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
            jobOffer.setSuite(which == 0);
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
        if (isEditMode) {
            for (CarOption car : carOptions) {
                if (car.getName().equals(jobOffer.getVehicleType())) {
                    jobOffer.setVehicleTypeId(car.getId());
                    break;
                }
            }
        }
    }

    @Override
    public void onJobTypesLoaded(List<CarOption> carOptions) {
        jobTypes = carOptions;
        if (isEditMode) {
            for (CarOption car : carOptions) {
                if (car.getName().equals(jobOffer.getJobType())) {
                    jobOffer.setJobTypeId(car.getId());
                    break;
                }
            }
        }
    }

    @Override
    public void onPostCreatesSuccessfully() {
        getActivity().onBackPressed();
    }


}
