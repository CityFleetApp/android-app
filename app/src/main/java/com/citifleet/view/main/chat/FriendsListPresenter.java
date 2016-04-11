package com.citifleet.view.main.chat;

import android.util.Log;

import com.citifleet.model.Friend;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 11.04.16.
 */
public class FriendsListPresenter {
    private FriendsListView view;
    private NetworkManager networkManager;
    private List<Friend> friendList;

    public FriendsListPresenter(FriendsListView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }


    public void loadAllFriends() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<Friend>> call = networkManager.getNetworkClient().getChatFriends();
            call.enqueue(friendCallback);
        } else {
            view.onNetworkError();
        }
    }


    Callback<List<Friend>> friendCallback = new Callback<List<Friend>>() {
        @Override
        public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.onFriendsLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<Friend>> call, Throwable t) {
            Log.e(FriendsListPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface FriendsListView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onFriendsLoaded(List<Friend> friends);
    }
}
