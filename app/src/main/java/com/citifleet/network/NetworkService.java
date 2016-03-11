package com.citifleet.network;

import com.citifleet.model.AddContactsBody;
import com.citifleet.model.InstagramLoginResponse;
import com.citifleet.model.LoginInfo;
import com.citifleet.model.ProfileImage;
import com.citifleet.model.UserInfo;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;

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
}
