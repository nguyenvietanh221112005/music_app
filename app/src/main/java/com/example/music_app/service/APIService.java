// APIService.java
package com.example.music_app.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    private static String base_url = "http://172.16.8.98:8080/";

    public static DataService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(DataService.class);
    }
}
