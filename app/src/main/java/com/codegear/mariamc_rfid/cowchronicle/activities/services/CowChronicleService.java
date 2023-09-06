package com.codegear.mariamc_rfid.cowchronicle.activities.services;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.QueryMap;

public interface CowChronicleService {

    //http://marivet.co.kr:5139/v2/ahebf/chalet/login
    @GET("login")
    Call<ResLogin> login(@QueryMap Map<String, String> reqLogin);
}
