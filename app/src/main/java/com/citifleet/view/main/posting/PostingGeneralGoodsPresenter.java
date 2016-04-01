package com.citifleet.view.main.posting;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.citifleet.model.GeneralGood;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.util.ScaleImageHelper;

import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 28.03.16.
 */
public class PostingGeneralGoodsPresenter {
    private NetworkManager networkManager;
    private PostingGeneralGoodsDetailView view;

    public PostingGeneralGoodsPresenter(NetworkManager networkManager, PostingGeneralGoodsDetailView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void createPost(final GeneralGood generalGood, boolean isEditMode) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            if (isEditMode) {
                RequestBody descrBody = RequestBody.create(MediaType.parse("text/plain"), generalGood.getDescription());
                RequestBody itemBody = RequestBody.create(MediaType.parse("text/plain"), generalGood.getItem());
                Call<Void> call = networkManager.getNetworkClient().editGood(generalGood.getId(), Double.parseDouble(generalGood.getPrice()), generalGood.getConditionId(), itemBody, descrBody);
                call.enqueue(createPostCallback);
            } else {
                getImagesRequestBodies(generalGood.getPhotos(), new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        HashMap<String, RequestBody> imagesMap = (HashMap<String, RequestBody>) msg.obj;
                        RequestBody descrBody = RequestBody.create(MediaType.parse("text/plain"), generalGood.getDescription());
                        RequestBody itemBody = RequestBody.create(MediaType.parse("text/plain"), generalGood.getItem());
                        Call<Void> call = networkManager.getNetworkClient().postGood(imagesMap, Double.parseDouble(generalGood.getPrice()), generalGood.getConditionId(), itemBody, descrBody);
                        call.enqueue(createPostCallback);
                    }
                });
            }
        } else {
            view.onNetworkError();
        }
    }

    private Callback<Void> createPostCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            view.stopLoading();
            if (response.isSuccessful()) {
                view.onPostCreatesSuccessfully();
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            view.stopLoading();
            Log.e(PostingGeneralGoodsPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };

    private void getImagesRequestBodies(final List<String> imageUrls, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ScaleImageHelper scaleImageHelper = new ScaleImageHelper();
                final HashMap<String, RequestBody> imagesMap = new HashMap<String, RequestBody>();
                for (int i = 0; i < imageUrls.size(); i++) {
                    if (imageUrls.get(i) != null) {
                        byte[] bytes = scaleImageHelper.getScaledImageBytes(imageUrls.get(i));
                        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
                        imagesMap.put("photos[" + i + "]\"; filename=\"image.png\" ", fileRequestBody);
                    }
                }
                Message message = new Message();
                message.obj = imagesMap;
                handler.sendMessage(message);
            }
        };
        new Thread(runnable).start();
    }

    public interface PostingGeneralGoodsDetailView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onPostCreatesSuccessfully();
    }

}
