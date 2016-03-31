package com.citifleet.view.main.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.CarOption;
import com.citifleet.model.UserEditInfo;
import com.citifleet.util.Constants;
import com.citifleet.util.EditUserCarEvent;
import com.citifleet.view.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 31.03.16.
 */
public class EditUserCarFragment extends BaseFragment implements EditUserCarPresenter.EditUserCarView {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.modelBtn)
    RelativeLayout modelBtn;
    @Bind(R.id.makeText)
    TextView makeText;
    @Bind(R.id.modelText)
    TextView modelText;
    @Bind(R.id.typeText)
    TextView typeText;
    @Bind(R.id.colorText)
    TextView colorText;
    @Bind(R.id.yearText)
    TextView yearText;
    @Bind(R.id.doneBtn)
    Button doneBtn;
    private List<CarOption> makeList;
    private List<CarOption> modelList;
    private List<CarOption> typesList;
    private List<CarOption> colorsList;
    private List<String> yearList;
    private EditUserCarPresenter presenter;
    private UserEditInfo copyOfUserEditInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_user_profile_car, container, false);
        ButterKnife.bind(this, view);
        UserEditInfo userEditInfo = Parcels.unwrap(getArguments().getParcelable(Constants.EDIT_USER_INFO_TAG));
        copyOfUserEditInfo = new UserEditInfo(userEditInfo); //copy object
        title.setText(getString(R.string.edit_account));
        presenter = new EditUserCarPresenter(this, CitiFleetApp.getInstance().getNetworkManager());
        presenter.init();
        if (userEditInfo.getCarMake() == Constants.DEFAULT_UNSELECTED_POSITION) {
            enableModelBtn(false);
        } else {
            presenter.getCarModelsByMakeId(userEditInfo.getCarMake());
        }
        if (!TextUtils.isEmpty(userEditInfo.getCarMakeDisplay())) {
            makeText.setText(userEditInfo.getCarMakeDisplay());
        }
        if (!TextUtils.isEmpty(userEditInfo.getCarModelDisplay())) {
            modelText.setText(userEditInfo.getCarModelDisplay());
        }
        if (!TextUtils.isEmpty(userEditInfo.getCarTypeDisplay())) {
            typeText.setText(userEditInfo.getCarTypeDisplay());
        }
        if (!TextUtils.isEmpty(userEditInfo.getCarColorDisplay())) {
            colorText.setText(userEditInfo.getCarColorDisplay());
        }
        if (userEditInfo.getCarYear() != Constants.DEFAULT_UNSELECTED_POSITION) {
            yearText.setText(String.valueOf(userEditInfo.getCarYear()));
        }

        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.doneBtn)
    void onDoneBtnClick() {
        if (copyOfUserEditInfo.getCarMake() != Constants.DEFAULT_UNSELECTED_POSITION && copyOfUserEditInfo.getCarModel() == Constants.DEFAULT_UNSELECTED_POSITION) {
            Toast.makeText(getContext(), getString(R.string.please_enter_model), Toast.LENGTH_SHORT).show();
            return;
        }
        EventBus.getDefault().post(new EditUserCarEvent(copyOfUserEditInfo));
        getActivity().onBackPressed();
    }

    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void startLoading() {
        doneBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        doneBtn.setEnabled(true);
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

    @OnClick(R.id.makeBtn)
    void onMakeBtnClick() {
        if (makeList != null && !makeList.isEmpty()) {
            showListDialog(getString(R.string.select_make), makeList, makeListener);
        }
    }

    @OnClick(R.id.yearBtn)
    void onYearBtnClick() {
        showYearDialog();
    }

    @OnClick(R.id.modelBtn)
    void onModelBtnClick() {
        if (modelList != null && !modelList.isEmpty()) {
            showListDialog(getString(R.string.select_model), modelList, modelListener);
        }
    }

    @OnClick(R.id.typeBtn)
    void onTypeBtnClick() {
        if (typesList != null && !typesList.isEmpty()) {
            showListDialog(getString(R.string.select_type), typesList, typeListener);
        }
    }

    @OnClick(R.id.colorBtn)
    void onColorBtnClick() {
        if (colorsList != null && !colorsList.isEmpty()) {
            showListDialog(getString(R.string.select_color), colorsList, colorListener);
        }
    }

    @Override
    public void onMakesLoaded(List<CarOption> makeList) {
        this.makeList = makeList;
    }

    @Override
    public void onModelLoaded(List<CarOption> modelList) {
        this.modelList = modelList;
        enableModelBtn(true);
    }

    private void enableModelBtn(boolean enable) {
        modelBtn.setClickable(enable);
        modelBtn.setAlpha(enable ? Constants.ENABLED_LAYOUT_ALPHA : Constants.DISABLED_LAYOUT_ALPHA);
    }

    @Override
    public void onTypeLoaded(List<CarOption> typeList) {
        this.typesList = typeList;
    }

    @Override
    public void onColorLoaded(List<CarOption> colorList) {
        this.colorsList = colorList;
    }

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

    private void showYearDialog() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if (Constants.MIN_POSTING_YEAR < currentYear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.select_year));
            yearList = new ArrayList<String>();
            for (int i = Constants.MIN_POSTING_YEAR; i <= currentYear; i++) {
                yearList.add(String.valueOf(i));
            }
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, yearList);
            builder.setCancelable(true);
            builder.setAdapter(arrayAdapter, dateListener);
            builder.show();
        }
    }

    private DialogInterface.OnClickListener makeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (makeList.get(which).getId() != copyOfUserEditInfo.getCarMake()) {
                copyOfUserEditInfo.setCarMake(makeList.get(which).getId());
                copyOfUserEditInfo.setCarMakeChanged(true);
                copyOfUserEditInfo.setCarMakeDisplay(makeList.get(which).getName());
                makeText.setText(makeList.get(which).getName());

                copyOfUserEditInfo.setCarModelDisplay(null);
                copyOfUserEditInfo.setCarModel(Constants.DEFAULT_UNSELECTED_POSITION);
                copyOfUserEditInfo.setCarModelChanged(true);
                if (modelList != null) {
                    modelList.clear();
                    modelText.setText(getString(R.string.select_model));
                }
                presenter.getCarModelsByMakeId(makeList.get(which).getId());
            }
        }
    };
    private DialogInterface.OnClickListener modelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (modelList.get(which).getId() != copyOfUserEditInfo.getCarModel()) {
                modelText.setText(modelList.get(which).getName());
                copyOfUserEditInfo.setCarModel(modelList.get(which).getId());
                copyOfUserEditInfo.setCarModelChanged(true);
                copyOfUserEditInfo.setCarModelDisplay(modelList.get(which).getName());
            }
        }
    };
    private DialogInterface.OnClickListener typeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (typesList.get(which).getId() != copyOfUserEditInfo.getCarType()) {
                typeText.setText(typesList.get(which).getName());
                copyOfUserEditInfo.setCarType(typesList.get(which).getId());
                copyOfUserEditInfo.setCarTypeChanged(true);
                copyOfUserEditInfo.setCarTypeDisplay(typesList.get(which).getName());
            }
        }
    };
    private DialogInterface.OnClickListener colorListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (colorsList.get(which).getId() != copyOfUserEditInfo.getCarColor()) {
                colorText.setText(colorsList.get(which).getName());
                copyOfUserEditInfo.setCarColor(colorsList.get(which).getId());
                copyOfUserEditInfo.setCarColorChanged(true);
                copyOfUserEditInfo.setCarColorDisplay(colorsList.get(which).getName());
            }
        }
    };

    private DialogInterface.OnClickListener dateListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (copyOfUserEditInfo.getCarYear() != Integer.parseInt(yearList.get(which))) {
                yearText.setText(yearList.get(which));
                copyOfUserEditInfo.setCarYear(Integer.parseInt(yearList.get(which)));
                copyOfUserEditInfo.setCarYearChanged(true);
            }
        }
    };
}
