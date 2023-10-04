package com.codegear.mariamc_rfid.cowchronicle.ui.dialog;

import android.app.Activity;
import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codegear.mariamc_rfid.DeviceDiscoverActivity;

public class DialogUtil {

    public static void guideDeviceConnect(Activity activity, boolean destinationScreenIsCowChronicleActivity) {

        CustomDialog.showSelectDialog(activity,
                "실행 불가", "장치 연결이 끊겨있습니다.\n장치 연결 후 다시 시도해 주세요.",
                "장치 연결하기", (MaterialDialog.SingleButtonCallback) (dialog, which) -> {
                    Intent intent = new Intent(activity, DeviceDiscoverActivity.class);
                    intent.putExtra(DeviceDiscoverActivity.ENABLE_AUTO_CONNECT_DEVICE, true); //자동연결 켜기
                    intent.putExtra(DeviceDiscoverActivity.DESTINATION_SCREEN_IS_COWCHRONICLE, destinationScreenIsCowChronicleActivity); //연결후 카우크로니클로 가게 하기
                    activity.startActivity(intent);
                },
                null, null
        );
    }
}
