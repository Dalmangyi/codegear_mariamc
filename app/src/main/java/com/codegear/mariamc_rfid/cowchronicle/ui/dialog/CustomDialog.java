package com.codegear.mariamc_rfid.cowchronicle.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
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
            .title("에러")
            .content(msg)
            .negativeText("닫기")
            .negativeColor(Color.RED)
            .show();
    }

    public static void showSelectDialog(Context context, String title, String msg,
                                        String strBtn1, MaterialDialog.SingleButtonCallback sbc1,
                                        String strBtn2, MaterialDialog.SingleButtonCallback sbc2
                                        ){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(msg)
                .cancelable(true);

        if(strBtn1 != null && sbc1 != null){
            builder.positiveText(strBtn1)
                .onPositive(sbc1)
                .positiveColorRes(R.color.colorPrimary);
        }

        if(strBtn2 != null && sbc2 != null){
            builder.negativeText(strBtn2)
                .onNegative(sbc2)
                .negativeColorRes(R.color.colorPrimary);
        }

        if(strBtn1 != null || strBtn2 != null){
            builder.neutralText("닫기")
                .neutralColorRes(R.color.deep_dark_grey)
                .onNeutral((dialog, which) -> dialog.dismiss());
        }

        builder.show();
    }
}
