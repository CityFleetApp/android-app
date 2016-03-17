package com.citifleet.view.main.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citifleet.CitiFleetApp;
import com.citifleet.R;
import com.citifleet.model.Notification;
import com.citifleet.util.Constants;
import com.citifleet.util.DividerItemDecoration;
import com.citifleet.view.BaseActivity;
import com.citifleet.view.BaseFragment;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vika on 17.03.16.
 */
public class NotificationsFragment extends BaseFragment implements NotificationPresenter.NotificationsView, NotificationsListAdapter.OnItemClickListener {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.notificationsListView)
    RecyclerView notificationsListView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    private NotificationPresenter presenter;
    private NotificationsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);
        ButterKnife.bind(this, view);
        title.setText(getString(R.string.notifications));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notificationsListView.setLayoutManager(layoutManager);
        adapter = new NotificationsListAdapter(this);
        notificationsListView.setAdapter(adapter);
        notificationsListView.addItemDecoration(new DividerItemDecoration(getActivity()));
        presenter = new NotificationPresenter(this, CitiFleetApp.getInstance().getNetworkManager());
        presenter.loadNotifications();
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


    @OnClick(R.id.allBtn)
    void onAllBtnClick() {
        presenter.onAllNotificationsBtnClicked();
    }

    @OnClick(R.id.unreadBtn)
    void unreadBtnClick() {
        presenter.onUnreadNotificationsBtnClicked();
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
    public void onNotificationsLoaded(List<Notification> notifications) {
        adapter.setNotifications(notifications);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Notification item) {
        presenter.setNotificationIsSeen(item.getId());
        Fragment fragment = new NotificationDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.SELECTED_NOTIFICATION_TAG, Parcels.wrap(item));
        fragment.setArguments(bundle);
        ((BaseActivity) getActivity()).changeFragment(fragment, true);
    }
}
