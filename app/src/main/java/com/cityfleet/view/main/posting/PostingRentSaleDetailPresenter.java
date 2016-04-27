package com.cityfleet.view.main.posting;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cityfleet.model.Car;
import com.cityfleet.model.CarOption;
import com.cityfleet.model.CarPostingType;
import com.cityfleet.model.Photo;
import com.cityfleet.network.NetworkErrorUtil;
import com.cityfleet.network.NetworkManager;
import com.cityfleet.util.Constants;
import com.cityfleet.util.ScaleImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vika on 24.03.16.
 */
public class PostingRentSaleDetailPresenter {
    private NetworkManager networkManager;
    private PostingRentSaleDetailView view;
    private int imagesUpdatingCount = 0;
    private boolean isUpdatingPost = false;

    public PostingRentSaleDetailPresenter(NetworkManager networkManager, PostingRentSaleDetailView view) {
        this.networkManager = networkManager;
        this.view = view;
    }

    public void init() {
        if (networkManager.isConnectedOrConnecting()) {
            Call<List<CarOption>> makesCall = networkManager.getNetworkClient().getCarMakes();
            makesCall.enqueue(makeCallback);
            Call<List<CarOption>> seatsCall = networkManager.getNetworkClient().getCarSeats();
            seatsCall.enqueue(seatsCallback);
            Call<List<CarOption>> fuelCall = networkManager.getNetworkClient().getCarFuels();
            fuelCall.enqueue(fuelCallback);
            Call<List<CarOption>> colorCall = networkManager.getNetworkClient().getCarColors();
            colorCall.enqueue(colorCallback);
            Call<List<CarOption>> typesCall = networkManager.getNetworkClient().getCarTypes();
            typesCall.enqueue(typesCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void getCarModelsByMakeId(int makeId) {
        if (networkManager.isConnectedOrConnecting()) {
            Call<List<CarOption>> modelCall = networkManager.getNetworkClient().getCardModels(makeId);
            modelCall.enqueue(modelCallback);
        } else {
            view.onNetworkError();
        }
    }

    public void createPost(final CarPostingType postingType, final Car car, final boolean isEditMode, List<Integer> photoIdToDelete) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            List<Photo> newPhotos = new ArrayList<Photo>();
            for (Photo photo : car.getPhotos()) {
                if (photo != null && photo.getId() == 0) {
                    newPhotos.add(photo);
                }
            }
            for (Integer integer : photoIdToDelete) {
                Call<Void> call = networkManager.getNetworkClient().deletePhotoFromCarPost(integer);
                call.enqueue(deletePhotoCallback);
            }
            if (isEditMode && newPhotos.isEmpty()) {
                updatePost(postingType, car, null);
            } else {
                getImagesRequestBodies(newPhotos, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (isEditMode) {
                            updatePost(postingType, car, (HashMap<String, RequestBody>) msg.obj);
                        } else {
                            createNewPost(postingType, car, (HashMap<String, RequestBody>) msg.obj);
                        }
                    }
                });
            }
        } else {
            view.onNetworkError();
        }
    }

    private void updatePost(CarPostingType postingType, Car car, HashMap<String, RequestBody> imagesMap) {
        isUpdatingPost = true;
        imagesUpdatingCount = 0;
        RequestBody descrBody = RequestBody.create(MediaType.parse("text/plain"), car.getDescription());
        Call<Void> call;
        if (postingType == CarPostingType.RENT) {
            call = networkManager.getNetworkClient().editRentCar(car.getId(), car.getMakeId(), car.getModelId(), car.getTypeId(), car.getColorId(),
                    car.getYear(), car.getFuelId(), car.getSeatsId(), Double.parseDouble(car.getPrice()), descrBody);
        } else {
            call = networkManager.getNetworkClient().editSaleCar(car.getId(), car.getMakeId(), car.getModelId(), car.getTypeId(), car.getColorId(),
                    car.getYear(), car.getFuelId(), car.getSeatsId(), Double.parseDouble(car.getPrice()), descrBody);
        }
        call.enqueue(createPostCallback);
        if (imagesMap != null) {
            for (RequestBody requestBody : imagesMap.values()) {
                imagesUpdatingCount++;
                Call<Void> callEditPhotos = networkManager.getNetworkClient().editPhotoFromCarPost(requestBody, car.getId());
                callEditPhotos.enqueue(editPhotoCallback);
            }
        }
    }

    private void createNewPost(CarPostingType postingType, Car car, HashMap<String, RequestBody> imagesMap) {
        isUpdatingPost = false;
        RequestBody descrBody = RequestBody.create(MediaType.parse("text/plain"), car.getDescription());
        Call<Void> call;
        if (postingType == CarPostingType.RENT) {
            call = networkManager.getNetworkClient().postRentCar(imagesMap, car.getMakeId(), car.getModelId(), car.getTypeId(), car.getColorId(),
                    car.getYear(), car.getFuelId(), car.getSeatsId(), Double.parseDouble(car.getPrice()), descrBody);
        } else {
            call = networkManager.getNetworkClient().postSaleCar(imagesMap, car.getMakeId(), car.getModelId(), car.getTypeId(), car.getColorId(),
                    car.getYear(), car.getFuelId(), car.getSeatsId(), Double.parseDouble(car.getPrice()), descrBody);
        }
        call.enqueue(createPostCallback);
    }

    private void getImagesRequestBodies(final List<Photo> imageUrls, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ScaleImageHelper scaleImageHelper = new ScaleImageHelper();
                final HashMap<String, RequestBody> imagesMap = new HashMap<String, RequestBody>();
                for (int i = 0; i < imageUrls.size(); i++) {
                    if (imageUrls.get(i) != null) {
                        byte[] bytes = scaleImageHelper.getScaledImageBytes(imageUrls.get(i).getUrl(), Constants.IMAGE_WIDTH);
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

    private Callback<Void> deletePhotoCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (!response.isSuccessful()) {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.e(PostingRentSaleDetailFragment.class.getName(), t.getMessage());
        }
    };
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
            Log.e(PostingRentSaleDetailFragment.class.getName(), t.getMessage());
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
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> makeCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onMakesLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> modelCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onModelLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> seatsCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onSeatsLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> fuelCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onFuelLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> colorCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onColorLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };
    private Callback<List<CarOption>> typesCallback = new Callback<List<CarOption>>() {
        @Override
        public void onResponse(Call<List<CarOption>> call, Response<List<CarOption>> response) {
            if (response.isSuccessful()) {
                view.onTypeLoaded(response.body());
            } else {
                view.onFailure(NetworkErrorUtil.gerErrorMessage(response));
            }
        }

        @Override
        public void onFailure(Call<List<CarOption>> call, Throwable t) {
            Log.e(PostingRentSaleDetailPresenter.class.getName(), t.getMessage());
            view.onFailure(t.getMessage());
        }
    };

    public interface PostingRentSaleDetailView {
        void startLoading();

        void stopLoading();

        void onFailure(String error);

        void onNetworkError();

        void onMakesLoaded(List<CarOption> makeList);

        void onModelLoaded(List<CarOption> modelList);

        void onTypeLoaded(List<CarOption> typeList);

        void onFuelLoaded(List<CarOption> fuelList);

        void onColorLoaded(List<CarOption> colorList);

        void onSeatsLoaded(List<CarOption> seatsList);

        void onPostCreatesSuccessfully();
    }
}
