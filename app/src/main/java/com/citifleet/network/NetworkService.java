package com.citifleet.network;

import com.citifleet.model.AddContactsBody;
import com.citifleet.model.Benefit;
import com.citifleet.model.Car;
import com.citifleet.model.CarOption;
import com.citifleet.model.ChatFriend;
import com.citifleet.model.ChatMessage;
import com.citifleet.model.ChatRoom;
import com.citifleet.model.Document;
import com.citifleet.model.FriendNearby;
import com.citifleet.model.GeneralGood;
import com.citifleet.model.JobOffer;
import com.citifleet.model.LegalAidLocation;
import com.citifleet.model.LegalAidPerson;
import com.citifleet.model.LoginInfo;
import com.citifleet.model.ManagePostModel;
import com.citifleet.model.Notification;
import com.citifleet.model.PagesResult;
import com.citifleet.model.ProfileImage;
import com.citifleet.model.Report;
import com.citifleet.model.Settings;
import com.citifleet.model.UserEditInfo;
import com.citifleet.model.UserImages;
import com.citifleet.model.UserInfo;
import com.citifleet.util.Constants;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NetworkService {
    @FormUrlEncoded
    @POST("users/login/")
    Call<LoginInfo> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("users/signup/")
    Call<LoginInfo> signup(@Field("full_name") String fullName, @Field("username") String username,
                           @Field("phone") String phone, @Field("hack_license") String hackLicense,
                           @Field("email") String email, @Field("password") String password,
                           @Field("password_confirm") String passwordConfirm);

    @FormUrlEncoded
    @POST("reports/nearby/")
    Call<Object> report(@Field("report_type") int reportType, @Field("lat") double lat, @Field("lng") double lng);

    @GET("reports/nearby/")
    Call<List<Report>> getReportsNearby(@Query("lat") double lat, @Query("lng") double lng);

    @GET("reports/friends/")
    Call<List<FriendNearby>> getFriendsNearby(@Query("lat") double lat, @Query("lng") double lng);

    @FormUrlEncoded
    @POST("users/reset-password/")
    Call<Void> resetPassword(@Field("email") String email);

    @Multipart
    @PUT("users/upload-avatar/")
    Call<ProfileImage> uploadAvatar(@Part("avatar\"; filename=\"image.png\" ") RequestBody file, @Part("description") RequestBody description);

    @GET("users/info/")
    Call<UserInfo> getUserInfo();

    @POST("users/add-contacts-friends/")
    Call<List<UserInfo>> addFriendsFromContacts(@Body AddContactsBody body);

    @FormUrlEncoded
    @POST("users/add-facebook-friends/")
    Call<List<UserInfo>> addFacebookFriends(@Field("token") String token);

    @FormUrlEncoded
    @POST("users/add-twitter-friends/")
    Call<List<UserInfo>> addTwitterFriends(@Field("token") String token, @Field("token_secret") String tokenSecret);

    @FormUrlEncoded
    @POST("users/add-instagram-friends/")
    Call<List<UserInfo>> addInstagramFriends(@Field("token") String token);

    @GET("benefits/")
    Call<List<Benefit>> getBenefits();

    @FormUrlEncoded
    @POST("users/change-password/")
    Call<Void> changePassword(@Field("old_password") String oldPassword, @Field("password") String password, @Field("password_confirm") String passwordConfirm);

    @Multipart
    @POST("users/photos/")
    Call<UserImages> uploadPhoto(@Part("file\"; filename=\"image.png\" ") RequestBody file, @Part("description") RequestBody description);

    @GET("users/photos/")
    Call<List<UserImages>> getPhotos();

    @DELETE("users/photos/{id}/")
    Call<Void> deletePhoto(@Path("id") int id);

    @GET("legalaid/locations/")
    Call<List<LegalAidLocation>> getLegalAidLocations();

    @GET("legalaid/insurance/")
    Call<List<LegalAidPerson>> getLegalAidInsurance(@Query("location") int locationId);

    @GET("legalaid/accouting/")
    Call<List<LegalAidPerson>> getLegalAidAccounting(@Query("location") int locationId);

    @GET("legalaid/dmv-lawyers/")
    Call<List<LegalAidPerson>> getLegalAidDmvLawyers(@Query("location") int locationId);

    @GET("legalaid/tlc-lawyers/")
    Call<List<LegalAidPerson>> getLegalAidTlcLawyers(@Query("location") int locationId);

    @GET("notifications/")
    Call<List<Notification>> getNotifications();

    @POST("notifications/{id}/mark-seen/")
    Call<Void> getNotificationById(@Path("id") int id);

    @GET("documents/")
    Call<List<Document>> getDocuments();

    @Multipart
    @POST("documents/")
    Call<Document> createDocument(@Part("file\"; filename=\"image.png\" ") RequestBody file, @Part("expiry_date") RequestBody expiryDate,
                                  @Part("plate_number") RequestBody plateNumber, @Part("document_type") RequestBody documentType);

    @Multipart
    @PATCH("documents/{id}/")
    Call<Document> updateDocument(@Path("id") int id, @Part("file\"; filename=\"image.png\" ") RequestBody file, @Part("expiry_date") RequestBody expiryDate,
                                  @Part("plate_number") RequestBody plateNumber, @Part("document_type") RequestBody documentType);

    @GET("users/settings/")
    Call<Settings> getSettings();

    @FormUrlEncoded
    @PATCH("users/settings/")
    Call<Settings> changeSettings(@Field("notifications_enabled") boolean notificationsEnabled, @Field("chat_privacy") boolean chatPrivacy, @Field("visible") boolean visible);

    @GET("marketplace/cars/sale/")
    Call<List<Car>> getCarsForSale();

    @GET("marketplace/cars/rent/")
    Call<List<Car>> getCarsForRent();

    @GET("marketplace/cars/make/")
    Call<List<CarOption>> getCarMakes();

    @GET("marketplace/cars/model/")
    Call<List<CarOption>> getCardModels(@Query("id") int id);

    @GET("marketplace/seats/")
    Call<List<CarOption>> getCarSeats();

    @GET("marketplace/colors/")
    Call<List<CarOption>> getCarColors();

    @GET("marketplace/types/")
    Call<List<CarOption>> getCarTypes();

    @GET("marketplace/fuel/")
    Call<List<CarOption>> getCarFuels();

    @GET("marketplace/goods/")
    Call<List<GeneralGood>> getGoods();

    @GET("marketplace/offers/")
    Call<List<JobOffer>> getOffers();

    @Multipart
    @POST("marketplace/cars/posting/rent/")
    Call<Void> postRentCar(@PartMap() Map<String, RequestBody> files, @Part("make") int make, @Part("model") int model, @Part("type") int type, @Part("color") int color,
                           @Part("year") int year, @Part("fuel") int fuel, @Part("seats") int seats, @Part("price") double price, @Part("description") RequestBody description);

    @Multipart
    @POST("marketplace/cars/posting/sale/")
    Call<Void> postSaleCar(@PartMap() Map<String, RequestBody> files, @Part("make") int make, @Part("model") int model, @Part("type") int type, @Part("color") int color,
                           @Part("year") int year, @Part("fuel") int fuel, @Part("seats") int seats, @Part("price") double price, @Part("description") RequestBody description);

    @Multipart
    @PATCH("marketplace/cars/posting/sale/{id}/")
    Call<Void> editSaleCar(@Path("id") int id, @Part("make") int make, @Part("model") int model, @Part("type") int type, @Part("color") int color,
                           @Part("year") int year, @Part("fuel") int fuel, @Part("seats") int seats, @Part("price") double price, @Part("description") RequestBody description);

    @Multipart
    @PATCH("marketplace/cars/posting/rent/{id}/")
    Call<Void> editRentCar(@Path("id") int id, @Part("make") int make, @Part("model") int model, @Part("type") int type, @Part("color") int color,
                           @Part("year") int year, @Part("fuel") int fuel, @Part("seats") int seats, @Part("price") double price, @Part("description") RequestBody description);

    @Multipart
    @POST("marketplace/goods/posting/")
    Call<Void> postGood(@PartMap() Map<String, RequestBody> files, @Part("price") double price, @Part("condition") int condition,
                        @Part("item") RequestBody item, @Part("description") RequestBody description);

    @Multipart
    @PATCH("marketplace/goods/posting/{id}/")
    Call<Void> editGood(@Path("id") int id, @Part("price") double price, @Part("condition") int condition,
                        @Part("item") RequestBody item, @Part("description") RequestBody description);

    @POST("marketplace/offers/{id}/request_job/")
    Call<Void> requestJob(@Path("id") int id);

    @GET("marketplace/job_types/")
    Call<List<CarOption>> getJobTypes();

    @GET("marketplace/vehicles/")
    Call<List<CarOption>> getVehicleTypes();

    @FormUrlEncoded
    @POST("marketplace/offers/posting/")
    Call<Void> postJobOffer(@Field("pickup_datetime") String datetime, @Field("pickup_address") String pickupAddress, @Field("destination") String destination, @Field("fare") double fare,
                            @Field("gratuity") double gratuity, @Field("vehicle_type") int vehicleType, @Field("suite") boolean suite, @Field("job_type") int jobType,
                            @Field("instructions") String instructions);

    @FormUrlEncoded
    @PATCH("marketplace/offers/posting/{id}/")
    Call<Void> editJobOffer(@Path("id") String id, @Field("pickup_datetime") String datetime, @Field("pickup_address") String pickupAddress, @Field("destination") String destination, @Field("fare") double fare,
                            @Field("gratuity") double gratuity, @Field("vehicle_type") int vehicleType, @Field("suite") boolean suite, @Field("job_type") int jobType,
                            @Field("instructions") String instructions);

    @GET("users/profile/")
    Call<UserEditInfo> getUserProfileInfo();

    @FormUrlEncoded
    @PATCH("users/profile/")
    Call<Void> updateUserProfileInfo(@Field("bio") String bio, @Field("username") String username, @Field("phone") String phone,
                                     @Field("car_make") String carMake, @Field("car_model") String carModel, @Field("car_type") String carType, @Field("car_color") String carColor,
                                     @Field("car_year") String carYear);

    @GET("marketplace/manage-posts/")
    Call<List<ManagePostModel>> getManagePosts();

    @DELETE("marketplace/goodsphotos/{id}/")
    Call<Void> deletePhotoFromGoodsPost(@Path("id") int id);

    @Multipart
    @POST("marketplace/goodsphotos/")
    Call<Void> editPhotoFromGoodsPost(@Part("file\"; filename=\"image.png\" ") RequestBody file, @Part("goods") int goodsId);

    @DELETE("marketplace/carphotos/{id}/")
    Call<Void> deletePhotoFromCarPost(@Path("id") int id);

    @Multipart
    @POST("marketplace/carphotos/")
    Call<Void> editPhotoFromCarPost(@Part("file\"; filename=\"image.png\" ") RequestBody file, @Part("car") int carsId);


    @FormUrlEncoded
    @POST("users/devicesdevice/gcm/")
    Call<Void> registerForGcm(@Field("registration_id") String registrationId, @Field("device_id") String deviceId, @Field("active") boolean active);

    @DELETE("users/devicesdevice/gcm/{registration_id}/")
    Call<Void> unregisterFromGcm(@Header("Authorization") String token, @Path("registration_id") String registrationId);

    @FormUrlEncoded
    @PATCH("users/devicesdevice/gcm/{registration_id}/")
    Call<Void> updateToken(@Field("registration_id") String registrationId);


    @POST("reports/map/{id}/confirm_report/")
    Call<Void> confirmReport(@Path("id") int id);


    @POST("reports/map/{id}/deny_report/")
    Call<Void> denyReport(@Path("id") int id);

    @GET("chat/friends/")
    Call<List<ChatFriend>> getChatFriends();

    @GET("chat/rooms/?limit=" + 10)
    Call<PagesResult<ChatRoom>> getChatRooms(@Query("search") String search, @Query("offset") int offset);

    @FormUrlEncoded
    @POST("chat/rooms/")
    Call<ChatRoom> createChatRoom(@Field("name") String name, @Field("participants") int[] participantsIds);

    @GET("chat/rooms/{id}/messages/?limit="+Constants.PAGE_SIZE)
    Call<PagesResult<ChatMessage>> getChatRoomHistory(@Path("id") int roomId, @Query("offset") int offset);
}
