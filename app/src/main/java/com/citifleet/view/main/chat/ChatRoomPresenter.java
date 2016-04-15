package com.citifleet.view.main.chat;

import android.util.Log;

import com.citifleet.model.ChatRoom;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 12.04.16.
 */
public class ChatRoomPresenter {
    private ChatRoomsListView view;
    private NetworkManager networkManager;

    public ChatRoomPresenter(ChatRoomsListView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }


    public void loadAllChatRooms() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<ChatRoom>> call = networkManager.getNetworkClient().getChatRooms();
            call.enqueue(chatRoomsCallback);
        } else {
            view.onNetworkError();
        }
    }


    Callback<List<ChatRoom>> chatRoomsCallback = new Callback<List<ChatRoom>>() {
        @Override
        public void onResponse(Call<List<ChatRoom>> call, Response<List<ChatRoom>> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                view.onChatRoomsListLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<ChatRoom>> call, Throwable t) {
            Log.e(ChatRoomPresenter.class.getName(), t.getMessage());
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface ChatRoomsListView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onChatRoomsListLoaded(List<ChatRoom> chatRooms);
    }
}
