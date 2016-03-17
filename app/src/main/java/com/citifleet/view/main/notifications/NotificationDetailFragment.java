package com.citifleet.view.main.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.citifleet.R;
import com.citifleet.model.Notification;
import com.citifleet.util.Constants;
import com.citifleet.view.BaseFragment;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private Notification notification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        notification = Parcels.unwrap(getArguments().getParcelable(Constants.SELECTED_NOTIFICATION_TAG));
        title.setText(notification.getType());
        notificationTitle.setText(notification.getTitle());
        notificationMessage.setText(notification.getMessage());
        date.setText(notification.getCreated());
        return view;
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
