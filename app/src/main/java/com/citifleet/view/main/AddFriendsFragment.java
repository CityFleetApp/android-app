package com.citifleet.view.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.util.PermissionUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFriendsFragment extends Fragment implements AddFriendsPresenter.AddFriendsView {
    private static final int REQUEST_PERMISSION_CONTACTS = 1;
    @Bind(R.id.title)
    TextView    title;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @BindString(R.string.default_error_mes)
    String      defaultErrorMes;
    private AddFriendsPresenter   presenter;
    private ContactsResultHandler contactsResultHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(R.string.add_friends_title);
        presenter = new AddFriendsPresenter(this, CitiFleetApp.getInstance().getNetworkManager());
        return view;
    }

    @OnClick(R.id.backBtn)
    void onBackBtnClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.contactsBtn)
    void onContactsBtnClick() {
        requestContactPermission();
    }

    @OnClick(R.id.facebookBtn)
    void onFacebookBtnClick() {

    }

    @OnClick(R.id.twitterBtn)
    void onTwitterBtnClick() {

    }

    @OnClick(R.id.instagramBtn)
    void onInstagramBtnClick() {

    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String error) {
        if (getActivity() != null) {
            if (error == null) {
                error = defaultErrorMes;
            }
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkError() {
        Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
    }

    private void requestContactPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // permissions have not been granted.
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CONTACTS);
        } else {
            getAllPhoneNumbersFromContacts();
        }
    }

    private void getAllPhoneNumbersFromContacts() {
        startLoading();
        contactsResultHandler = new ContactsResultHandler(this);
        Thread thread = new Thread(threadToRetrieveContacts);
        thread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CONTACTS) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted
                getAllPhoneNumbersFromContacts();
            }
        }
    }

    private Runnable threadToRetrieveContacts = new Runnable() {
        @Override
        public void run() {
            Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            List<String> allPhoneNumbers = new ArrayList<String>();
            while (phones.moveToNext()) {
                allPhoneNumbers.add(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
            phones.close();
            Message message = new Message();
            message.obj = allPhoneNumbers;
            contactsResultHandler.sendMessage(message);
        }
    };

    static class ContactsResultHandler extends Handler {
        WeakReference<AddFriendsFragment> fragmentRef;

        ContactsResultHandler(AddFriendsFragment fragment) {
            this.fragmentRef = new WeakReference<AddFriendsFragment>(fragment);
        }

        @Override
        public void handleMessage(Message message) {
            AddFriendsFragment fragment = this.fragmentRef.get();
            List<String> phoneNumbers = (List<String>) message.obj;
            fragment.presenter.addFriendsFromContacts(phoneNumbers);
        }
    }
}
