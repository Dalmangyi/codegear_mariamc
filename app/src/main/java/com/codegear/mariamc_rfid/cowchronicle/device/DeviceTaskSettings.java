package com.codegear.mariamc_rfid.cowchronicle.device;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.rfidreader.common.CustomProgressDialog;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.settings.ProfileContent;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;

public class DeviceTaskSettings {

    //안테나 설정 저장용 Task
    public static class SaveAntennaConfigurationTask extends AsyncTask<Void, Void, Boolean> {

        private final String TAG = "AntennaConfiguration";
        private CustomProgressDialog progressDialog;
        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;
        private final int powerLevel;
        private Activity activity;

        public SaveAntennaConfigurationTask(int powerLevelIndex, Activity activity) {
            powerLevel = powerLevelIndex;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(activity, activity.getString(R.string.antenna_progress_title));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Antennas.AntennaRfConfig antennaRfConfig;
            try {
                antennaRfConfig = RFIDController.mConnectedReader.Config.Antennas.getAntennaRfConfig(1);
                antennaRfConfig.setTransmitPowerIndex(powerLevel);
                RFIDController.mConnectedReader.Config.Antennas.setAntennaRfConfig(1, antennaRfConfig);
                RFIDController.antennaRfConfig = antennaRfConfig;
                ProfileContent.UpdateActiveProfile();
                return true;
            } catch (InvalidUsageException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
                invalidUsageException = e;
            } catch (OperationFailureException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
                operationFailureException = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.cancel();
            if (!result) {
                if (invalidUsageException != null) {
                    CustomDialog.showSimpleError(activity, "" + activity.getString(R.string.status_failure_message) + "\n" + invalidUsageException.getVendorMessage());
                }
                if (operationFailureException != null) {
                    CustomDialog.showSimpleError(activity, "" + activity.getString(R.string.status_failure_message) + "\n" + operationFailureException.getVendorMessage());
                }
            }
            if (invalidUsageException == null && operationFailureException == null) {
                CustomDialog.showSimple(activity, "" + activity.getString(R.string.status_success_message));
            }
            super.onPostExecute(result);
        }
    }
}
