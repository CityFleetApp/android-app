package com.cityfleet.view.main.notifications;

import android.util.Log;

import com.cityfleet.model.Notification;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 17.03.16.
 */
public class NotificationPresenter {
    private NotificationsView view;
    private NetworkManager networkManager;
    private List<Notification> notifications;
    private List<Notification> unreadNotifications;

    public NotificationPresenter(NotificationsView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }


    public void loadNotifications() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<Notification>> call = networkManager.getNetworkClient().getNotifications();
            call.enqueue(notificationCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void setNotificationIsSeen(int id) {
        Call<Void> call = networkManager.getNetworkClient().markNotificationSeen(id);
        call.enqueue(markSeenNotificationCallback);
    }

    public void onAllNotificationsBtnClicked() {
        if (notifications != null) {
            view.onNotificationsLoaded(notifications);
        }
    }

    public void onUnreadNotificationsBtnClicked() {
        if (notifications != null) {
            unreadNotifications = new ArrayList<Notification>();
            for (Notification n : notifications) {
                if (n.isUnseen()) {
                    unreadNotifications.add(n);
                }
            }
            view.onNotificationsLoaded(unreadNotifications);
        }
    }

    Callback<List<Notification>> notificationCallback = new Callback<List<Notification>>() {
        @Override
        public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                notifications = response.body();
                view.onNotificationsLoaded(notifications);
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<Notification>> call, Throwable t) {
            Log.e(NotificationPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    Callback<Void> markSeenNotificationCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (!response.isSuccessful()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.e(NotificationPresenter.class.getName(), t.getMessage());
        }
    };

    public interface NotificationsView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onNotificationsLoaded(List<Notification> notifications);
    }
}
