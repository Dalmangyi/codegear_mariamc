package com.codegear.mariamc_rfid.cowchronicle.device;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.rfid.RfidListeners;

import dmax.dialog.SpotsDialog;

public class DeviceController {

    private static final String TAG = "DeviceController";

    public interface OnControllerListener{
        void done();
        void error();
    }

    //인벤토리 작업 중지시키기
    public static void stopInventory(Activity mActivity, OnControllerListener onControllerListener){
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
            if (RFIDController.mIsInventoryRunning){

                AlertDialog dialogLoading = new SpotsDialog.Builder()
                        .setContext(mActivity)
                        .setTheme(R.style.CustomAlertDialog)
                        .setMessage("장치의 동작을 정지 중입니다.")
                        .build();
                RFIDController.getInstance().stopInventory(new RfidListeners() {
                    @Override
                    public void onSuccess(Object object) {
                        if (RFIDController.mIsInventoryRunning) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                if (e != null && e.getStackTrace().length > 0) {
                                    Log.e(TAG, e.getStackTrace()[0].toString());
                                }
                            }
                        }
                        Application.bBrandCheckStarted = false;
                        RFIDController.mIsInventoryRunning = false;

                        dialogLoading.dismiss();

                        if(onControllerListener != null){
                            onControllerListener.done();
                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        dialogLoading.dismiss();
                        CustomDialog.showSimpleError(mActivity, "정지 에러 : "+exception.getMessage());

                        if(onControllerListener != null){
                            onControllerListener.error();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        dialogLoading.dismiss();
                        CustomDialog.showSimpleError(mActivity, "정지 에러 : "+message);

                        if(onControllerListener != null){
                            onControllerListener.error();
                        }
                    }
                });
            }
            else{
                if(onControllerListener != null){
                    onControllerListener.done();
                }
            }
        }
        else {
            if(onControllerListener != null){
                onControllerListener.done();
            }
        }
    }
}
