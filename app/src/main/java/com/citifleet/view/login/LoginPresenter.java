package com.citifleet.view.login;

import android.util.Log;

import com.citifleet.model.LoginInfo;
import com.citifleet.network.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter {
    private NetworkManager networkManager;
    private LoginView      view;

    public LoginPresenter(NetworkManager networkManager, LoginView loginView) {
        this.networkManager = networkManager;
        this.view = loginView;
    }

    public void login(String email, String password) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<LoginInfo> loginCall = networkManager.getNetworkClient().login(email, password);
            loginCall.enqueue(loginCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<LoginInfo> loginCallback = new Callback<LoginInfo>() {
        @Override
        public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.onLoginSuccess(response.body().getToken());
            } else {
                Log.e(LoginPresenter.class.getName(), response.message());
                view.onLoginFailure(response.message());
            }
        }

        @Override
        public void onFailure(Call<LoginInfo> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onLoginFailure(t.getMessage());
        }
    };

    public interface LoginView {
        void onLoginSuccess(String token);

        void startLoading();

        void stopLoading();

        void onLoginFailure(String error);

        void onNetworkError();
    }

}
