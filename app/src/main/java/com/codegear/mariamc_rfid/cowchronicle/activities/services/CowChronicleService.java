package com.codegear.mariamc_rfid.cowchronicle.activities.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;

public interface CowChronicleService {

    //http://marivet.co.kr:5139/v2/ahebf/chalet/login
    @HTTP(method = "get", path="login", hasBody = true)
    Call<ResLogin> login(@Body ReqLogin reqLogin);
}
