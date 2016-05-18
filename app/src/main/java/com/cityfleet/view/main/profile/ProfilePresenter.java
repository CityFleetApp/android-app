package com.cityfleet.view.main.profile;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cityfleet.CityFleetApp;
import com.cityfleet.model.ChatRoom;
import com.cityfleet.model.UserImages;
import com.cityfleet.model.UserInfo;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.Constants;
import com.cityfleet.util.ScaleImageHelper;
import com.cityfleet.view.login.LoginPresenter;
import com.cityfleet.view.main.chat.FriendsListPresenter;

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
    private boolean isFriendProfile = false;
    private int friendId;

    public ProfilePresenter(NetworkManager networkManager, ProfileView view, boolean isFriendProfile, int friendId) {
        this.networkManager = networkManager;
        this.view = view;
        this.friendId = friendId;
        this.isFriendProfile = isFriendProfile;
    }

    public void init() {
        if (networkManager.isConnectedOrConnecting()) {
            Call<UserInfo> call;
            Call<List<UserImages>> userImagesCall;
            if (isFriendProfile) {
                call = networkManager.getNetworkClient().getFriendInfo(friendId);
                userImagesCall = networkManager.getNetworkClient().getFriendPhotos(friendId);
            } else {
                call = networkManager.getNetworkClient().getUserInfo();
                userImagesCall = networkManager.getNetworkClient().getPhotos();
            }
            call.enqueue(getUserInfoCallback);
            userImagesCall.enqueue(getUserImagesCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void createChatRoomWithFriend(int friendId) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<ChatRoom> call = networkManager.getNetworkClient().createChatRoom(String.valueOf(friendId), new int[]{friendId});
            call.enqueue(newChatRoomCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void uploadImageForList(String filePath) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            final RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            getScaledImageRequestBody(filePath, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Call<UserImages> call = networkManager.getNetworkClient().uploadPhoto((RequestBody) msg.obj, description);
                    call.enqueue(uploadImageCallback);
                }
            });

        } else {
            view.onNetworkError();
        }
    }

    private Callback<ChatRoom> newChatRoomCallback = new Callback<ChatRoom>() {
        @Override
        public void onResponse(Call<ChatRoom> call, Response<ChatRoom> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onChatRoomCreated(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<ChatRoom> call, Throwable t) {
            Log.e(FriendsListPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

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

    public void deleteImageFromList(int id) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<Void> call = CityFleetApp.getInstance().getNetworkManager().getNetworkClient().deletePhoto(id);
            call.enqueue(deleteImageCallback);
        } else {
            view.onNetworkError();
        }
    }

    private Callback<Void> deleteImageCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onDeleteImageSuccess();
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };
    Callback<UserInfo> getUserInfoCallback = new Callback<UserInfo>() {
        @Override
        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
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
            if (response.isSuccessful()) {
                view.updateImageFromList(response.body());
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
            if (response.isSuccessful()) {
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

        void updateImageFromList(UserImages userImages);

        void onDeleteImageSuccess();

        void onUserImagesLoaded(List<UserImages> list);

        void onChatRoomCreated(ChatRoom chatRoom);
    }
}
