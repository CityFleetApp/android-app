package com.citifleet.view.login;

import android.util.Log;

import com.citifleet.model.LoginInfo;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationPresenter {
    private NetworkManager   networkManager;
    private RegistrationView view;

    public RegistrationPresenter(NetworkManager networkManager, RegistrationView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void signup(String fullName, String username, String phone, String hackLicense,
                       String email, String password, String passwordConfirm) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<LoginInfo> loginCall = networkManager.getNetworkClient().signup(fullName, username, phone, hackLicense, email, password, passwordConfirm);
            loginCall.enqueue(signupCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<LoginInfo> signupCallback = new Callback<LoginInfo>() {
        @Override
        public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onSignUpSuccess(response.body().getToken());
            } else {
                view.onSignUpFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<LoginInfo> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onSignUpFailure(t.getMessage());
        }
    };

    public interface RegistrationView {
        void onSignUpSuccess(String token);

        void startLoading();

        void stopLoading();

        void onSignUpFailure(String error);

        void onNetworkError();

    }
}
