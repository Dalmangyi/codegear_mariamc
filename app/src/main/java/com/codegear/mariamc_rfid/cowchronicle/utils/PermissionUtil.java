package com.codegear.mariamc_rfid.cowchronicle.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.permissionx.guolindev.PermissionMediator;
import com.permissionx.guolindev.PermissionX;

public class PermissionUtil {

    public static void reqPermissions(FragmentActivity fragmentActivity, OnRequestCallback onRequestCallback, String ... permissions) {
        PermissionMediator permissionMediator = PermissionX.init(fragmentActivity);
        reqPermissionsCore(fragmentActivity, permissionMediator, onRequestCallback, permissions);
    }

    public static void reqPermissions(Fragment fragment, OnRequestCallback onRequestCallback, String ... permissions) {
        PermissionMediator permissionMediator = PermissionX.init(fragment);
        reqPermissionsCore(fragment.getContext(), permissionMediator, onRequestCallback, permissions);
    }

    private static void reqPermissionsCore(Context context, PermissionMediator permissionMediator, OnRequestCallback onRequestCallback, String ... permissions){
        permissionMediator.permissions(permissions)
            .onExplainRequestReason((scope, deniedList, beforeRequest) -> {
                scope.showRequestReasonDialog(deniedList, "앱을 사용하기 위해 아래 권한 허용이 필요합니다.", "허용");
            })
            .onForwardToSettings((scope, deniedList) -> {
                scope.showForwardToSettingsDialog(deniedList, "설정에서 다음 권한을 허용해주세요", "허용");
            })
            .request((allGranted, grantedList, deniedList) -> {
                if (!allGranted) {
                    Toast.makeText(context, "다음 권한들을 허용해주세요.：" + deniedList, Toast.LENGTH_SHORT).show();
                }
                else{
                    //모두 허가 되었을때.
                    onRequestCallback.onDone();
                }
            });
    }

    public interface OnRequestCallback {
        void onDone();
    }
}
