package com.citifleet.view.main.settings;

import android.util.Log;

import com.citifleet.model.ProfileImage;
import com.citifleet.model.UserEditInfo;
import com.citifleet.model.UserInfo;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.io.File;
import java.util.Calendar;

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



    public void initImage() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<UserInfo> call = networkManager.getNetworkClient().getUserInfo();
            call.enqueue(getUserInfoCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void initInfo() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<UserEditInfo> userEditInfoCall = networkManager.getNetworkClient().getUserProfileInfo();
            userEditInfoCall.enqueue(getEditInfoUserCallback);
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

    public void updateUserInfo(UserEditInfo info) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            String bio = null, username = null, phone = null, carMake = null, carModel = null, carType = null, carColor = null, carYear = null;
            if (info.isBioChanged()) {
                bio = info.getBio();
            }
            if (info.isPhoneChanged()) {
                phone = info.getPhone();
            }
            if (info.isUsernameChanged()) {
                username = info.getUsername();
            }
            if (info.isCarMakeChanged()) {
                carMake = String.valueOf(info.getCarMake());
            }
            if (info.isCarModelChanged()) {
                carModel = String.valueOf(info.getCarModel());
            }
            if (info.isCarColorChanged()) {
                carColor = String.valueOf(info.getCarColor());
            }
            if (info.isCarTypeChanged()) {
                carType = String.valueOf(info.getCarType());
            }
            if (info.isCarYearChanged()) {
                carYear = String.valueOf(info.getCarYear());
            }
            Call<Void> updateCall = networkManager.getNetworkClient().updateUserProfileInfo(bio, username, phone, carMake, carModel, carType, carColor, carYear);
            updateCall.enqueue(updateUserInfoCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<Void> updateUserInfoCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onInfoUpdateSuccessfully();
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.e(EditUserProfilePresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    Callback<UserEditInfo> getEditInfoUserCallback = new Callback<UserEditInfo>() {
        @Override
        public void onResponse(Call<UserEditInfo> call, Response<UserEditInfo> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.setUserInfo(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<UserEditInfo> call, Throwable t) {
            Log.e(EditUserProfilePresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
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

        void setUserInfo(UserEditInfo info);

        void onInfoUpdateSuccessfully();
    }
}
