package com.codegear.mariamc_rfid.cowchronicle.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class AndroidUtil {

    //기기 아이디
    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            deviceId = "null";
        }

        return deviceId;
    }

    //기기 모델명
    public static String getDeviceModel(){
        return Build.MODEL;
    }

    //Android OS 버전 가져오기
    public static String getDeviceOs() {
        return Build.VERSION.RELEASE;
    }

    //SDK 버전 가져오기
    public static int getDeviceSdk() {
        return Build.VERSION.SDK_INT;
    }

}
