package com.codegear.mariamc_rfid.cowchronicle.activities.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://marivet.co.kr/v2/ahebf/chalet/fetch_data.php?var1=002157718232";

    public static CowChronicleService getApiService(){
        return getInstance().create(CowChronicleService.class);
    }

    private static Retrofit getInstance(){
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

}
