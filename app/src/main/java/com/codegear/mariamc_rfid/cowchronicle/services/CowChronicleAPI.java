package com.codegear.mariamc_rfid.cowchronicle.services;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface CowChronicleAPI {

    /*
    로그인
    url : http://marivet.co.kr:5139/v2/ahebf/chalet/login
    자신의 정보 및 목장 리스트를 가져옴
     */
    @GET("login")
    Call<ResLogin> login(@QueryMap Map<String, String> reqLogin);


    /*
    특정 목장의 소 리스트 가져오기
    url : http://marivet.co.kr:5139/v2/ahebf/chalet/cowlist?farmcd=219
    소의 상세 정보도 같이 가져옴.
     */
    @GET("cowlist")
    Call<ResCowList> getCowList(@Query("farmcd") String farmCode);

    /*
    태그 데이터 추가하기
    url : http://marivet.co.kr:5139/v2/ahebf/chalet/insert_data
    RFID리더기(장치)로 읽은 태그 데이터를 소 정보와 함께 서버에 저장
     */
    @POST("insert_data")
    Call<ResInsertTagData> insertTagData(@Body List<ReqInsertTagData> reqInsertTagList);
}
