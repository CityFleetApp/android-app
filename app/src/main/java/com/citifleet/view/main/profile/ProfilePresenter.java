package com.citifleet.view.main.profile;

import android.util.Log;

import com.citifleet.CitiFleetApp;
import com.citifleet.model.UserInfo;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.view.login.LoginPresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 12.03.16.
 */
public class ProfilePresenter {
    private NetworkManager networkManager;
    private ProfileView view;

    public ProfilePresenter(NetworkManager networkManager, ProfileView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void init() {
        if (networkManager.isConnectedOrConnecting()) {
            Call<UserInfo> call = CitiFleetApp.getInstance().getNetworkManager().getNetworkClient().getUserInfo();
            call.enqueue(getUserInfoCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<UserInfo> getUserInfoCallback = new Callback<UserInfo>() {
        @Override
        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.updateImage(response.body().getAvatarUrl());
                view.setUserInfo(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<UserInfo> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface ProfileView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void updateImage(String url);

        void onNetworkError();

        void setUserInfo(UserInfo userInfo);
    }
}
