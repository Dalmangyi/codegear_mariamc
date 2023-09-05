package com.codegear.mariamc_rfid.cowchronicle.activities.services;

import com.codegear.mariamc_rfid.cowchronicle.activities.models.TestModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CowChronicleService {

    @GET("posts/{UserID}")
    Call<TestModel> test_api_get(@Path("UserID") String userid);
}
