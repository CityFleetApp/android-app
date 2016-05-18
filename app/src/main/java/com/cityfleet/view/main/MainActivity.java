package com.cityfleet.view.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.AddContactsBody;
import com.cityfleet.model.UserInfo;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.CommonUtils;
import com.cityfleet.util.Constants;
import com.cityfleet.util.PermissionUtil;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.main.mainmap.MainMapFragment;
import com.cityfleet.view.main.marketplace.JobInfoFragment;
import com.cityfleet.view.main.marketplace.JobOfferCompletedFragment;
import com.cityfleet.view.main.notifications.NotificationDetailFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_PERMISSION_CONTACTS = 345;
    private ContactsResultHandler contactsResultHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        syncContacts();
    }

    private void syncContacts() {
        requestContactPermission();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(Constants.JOB_OFFER_ID_TAG) && intent.hasExtra(Constants.JOB_OFFER_EXECUTOR_TAG)) {
            int jobOfferId = intent.getIntExtra(Constants.JOB_OFFER_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            String title = intent.getStringExtra(Constants.JOB_OFFER_TITLE_TAG);
            String driverName = intent.getStringExtra(Constants.JOB_OFFER_EXECUTOR_TAG);
            changeFragment(getJobOfferCompletedFragment(jobOfferId, title, driverName), false);
        } else if (intent.hasExtra(Constants.JOB_OFFER_ID_TAG)) {
            int jobOfferId = intent.getIntExtra(Constants.JOB_OFFER_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            if (jobOfferId != Constants.DEFAULT_UNSELECTED_POSITION) {
                changeFragment(getJobOfferFragment(jobOfferId), false);
            }
        } else if (intent.hasExtra(Constants.NOTIFICATION_ID_TAG)) {
            int notifId = intent.getIntExtra(Constants.NOTIFICATION_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            if (notifId != Constants.DEFAULT_UNSELECTED_POSITION) {
                changeFragment(getNotificationFragment(notifId), false);
            }
        }

    }

    @Override
    protected Fragment getInitFragment() {
        if (getIntent().hasExtra(Constants.JOB_OFFER_ID_TAG) && getIntent().hasExtra(Constants.JOB_OFFER_EXECUTOR_TAG)) {
            int jobOfferId = getIntent().getIntExtra(Constants.JOB_OFFER_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            String title = getIntent().getStringExtra(Constants.JOB_OFFER_TITLE_TAG);
            String driverName = getIntent().getStringExtra(Constants.JOB_OFFER_EXECUTOR_TAG);
            return getJobOfferCompletedFragment(jobOfferId, title, driverName);
        } else if (getIntent().hasExtra(Constants.JOB_OFFER_ID_TAG)) {
            int jobOfferId = getIntent().getIntExtra(Constants.JOB_OFFER_ID_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            return getJobOfferFragment(jobOfferId);
        } else if (getIntent().hasExtra(Constants.SELECTED_NOTIFICATION_TAG)) {
            int notifId = getIntent().getIntExtra(Constants.SELECTED_NOTIFICATION_TAG, Constants.DEFAULT_UNSELECTED_POSITION);
            return getNotificationFragment(notifId);
        } else {
            return new MainMapFragment();
        }
    }

    private JobOfferCompletedFragment getJobOfferCompletedFragment(int id, String title, String driverName) {
        JobOfferCompletedFragment fragment = new JobOfferCompletedFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.JOB_OFFER_ID_TAG, id);
        args.putString(Constants.JOB_OFFER_TITLE_TAG, title);
        args.putString(Constants.JOB_OFFER_EXECUTOR_TAG, driverName);
        fragment.setArguments(args);
        return fragment;
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

    private void requestContactPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CONTACTS);
        } else {
            getAllPhoneNumbersFromContacts();
        }
    }

    private void getAllPhoneNumbersFromContacts() {
        contactsResultHandler = new ContactsResultHandler(this);
        Thread thread = new Thread(threadToRetrieveContacts);
        thread.start();
    }

    private Runnable threadToRetrieveContacts = new Runnable() {
        @Override
        public void run() {
            List<String> allPhoneNumbers = CommonUtils.getAllPhoneNumbers(MainActivity.this);
            Message message = new Message();
            message.obj = allPhoneNumbers;
            contactsResultHandler.sendMessage(message);
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CONTACTS) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted
                getAllPhoneNumbersFromContacts();
            }
        }
    }

    static class ContactsResultHandler extends Handler {
        WeakReference<MainActivity> activityRef;

        ContactsResultHandler(MainActivity activity) {
            this.activityRef = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            MainActivity activity = this.activityRef.get();
            List<String> phoneNumbers = (List<String>) message.obj;
            if (activity != null) {
                activity.uploadFriendsFromContacts(phoneNumbers);
            }
        }
    }

    private void uploadFriendsFromContacts(List<String> phoneNumbers) {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            Call<List<UserInfo>> call = networkManager.getNetworkClient().addFriendsFromContacts(new AddContactsBody(phoneNumbers));
            call.enqueue(callback);
        }
    }

    private Callback<List<UserInfo>> callback = new Callback<List<UserInfo>>() {
        @Override
        public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
            if (!response.isSuccessful()) {
                String message = NetworkErrorUtil.gerErrorMessage(response);
                if (!TextUtils.isEmpty(message)) {
                    Log.e(MainActivity.class.getName(), message);
                }
            }
        }

        @Override
        public void onFailure(Call<List<UserInfo>> call, Throwable t) {
            if (!TextUtils.isEmpty(t.getMessage())) {
                Log.e(MainActivity.class.getName(), t.getMessage());
            }
        }
    };
}
