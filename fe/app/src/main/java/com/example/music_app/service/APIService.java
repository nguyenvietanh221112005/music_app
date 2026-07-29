// APIService.java
package com.example.music_app.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    private static String base_url = "http://192.168.1.3:8080/";

    public static DataService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(DataService.class);
    }
}
