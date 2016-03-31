package com.citifleet.view.main.settings;

import android.util.Log;

import com.citifleet.model.ProfileImage;
import com.citifleet.model.UserInfo;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 31.03.16.
 */
public class EditUserProfilePresenter {
    private NetworkManager networkManager;
    private EditUserProfileVIew view;

    public EditUserProfilePresenter(NetworkManager networkManager, EditUserProfileVIew view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void init() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
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

    Callback<ProfileImage> uploadImageCallback = new Callback<ProfileImage>() {
        @Override
        public void onResponse(Call<ProfileImage> call, Response<ProfileImage> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.updateProfileImage(response.body().getAvatar());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<ProfileImage> call, Throwable t) {
            Log.e(EditUserProfilePresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    Callback<UserInfo> getUserInfoCallback = new Callback<UserInfo>() {
        @Override
        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.updateProfileImage(response.body().getAvatarUrl());
                view.setName(response.body().getFullName());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<UserInfo> call, Throwable t) {
            Log.e(EditUserProfilePresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface EditUserProfileVIew {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void updateProfileImage(String url);

        void onNetworkError();

        void setName(String name);

        void setUserInfo();
    }
}
