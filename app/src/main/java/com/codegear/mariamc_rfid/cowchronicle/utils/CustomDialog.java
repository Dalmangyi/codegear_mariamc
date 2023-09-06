package com.codegear.mariamc_rfid.cowchronicle.utils;

import android.content.Context;
import android.graphics.Color;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codegear.mariamc_rfid.R;

public class CustomDialog {

    public static void showSimple(Context context, String msg){
        new MaterialDialog.Builder(context)
            .content(msg)
            .positiveText("확인")
            .positiveColorRes(R.color.colorPrimary)
            .show();
    }

    public static void showSimpleError(Context context, String msg){
        new MaterialDialog.Builder(context)
            .title("에러")
            .content(msg)
            .negativeText("닫기")
            .negativeColor(Color.RED)
            .show();
    }
}
