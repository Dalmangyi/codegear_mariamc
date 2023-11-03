package com.codegear.mariamc_rfid.cowchronicle.services;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import android.app.AlertDialog;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.UnknownHostException;

import dmax.dialog.SpotsDialog;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

        //Client
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        //Gson
        Gson gson = new GsonBuilder().setLenient().create();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    public static <E> void commonCall(Class<E>clazz, Activity mActivity, Call<E> call, AlertDialog alertDialog, OnStateListener<E> onStateListener){

        AlertDialog dialogLoading;
        if (alertDialog != null){
            dialogLoading = alertDialog;
        }
        else{
            dialogLoading = new SpotsDialog.Builder().setContext(mActivity).setTheme(R.style.CustomAlertDialog).build();
            mActivity.runOnUiThread(()->{
                dialogLoading.show();
            });
        }


        AlertDialog finalDialogLoading = dialogLoading;
        call.enqueue(new Callback<E>() {
            @Override
            public void onResponse(@NonNull Call<E> call, @NonNull Response<E> response) {
                Log.e(TAG,"API success. code:"+response.code()+",res:"+response.body());
                try{
                    mActivity.runOnUiThread(()-> {
                        finalDialogLoading.dismiss();
                    });

                    if (!response.isSuccessful()) {
                        String errMessage = "";
                        try {
                            errMessage = response.errorBody().string();
                        } catch (IOException e) {
//                        throw new RuntimeException(e);
                        }
                        String finalErrMessage = errMessage;
                        mActivity.runOnUiThread(()-> {
                            CustomDialog.showSimpleError(mActivity, "서버 담당자에게 문의해 주세요. (" + response.code() + ")\n" + response.message() + "\n" + finalErrMessage);
                        });
                        return;
                    }

                    //성공
                    if(onStateListener != null){
                        ((ResCommon)response.body()).convertData();
                        onStateListener.OnSuccess(response.body());
                    }
                }catch (Exception e){
                    Log.e(TAG, "RetrofitClient Exception onResponse:"+e.toString());
                }

            }

            @Override
            public void onFailure(@NonNull Call<E> call, @NonNull Throwable t) {
                Log.e(TAG,"API onFailure. "+t.getMessage());
                mActivity.runOnUiThread(()-> {
                    try {
                        finalDialogLoading.dismiss();

                        if (t instanceof UnknownHostException) {
                            CustomDialog.showSimpleError(mActivity, "네트워크 연결을 확인해 주세요.");
                        } else {
                            CustomDialog.showSimpleError(mActivity, t.getMessage());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "RetrofitClient Exception onFailure:" + e.toString());
                    }
                });
            }
        });
    }

    public interface OnStateListener<E> {
        void OnSuccess(E res);
    }

}
