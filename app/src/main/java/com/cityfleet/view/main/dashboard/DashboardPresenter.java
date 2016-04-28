package com.cityfleet.view.main.dashboard;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cityfleet.model.ProfileImage;
import com.cityfleet.model.UserInfo;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.Constants;
import com.cityfleet.util.ScaleImageHelper;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardPresenter {
    private NetworkManager networkManager;
    private DashboardView view;

    public DashboardPresenter(NetworkManager networkManager, DashboardView view) {
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
            final RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            getScaledImageRequestBody(filepath, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Call<ProfileImage> call = networkManager.getNetworkClient().uploadAvatar((RequestBody) msg.obj, description);
                    call.enqueue(uploadImageCallback);
                }
            });

        } else {
            view.onNetworkError();
        }
    }

    private void getScaledImageRequestBody(final String imageUrl, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ScaleImageHelper scaleImageHelper = new ScaleImageHelper();
                byte[] bytes = scaleImageHelper.getScaledImageBytes(imageUrl, Constants.PROFILE_IMAGE_WIDTH);
                RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
                Message message = new Message();
                message.obj = fileRequestBody;
                handler.sendMessage(message);
            }
        };
        new Thread(runnable).start();
    }

    Callback<UserInfo> getUserInfoCallback = new Callback<UserInfo>() {
        @Override
        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.updateImage(response.body().getAvatarUrl());
                view.setName(response.body().getFullName());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<UserInfo> call, Throwable t) {
            if (t.getMessage() != null) {
                Log.e(DashboardPresenter.class.getName(), t.getMessage());
            }
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    Callback<ProfileImage> uploadImageCallback = new Callback<ProfileImage>() {
        @Override
        public void onResponse(Call<ProfileImage> call, Response<ProfileImage> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.updateImage(response.body().getAvatar());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<ProfileImage> call, Throwable t) {
            Log.e(DashboardPresenter.class.getName(), t.getMessage());
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
