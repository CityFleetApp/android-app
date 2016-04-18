package com.citifleet.view.main.chat;

import android.util.Log;

import com.citifleet.model.ChatFriend;
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

    public FriendsListPresenter(FriendsListView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }


    public void loadAllFriends() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<ChatFriend>> call = networkManager.getNetworkClient().getChatFriends();
            call.enqueue(friendCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void createChatRoom(int participantId) {

    }

    Callback<List<ChatFriend>> friendCallback = new Callback<List<ChatFriend>>() {
        @Override
        public void onResponse(Call<List<ChatFriend>> call, Response<List<ChatFriend>> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.onFriendsLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<ChatFriend>> call, Throwable t) {
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

        void onFriendsLoaded(List<ChatFriend> friends);
    }
}
