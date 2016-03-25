package com.citifleet.view.main.posting;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.citifleet.model.CarOption;
import com.citifleet.model.PostingType;
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
 * Created by vika on 24.03.16.
 */
public class PostingRentSaleDetailPresenter {
    private NetworkManager networkManager;
    private PostingRentSaleDetailView view;

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

    public void createPost(final PostingType postingType, final int make, final int model, final int type, final int color, final int year, final int fuel, final int seats, final double price, final String description, String[] imageUrls) {
        if (networkManager.isConnectedOrConnecting()) {
            view.startLoading();
            getImagesRequestBodies(imageUrls, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    HashMap<String, RequestBody> imagesMap = (HashMap<String, RequestBody>) msg.obj;
                    RequestBody descrBody = RequestBody.create(MediaType.parse("text/plain"), description);
                    Call<Void> call;
                    if (postingType == PostingType.RENT) {
                        call = networkManager.getNetworkClient().postRentCar(imagesMap, make, model, type, color, year, fuel, seats, price, descrBody);
                    } else {
                        call = networkManager.getNetworkClient().postSaleCar(imagesMap, make, model, type, color, year, fuel, seats, price, descrBody);
                    }
                    call.enqueue(createPostCallback);
                }
            });
        } else {
            view.onNetworkError();
        }
    }

    private void getImagesRequestBodies(final String[] imageUrls, final Handler handler) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ScaleImageHelper scaleImageHelper = new ScaleImageHelper();
                final HashMap<String, RequestBody> imagesMap = new HashMap<String, RequestBody>();
                for (int i = 0; i < imageUrls.length; i++) {
                    if (imageUrls[i] != null) {
                        byte[] bytes = scaleImageHelper.getScaledImageBytes(imageUrls[i]);
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
