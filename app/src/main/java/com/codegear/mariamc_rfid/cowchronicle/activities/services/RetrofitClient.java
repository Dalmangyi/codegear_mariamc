package com.codegear.mariamc_rfid.cowchronicle.activities.services;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import android.app.AlertDialog;

import com.codegear.mariamc_rfid.cowchronicle.utils.CustomDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.UnknownHostException;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static String TAG = "RetrofitClient";
    private static final String BASE_URL = "http://marivet.co.kr:5139/v2/ahebf/chalet/";

    public static CowChronicleAPI getApiService(){
        return getInstance().create(CowChronicleAPI.class);
    }

    private static Retrofit getInstance(){
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static <E> void commonCall(Class<E>clazz, Activity mContext, Call<E> call, AlertDialog alertDialog, OnStateListener<E> onStateListener){

        AlertDialog dialogLoading;
        if (alertDialog != null){
            dialogLoading = alertDialog;
        }
        else{
            dialogLoading = new SpotsDialog.Builder().setContext(mContext).build();
            dialogLoading.show();
        }

        boolean debug = true;
        if (debug){
            String tempJson;
            {
                tempJson = "{ \"rowcnt \": 115,  \"success\": 1, \"data\":[{\"COW_ID_NUM\":\"002173840724\",\"TAGNO\":\"541100000000465\",\"KIND\":\"한우\",\"SEX\":\"거세\",\"SNM\":\"4072\",\"BIRTHDT\":\"2022-05-04\",\"MONTHS\":\"16\",\"PRTY\":\" \",\"PRN_STTS\":\" \",\"BREDKND\":\"비육우\",\"REGNO\":\" \"}]}";
            }
            Gson gson = new Gson();
            E e = (E)gson.fromJson(tempJson, clazz);

            dialogLoading.dismiss();
            if(onStateListener != null){
                onStateListener.OnSuccess(e);
            }

            return;
        }



        AlertDialog finalDialogLoading = dialogLoading;
        call.enqueue(new Callback<E>() {
            @Override
            public void onResponse(@NonNull Call<E> call, @NonNull Response<E> response) {
                Log.e(TAG,"API success. code:"+response.code()+",res:"+response.body());
                finalDialogLoading.dismiss();

                if (!response.isSuccessful()) {
                    CustomDialog.showSimpleError(mContext, "서버 담당자에게 문의해주세요. ("+response.code()+")\n"+response.message());
                    return;
                }

                //성공
                if(onStateListener != null){
                    ((ResCommon)response.body()).convertData();
                    onStateListener.OnSuccess(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<E> call, @NonNull Throwable t) {
                Log.e(TAG,"API onFailure. "+t.getMessage());
                finalDialogLoading.dismiss();

                if (t instanceof UnknownHostException){
                    CustomDialog.showSimpleError(mContext, "네트워크 연결을 확인해주세요.");
                }
                else{
                    CustomDialog.showSimpleError(mContext, t.getMessage());
                }

            }
        });
    }

    public interface OnStateListener<E> {
        void OnSuccess(E res);
    }

}
