package com.cityfleet.view.main.addfriends;

import android.util.Log;

import com.cityfleet.model.AddContactsBody;
import com.cityfleet.model.UserInfo;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendsPresenter {
    private NetworkManager networkManager;
    private AddFriendsView view;

    public AddFriendsPresenter(AddFriendsView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }

    public void addFriendsFromContacts(List<String> phoneNumbers) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<UserInfo>> call = networkManager.getNetworkClient().addFriendsFromContacts(new AddContactsBody(phoneNumbers));
            call.enqueue(callback);
        } else {
            view.stopLoading();
            view.onNetworkError();
        }
    }

    public void addFacebookFriends(String token) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<UserInfo>> call = networkManager.getNetworkClient().addFacebookFriends(token);
            call.enqueue(callback);
        } else {
            view.stopLoading();
            view.onNetworkError();
        }
    }

    public void addTwitterFriends(String token, String tokenSecret) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<UserInfo>> call = networkManager.getNetworkClient().addTwitterFriends(token, tokenSecret);
            call.enqueue(callback);
        } else {
            view.stopLoading();
            view.onNetworkError();
        }
    }

    public void addInstagramFriends(String token) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<UserInfo>> call = networkManager.getNetworkClient().addInstagramFriends(token);
            call.enqueue(callback);
        } else {
            view.stopLoading();
            view.onNetworkError();
        }
    }

    Callback<List<UserInfo>> callback = new Callback<List<UserInfo>>() {
        @Override
        public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
            view.stopLoading();
            if (!response.isSuccessful()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            } else {
                view.onSuccess();
            }
        }

        @Override
        public void onFailure(Call<List<UserInfo>> call, Throwable t) {
            Log.e(AddFriendsPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface AddFriendsView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onSuccess();
    }
}
