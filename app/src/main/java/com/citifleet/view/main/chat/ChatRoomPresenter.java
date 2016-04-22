package com.citifleet.view.main.chat;

import android.util.Log;

import com.citifleet.model.ChatRoom;
import com.citifleet.model.PagesResult;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 12.04.16.
 */
public class  ChatRoomPresenter {
    private ChatRoomsListView view;
    private NetworkManager networkManager;
    private int offset = 0;
    private boolean isLoading = false;

    public ChatRoomPresenter(ChatRoomsListView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }

    public void loadNextPage() {
        if (!isLoading) {
            loadAllChatRooms(null);
        }
    }

    public void search(String query) {
        if (!isLoading) {
            if(query.length()==0){
                query = null;
            }
            offset = 0;
            view.clearList();
            loadAllChatRooms(query);
        }
    }


    private void loadAllChatRooms(String query) {
        if (networkManager.isConnectedOrConnecting()) {
            isLoading = true;
            view.startLoading();
            Call<PagesResult<ChatRoom>> call = networkManager.getNetworkClient().getChatRooms(query, offset);
            call.enqueue(chatRoomsCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<PagesResult<ChatRoom>> chatRoomsCallback = new Callback<PagesResult<ChatRoom>>() {
        @Override
        public void onResponse(Call<PagesResult<ChatRoom>> call, Response<PagesResult<ChatRoom>> response) {
            view.stopLoading();
            isLoading = false;
            if (response.isSuccess()) {
                view.onChatRoomsListLoaded(response.body().getResults());
                offset = offset + Constants.PAGE_SIZE;
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<PagesResult<ChatRoom>> call, Throwable t) {
            Log.e(ChatRoomPresenter.class.getName(), t.getMessage());
            isLoading = false;
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface ChatRoomsListView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void clearList();

        void onChatRoomsListLoaded(List<ChatRoom> chatRooms);
    }
}
