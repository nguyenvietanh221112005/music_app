// DataService.java
package com.example.music_app.service;

import com.example.music_app.model.ApiResponse;
import com.example.music_app.model.BaiHat;
import com.example.music_app.model.TheLoai;
import com.example.music_app.model.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DataService {

    @GET("api/songs/top")
    Call<ArrayList<BaiHat>> getTopBXH();



    @GET("api/categories")
    Call<ArrayList<TheLoai>> getTheLoai();

    @GET("api/songs/category/{id}")
    Call<ArrayList<BaiHat>> getBaiHatByTheLoai(@Path("id") int idTheLoai);
    @POST("api/favorites/add/{userId}/{songId}")
    Call<Void> addFavorite(@Path("userId") int userId, @Path("songId") int songId);

    @DELETE("api/favorites/delete/{userId}/{songId}")
    Call<Void> deleteFavorite(@Path("userId") int userId, @Path("songId") int songId);

    @GET("api/favorites/{userId}")
    Call<ArrayList<BaiHat>> getFavorites(@Path("userId") int userId);

    @POST("api/users/register")
    Call<ApiResponse> register(@Body Users user);

    @POST("api/users/login")
    Call<Users> login(@Body Users user);

    @GET("api/songs/search")
    Call<List<BaiHat>> searchSongs(@Query("keyword") String keyword);

    @GET("api/users/{id}")
    Call<Users> getUserById(@Path("id") int id);
}
