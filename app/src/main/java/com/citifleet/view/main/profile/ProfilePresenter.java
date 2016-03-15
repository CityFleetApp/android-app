package com.citifleet.view.main.profile;

import android.util.Log;

import com.citifleet.model.UserImages;
import com.citifleet.model.UserInfo;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.view.login.LoginPresenter;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
            Call<UserInfo> call = networkManager.getNetworkClient().getUserInfo();
            call.enqueue(getUserInfoCallback);
            Call<List<UserImages>> userImagesCall = networkManager.getNetworkClient().getPhotos();
            userImagesCall.enqueue(getUserImagesCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void uploadImageForList(int position, String filePath) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            File file = new File(filePath);
            RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            Call<UserImages> call = networkManager.getNetworkClient().uploadPhoto(requestBody, description);
            call.enqueue(uploadImageCallback);
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

    Callback<UserImages> uploadImageCallback = new Callback<UserImages>() {
        @Override
        public void onResponse(Call<UserImages> call, Response<UserImages> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.updateImageFromList(0, response.body().getFile());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<UserImages> call, Throwable t) {
            Log.e(ProfilePresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    Callback<List<UserImages>> getUserImagesCallback = new Callback<List<UserImages>>() {
        @Override
        public void onResponse(Call<List<UserImages>> call, Response<List<UserImages>> response) {
            if (response.isSuccess()) {
                view.onUserImagesLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<UserImages>> call, Throwable t) {
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

        void updateImageFromList(int position, String url);

        void onUserImagesLoaded(List<UserImages> list);
    }
}
