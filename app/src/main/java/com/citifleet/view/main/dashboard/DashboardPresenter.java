package com.citifleet.view.main.dashboard;

import android.util.Log;

import com.citifleet.model.ProfileImage;
import com.citifleet.model.UserInfo;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.view.login.LoginPresenter;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardPresenter {
    private NetworkManager networkManager;
    private DashboardView  view;

    public DashboardPresenter(NetworkManager networkManager, DashboardView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void init() {
        if (networkManager.isConnectedOrConnecting()) {
            Call<UserInfo> call = networkManager.getNetworkClient().getUserInfo();
            call.enqueue(getUserInfoCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void uploadImage(String filepath) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            File file = new File(filepath);
            RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            Call<ProfileImage> call = networkManager.getNetworkClient().uploadAvatar(requestBody, description);
            call.enqueue(uploadImageCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<UserInfo>     getUserInfoCallback = new Callback<UserInfo>() {
        @Override
        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.updateImage(response.body().getAvatarUrl());
                view.setName(response.body().getFullName());
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
    Callback<ProfileImage> uploadImageCallback = new Callback<ProfileImage>() {
        @Override
        public void onResponse(Call<ProfileImage> call, Response<ProfileImage> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.updateImage(response.body().getAvatar());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<ProfileImage> call, Throwable t) {
            Log.e(LoginPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface DashboardView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void updateImage(String url);

        void onNetworkError();

        void setName(String name);
    }


}
