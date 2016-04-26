package com.citifleet.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.citifleet.R;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.main.mainmap.MainMapFragment;
import com.citifleet.view.main.marketplace.JobInfoFragment;
import com.citifleet.view.main.notifications.NotificationDetailFragment;

public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra(Constants.JOB_OFFER_ID_TAG)) {
            int jobOfferId = intent.getIntExtra(Constants.JOB_OFFER_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            if (jobOfferId != Constants.DEFAULT_UNSELECTED_POSITION) {
                changeFragment(getJobOfferFragment(jobOfferId), false);
            }
        } else if(intent.hasExtra(Constants.NOTIFICATION_ID_TAG)){
            int notifId = intent.getIntExtra(Constants.NOTIFICATION_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            if (notifId != Constants.DEFAULT_UNSELECTED_POSITION) {
                changeFragment(getNotificationFragment(notifId), false);
            }
        }
    }

    @Override
    protected Fragment getInitFragment() {
        if (getIntent().hasExtra(Constants.JOB_OFFER_ID_TAG)) {
            int jobOfferId = getIntent().getIntExtra(Constants.JOB_OFFER_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            return getJobOfferFragment(jobOfferId);
        } else if (getIntent().hasExtra(Constants.SELECTED_NOTIFICATION_TAG)) {
            int notifId = getIntent().getIntExtra(Constants.SELECTED_NOTIFICATION_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            return getNotificationFragment(notifId);
        } else {
            return new MainMapFragment();
        }
    }

    private JobInfoFragment getJobOfferFragment(int jobOfferId) {
        JobInfoFragment jobInfoFragment = new JobInfoFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.JOB_OFFER_ID_TAG, jobOfferId);
        jobInfoFragment.setArguments(args);
        return jobInfoFragment;
    }

    private NotificationDetailFragment getNotificationFragment(int notificationId) {
        NotificationDetailFragment notificationDetailFragment = new NotificationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.NOTIFICATION_ID_TAG, notificationId);
        notificationDetailFragment.setArguments(args);
        return notificationDetailFragment;
    }

}
