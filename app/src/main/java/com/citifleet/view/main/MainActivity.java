package com.citifleet.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.citifleet.R;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.main.mainmap.MainMapFragment;
import com.citifleet.view.main.marketplace.JobInfoFragment;

public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int jobOfferId = intent.getIntExtra(Constants.JOB_OFFER_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
        if (jobOfferId != Constants.DEFAULT_UNSELECTED_POSITION) {
            changeFragment(getJobOfferFragment(jobOfferId), false);
        }
    }

    @Override
    protected Fragment getInitFragment() {
        if (getIntent().hasExtra(Constants.JOB_OFFER_ID_TAG)) {
            int jobOfferId = getIntent().getIntExtra(Constants.JOB_OFFER_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            return getJobOfferFragment(jobOfferId);
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
}
