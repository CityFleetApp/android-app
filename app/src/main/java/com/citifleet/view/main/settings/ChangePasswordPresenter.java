package com.citifleet.view.main.settings;

import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 15.03.16.
 */
public class ChangePasswordPresenter {
    private ChangePasswordView view;
    private NetworkManager networkManager;

    public ChangePasswordPresenter(ChangePasswordView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }

    public void changePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        if (networkManager.isConnectedOrConnecting()) {
            view.setLoading(true);
            Call<Void> call = networkManager.getNetworkClient().changePassword(oldPassword, newPassword, confirmNewPassword);
            call.enqueue(callback);
        } else {
            view.setLoading(false);
            view.onNetworkError();
        }
    }

    private Callback<Void> callback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            view.setLoading(false);
            if (response.isSuccess()) {
                view.onSuccess();
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            view.setLoading(false);
            view.onFailure(t.getMessage());
        }
    };

    public interface ChangePasswordView {

        void setLoading(boolean isLoading);

        void onFailure(String error);

        void onNetworkError();

        void onSuccess();

    }
}
