// DataService.java
package com.example.music_app.service;

import com.example.music_app.model.BaiHat;
import com.example.music_app.model.TheLoai;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DataService {

    @GET("api/songs/top")
    Call<ArrayList<BaiHat>> getTopBXH();


    @GET("api/categories")
    Call<ArrayList<TheLoai>> getTheLoai();


}
