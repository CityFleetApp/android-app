package com.citifleet.view.main.posting;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.citifleet.model.GeneralGood;
import com.citifleet.model.Photo;
import com.citifleet.network.NetworkErrorUtil;
import com.citifleet.network.NetworkManager;
import com.citifleet.util.ScaleImageHelper;

import java.util.ArrayList;
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
    private int imagesUpdatingCount = 0;
    private boolean isUpdatingPost = false;

    public PostingGeneralGoodsPresenter(NetworkManager networkManager, PostingGeneralGoodsDetailView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void createPost(final GeneralGood generalGood, final boolean isEditMode, List<Integer> photoIdToDelete) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            List<Photo> newPhotos = new ArrayList<Photo>();
            for (Photo photo : generalGood.getPhotos()) {
                if (photo != null && photo.getId() == 0) {
                    newPhotos.add(photo);
                }
            }
            for (Integer integer : photoIdToDelete) {
                Call<Void> call = networkManager.getNetworkClient().deletePhotoFromGoodsPost(integer);
                call.enqueue(editPhotoCallback);
            }
            if (isEditMode && newPhotos.isEmpty()) {
                updatePost(generalGood, null);
            } else {
                getImagesRequestBodies(newPhotos, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (isEditMode) {
                            updatePost(generalGood, (HashMap<String, RequestBody>) msg.obj);
                        } else {
                            createPost(generalGood, (HashMap<String, RequestBody>) msg.obj);
                        }
                    }
                });
            }
        } else {
            view.onNetworkError();
        }
    }

    private void updatePost(GeneralGood generalGood, HashMap<String, RequestBody> imagesMap) {
        RequestBody descrBody = RequestBody.create(MediaType.parse("text/plain"), generalGood.getDescription());
        RequestBody itemBody = RequestBody.create(MediaType.parse("text/plain"), generalGood.getItem());
        Call<Void> call = networkManager.getNetworkClient().editGood(generalGood.getId(), Double.parseDouble(generalGood.getPrice()), generalGood.getConditionId(),
                itemBody, descrBody);
        call.enqueue(createPostCallback);
        isUpdatingPost = true;
        for (RequestBody requestBody : imagesMap.values()) {
            imagesUpdatingCount++;
            Call<Void> callEditPhotos = networkManager.getNetworkClient().editPhotoFromGoodsPost(requestBody, generalGood.getId());
            callEditPhotos.enqueue(editPhotoCallback);
        }

    }

    private void createPost(GeneralGood generalGood, HashMap<String, RequestBody> imagesMap) {
        RequestBody descrBody = RequestBody.create(MediaType.parse("text/plain"), generalGood.getDescription());
        RequestBody itemBody = RequestBody.create(MediaType.parse("text/plain"), generalGood.getItem());
        Call<Void> call = networkManager.getNetworkClient().postGood(imagesMap, Double.parseDouble(generalGood.getPrice()), generalGood.getConditionId(), itemBody, descrBody);
        call.enqueue(createPostCallback);
    }


    private Callback<Void> editPhotoCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                imagesUpdatingCount--;
                if (imagesUpdatingCount == 0) {
                    view.stopLoading();
                    view.onPostCreatesSuccessfully();
                }
            } else {
                view.stopLoading();
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

    private Callback<Void> createPostCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                if (!isUpdatingPost || (isUpdatingPost && imagesUpdatingCount == 0)) {
                    view.onPostCreatesSuccessfully();
                    view.stopLoading();
                }
            } else {
                view.stopLoading();
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

    private void getImagesRequestBodies(final List<Photo> imageUrls, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ScaleImageHelper scaleImageHelper = new ScaleImageHelper();
                final HashMap<String, RequestBody> imagesMap = new HashMap<String, RequestBody>();
                for (int i = 0; i < imageUrls.size(); i++) {
                    if (imageUrls.get(i) != null) {
                        byte[] bytes = scaleImageHelper.getScaledImageBytes(imageUrls.get(i).getUrl());
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
