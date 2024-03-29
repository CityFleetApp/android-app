package com.cityfleet.view.main.mainmap;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.cityfleet.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportDialogFragment extends DialogFragment {
    @Bind({R.id.policeBtn, R.id.tlcBtn, R.id.accidentBtn, R.id.traficJamBtn, R.id.hazardBtn, R.id.roadClosureBtn})
    List<TextView> reportButtons;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_dialog, null);
        ButterKnife.bind(this, v);
        getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ((MainMapFragment) getTargetFragment()).onReportDialogClosed();
            }
        });
        getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ((MainMapFragment) getTargetFragment()).onReportDialogClosed();
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        ((MainMapFragment) getTargetFragment()).onReportDialogClosed();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = CityFleetApp.getInstance().getRefWatcher();
//        refWatcher.watch(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @OnClick(R.id.closeBtn)
    void onCloseBtnClick() {
        getDialog().dismiss();
        ((MainMapFragment) getTargetFragment()).onReportDialogClosed();
    }

    @OnClick({R.id.policeBtn, R.id.tlcBtn, R.id.accidentBtn, R.id.traficJamBtn, R.id.hazardBtn, R.id.roadClosureBtn})
    void onReportBtnClick(TextView view) {
        ((MainMapFragment) getTargetFragment()).onReportItemClick(reportButtons.indexOf(view));
        getDialog().dismiss();
    }
}
