package com.citifleet.network;

import com.citifleet.model.LoginInfo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NetworkService {
    @FormUrlEncoded
    @POST("userslogin/")
    Call<LoginInfo> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("userssignup/")
    Call<LoginInfo> signup(@Field("full_name") String fullName, @Field("username") String username,
                           @Field("phone") String phone, @Field("hack_license") String hackLicense,
                           @Field("email") String email, @Field("password") String password,
                           @Field("password_confirm") String passwordConfirm);

}
