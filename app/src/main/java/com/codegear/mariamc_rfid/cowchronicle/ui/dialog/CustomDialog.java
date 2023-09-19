package com.codegear.mariamc_rfid.cowchronicle.ui.dialog;

import android.content.Context;
import android.graphics.Color;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codegear.mariamc_rfid.R;

public class CustomDialog {

    public static void showSimple(Context context, String msg){
        showSimpleCore(context, msg);
    }

    public static void showSimple(Context context, int resStringId){
        String msg = context.getResources().getString(resStringId);
        showSimpleCore(context, msg);
    }

    private static void showSimpleCore(Context context, String msg){
        new MaterialDialog.Builder(context)
                .content(msg)
                .positiveText("확인")
                .positiveColorRes(R.color.colorPrimary)
                .show();
    }

    public static void showSimpleError(Context context, String msg){
        new MaterialDialog.Builder(context)
            .title("통신 에러")
            .content(msg)
            .negativeText("닫기")
            .negativeColor(Color.RED)
            .show();
    }
}
