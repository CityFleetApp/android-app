package com.cityfleet.view.main.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cityfleet.CityFleetApp;
import com.cityfleet.R;
import com.cityfleet.model.Notification;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.Constants;
import com.cityfleet.view.BaseActivity;
import com.cityfleet.view.BaseFragment;
import com.cityfleet.view.main.marketplace.JobInfoFragment;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 17.03.16.
 */
public class NotificationDetailFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.notificationTitle)
    TextView notificationTitle;
    @Bind(R.id.notificationMessage)
    TextView notificationMessage;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.seeJobBtn)
    Button seeJobBtn;
    private Notification notification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        if (args.containsKey(Constants.SELECTED_NOTIFICATION_TAG)) {
            notification = Parcels.unwrap(args.getParcelable(Constants.SELECTED_NOTIFICATION_TAG));
            initView();
        } else {
            int notificationId = args.getInt(Constants.NOTIFICATION_ID_TAG);
            loadNotificationInfo(notificationId);
        }

        return view;
    }

    private void loadNotificationInfo(int notificationId) {
        NetworkManager networkManager = CityFleetApp.getInstance().getNetworkManager();
        if (networkManager.isConnectedOrConnecting()) {
            Call<Notification> call = networkManager.getNetworkClient().getNotificationInfo(notificationId);
            call.enqueue(infoCallback);
        } else {
            Toast.makeText(getActivity(), getString(R.string.networkMesMoInternet), Toast.LENGTH_LONG).show();
        }
    }

    private Callback<Notification> infoCallback = new Callback<Notification>() {
        @Override
        public void onResponse(Call<Notification> call, Response<Notification> response) {
            if (response.isSuccessful()) {
                notification = response.body();
                initView();
            } else {
                String error = NetworkErrorUtil.gerErrorMessage(response);
                if (TextUtils.isEmpty(error)) {
                    error = getString(R.string.default_error_mes);
                }
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Notification> call, Throwable t) {
            Log.d(NotificationDetailFragment.class.getName(), t.getMessage());
            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void initView() {
        title.setText(getString(R.string.tlc_alert));
        notificationTitle.setText(notification.getTitle());
        notificationMessage.setText(notification.getMessage());
        String dateToShow = "";
        try {
            Date date = NotificationsListAdapter.simpleDateFormatFromServer.parse(notification.getCreated());
            dateToShow = NotificationsListAdapter.simpleDateFormatToShow.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setText(dateToShow);
        if (notification.getRefType().equals("offer_created")) {
            seeJobBtn.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.seeJobBtn)
    void onSeeJobBtnClicked() {
        JobInfoFragment jobInfoFragment = new JobInfoFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.JOB_OFFER_ID_TAG, notification.getRefId());
        jobInfoFragment.setArguments(args);
        ((BaseActivity) getActivity()).changeFragment(jobInfoFragment, true);
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
