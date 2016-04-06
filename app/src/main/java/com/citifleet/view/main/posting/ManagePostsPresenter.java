package com.citifleet.view.main.posting;

import android.util.Log;

import com.citifleet.model.ManagePostModel;
import com.citifleet.model.Notification;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 01.04.16.
 */
public class ManagePostsPresenter  {
    private ManagePostsView view;
    private NetworkManager networkManager;
    private List<ManagePostModel> posts;

    public ManagePostsPresenter(ManagePostsView view, NetworkManager networkManager) {
        this.view = view;
        this.networkManager = networkManager;
    }


    public void loadPosts() {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            Call<List<ManagePostModel>> call = networkManager.getNetworkClient().getManagePosts();
            call.enqueue(notificationCallback);
        } else {
            view.onNetworkError();
        }
    }

    Callback<List<ManagePostModel>> notificationCallback = new Callback<List<ManagePostModel>>() {
        @Override
        public void onResponse(Call<List<ManagePostModel>> call, Response<List<ManagePostModel>> response) {
            view.stopLoading();
            if (response.isSuccess()) {
                posts = response.body();
                view.onPostsLoaded(posts);
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<ManagePostModel>> call, Throwable t) {
         //   Log.e(ManagePostsPresenter.class.getName(), t.getMessage());
            t.printStackTrace();
            view.stopLoading();
            view.onFailure(t.getMessage());
        }
    };

    public interface ManagePostsView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onPostsLoaded(List<ManagePostModel> posts);
    }
}
