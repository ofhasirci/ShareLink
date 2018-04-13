package com.example.veled.sharelink;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by veled on 8.02.2018.
 */

public interface ApiInterface {
    @GET("link/getlinks")
    Call<GroupObjectModel> getLinks(@Header("x-access-token") String token,
                                         @Query("limit") String limit,
                                         @Query("skip") String skip,
                                         @Query("groupName") String groupName);

    @POST("auth/signin")
    @FormUrlEncoded
    Call<LinkResponseModel> signin(@Field("email") String email,
                                   @Field("password") String password);

    @GET("auth/profile")
    Call<LinkResponseModel> getProfile(@Header("x-access-token") String token);

    @POST("auth/login")
    @FormUrlEncoded
    Call<LinkResponseModel> login(@Field("email") String email,
                                  @Field("password") String password);

    @POST("auth/resetpass")
    @FormUrlEncoded
    Call<LinkResponseModel> resetpass(@Field("email") String email);

    @POST("auth/changepass")
    @FormUrlEncoded
    Call<LinkResponseModel> changepass(@Field("email") String email,
                                       @Field("oldPassword") String oldPassword,
                                       @Field("newPassword") String newPassword);

    @POST("link/creategroup")
    @FormUrlEncoded
    Call<LinkResponseModel> createGroup(@Field("groupName") String groupName,
                                        @Field("starter") String starter,
                                        @Field("dateOfStart") String date);

    @POST("link/addcontact")
    @FormUrlEncoded
    Call<LinkResponseModel> addContact(@Field("email") String email,
                                       @Field("groupName") String groupName);

}
