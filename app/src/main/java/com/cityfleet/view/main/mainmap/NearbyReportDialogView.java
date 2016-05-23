package com.cityfleet.view.main.mainmap;

import android.animation.Animator;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityfleet.R;
import com.cityfleet.model.Report;
import com.cityfleet.model.ReportType;
import com.cityfleet.util.CommonUtils;
import com.cityfleet.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 07.04.16.
 */
public class NearbyReportDialogView {
    @Bind(R.id.titleMiles)
    TextView titleMiles;
    @Bind(R.id.stillHereBtn)
    Button stillHereBtn;
    @Bind(R.id.notHereBtn)
    Button notHereBtn;
    @Bind(R.id.nearReportDialog)
    LinearLayout nearReportDialog;
    MainMapFragment fragment;
    Report report;
    int dialogHeight;

    public NearbyReportDialogView(MainMapFragment fragment, View viewCont) {
        ButterKnife.bind(this, viewCont);
        this.fragment = fragment;
        waitForDialogToMeasure();
    }

    public void onDestroy() {
        ButterKnife.unbind(this);
    }

    public boolean isVisible() {
        return nearReportDialog.getVisibility() == View.VISIBLE;
    }

    public Report getSelectedReport() {
        return report;
    }

    private void waitForDialogToMeasure() {
        ViewTreeObserver viewTreeObserver = nearReportDialog.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        nearReportDialog.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        nearReportDialog.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    dialogHeight = nearReportDialog.getHeight();
                    nearReportDialog.setTranslationY(-dialogHeight);
                    nearReportDialog.setVisibility(View.GONE);
                }
            });
        }
    }

    public void show(Report report, Location currentLocation) {
        this.report = report;
        int iconResId = ReportType.values()[report.getReportType() - 1].getSmallIconResId();
        titleMiles.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        Location reportLocation = new Location("");
        reportLocation.setLatitude(report.getLat());
        reportLocation.setLongitude(report.getLng());
        float distanceInMeters = currentLocation.distanceTo(reportLocation);
        float distanceInMiles = CommonUtils.convertMetersToMiles(distanceInMeters);
        titleMiles.setText(fragment.getString(R.string.in_miles, fragment.getString(ReportType.values()[report.getReportType() - 1].getNameRes()), String.format("%.1f", distanceInMiles)));
        if (nearReportDialog.getVisibility() == View.GONE) {
            playShowHideAnimation();
        }
    }

    public void hide() {
        playShowHideAnimation();
    }

    private void playShowHideAnimation() {
        if (nearReportDialog.getVisibility() == View.VISIBLE) {
            nearReportDialog.animate().translationY(-dialogHeight).setDuration(Constants.DIALOG_ANIMATION_DURATION).setInterpolator(new AccelerateInterpolator()).
                    setListener(openingAnimatorListener).start();
        } else {
            nearReportDialog.setVisibility(View.VISIBLE);
            nearReportDialog.animate().translationY(0).setDuration(Constants.DIALOG_ANIMATION_DURATION).setInterpolator(new AccelerateInterpolator()).setListener(null).start();
        }
    }

    private Animator.AnimatorListener openingAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            nearReportDialog.setVisibility(View.GONE);
            report = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @OnClick(R.id.stillHereBtn)
    void onStillHereBtnClick() {
        if (report != null) {
            fragment.onReportConfirmDenyClicked(report.getId(), true);
        }
        hide();
    }

    @OnClick(R.id.notHereBtn)
    void onNotHereBtnClick() {
        if (report != null) {
            fragment.onReportConfirmDenyClicked(report.getId(), false);
        }
        hide();
    }


}
