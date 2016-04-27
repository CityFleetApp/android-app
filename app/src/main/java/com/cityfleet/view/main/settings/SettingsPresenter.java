package com.cityfleet.view.main.settings;

import com.cityfleet.model.Settings;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 18.03.16.
 */
public class SettingsPresenter {
    private SettingsView view;
    private NetworkManager networkManager;
    private Settings settings;

    public SettingsPresenter(SettingsView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }

    public void loadSettings() {
        if (networkManager.isConnectedOrConnecting()) {
            view.setLoading(true);
            Call<Settings> call = networkManager.getNetworkClient().getSettings();
            call.enqueue(callback);
        } else {
            view.setLoading(false);
            view.onNetworkError();
        }
    }

    public void changeStatusVisible(boolean visible) {
        if (settings!=null &&settings.isVisible() != visible) {
            settings.setVisible(visible);
            putChangesToServer();
        }
    }

    public void changeNotificationsEnabled(boolean enabled) {
        if (settings!=null && settings.isNotificationsEnabled() != enabled) {
            settings.setNotificationsEnabled(enabled);
            putChangesToServer();
        }
    }

    public void changeChatPrivacy(boolean chatPrivacy) {
        if (settings!=null &&settings.isChatPrivacy() != chatPrivacy) {
            settings.setChatPrivacy(chatPrivacy);
            putChangesToServer();
        }
    }

    private void putChangesToServer() {
        if (networkManager.isConnectedOrConnecting()) {
            view.setLoading(true);
            Call<Settings> call = networkManager.getNetworkClient().changeSettings(settings.isNotificationsEnabled(), settings.isChatPrivacy(), settings.isVisible());
            call.enqueue(callback);
        } else {
            view.setLoading(false);
            view.onNetworkError();
        }
    }

    private Callback<Settings> callback = new Callback<Settings>() {
        @Override
        public void onResponse(Call<Settings> call, Response<Settings> response) {
            view.setLoading(false);
            if (response.isSuccessful()) {
                settings = response.body();
                view.setStatusVisible(settings.isVisible());
                view.setNotificationsEnabled(settings.isNotificationsEnabled());
                view.setChatPrivacy(settings.isChatPrivacy());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Settings> call, Throwable t) {
            view.setLoading(false);
            view.onFailure(t.getMessage());
        }
    };

    public interface SettingsView {
        void setLoading(boolean isLoading);

        void onFailure(String error);

        void onNetworkError();

        void setStatusVisible(boolean visible);

        void setNotificationsEnabled(boolean enabled);

        void setChatPrivacy(boolean chatPrivacy);
    }
}
