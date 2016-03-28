package com.citifleet.network;

import com.citifleet.model.AddContactsBody;
import com.citifleet.model.Benefit;
import com.citifleet.model.Car;
import com.citifleet.model.CarOption;
import com.citifleet.model.Document;
import com.citifleet.model.LegalAidLocation;
import com.citifleet.model.LegalAidPerson;
import com.citifleet.model.LoginInfo;
import com.citifleet.model.Notification;
import com.citifleet.model.ProfileImage;
import com.citifleet.model.Settings;
import com.citifleet.model.UserImages;
import com.citifleet.model.UserInfo;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
    @POST("reports/")
    Call<Object> report(@Field("report_type") int reportType, @Field("lat") double lat, @Field("lng") double lng);

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

    @Multipart
    @POST("marketplace/cars/posting/rent/")
    Call<Void> postRentCar(@PartMap() Map<String, RequestBody> files, @Part("make") int make, @Part("model") int model, @Part("type") int type, @Part("color") int color,
                           @Part("year") int year, @Part("fuel") int fuel, @Part("seats") int seats, @Part("price") double price, @Part("description") RequestBody description);

    @Multipart
    @POST("marketplace/cars/posting/sale/")
    Call<Void> postSaleCar(@PartMap() Map<String, RequestBody> files, @Part("make") int make, @Part("model") int model, @Part("type") int type, @Part("color") int color,
                           @Part("year") int year, @Part("fuel") int fuel, @Part("seats") int seats, @Part("price") double price, @Part("description") RequestBody description);

    @Multipart
    @POST("marketplace/goods/posting/")
    Call<Void> postGood(@PartMap() Map<String, RequestBody> files, @Part("price") double price, @Part("condition") int condition,
                        @Part("item") RequestBody item, @Part("description") RequestBody description);
}
