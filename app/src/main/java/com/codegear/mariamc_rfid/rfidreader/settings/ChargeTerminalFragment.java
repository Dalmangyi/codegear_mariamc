package com.codegear.mariamc_rfid.rfidreader.settings;


import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.MAIN_RFID_SETTINGS_TAB;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.rfidreader.common.CustomProgressDialog;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChargeTerminalFragment extends BackPressedFragment {
    Context context;
    private CheckBox checkBoxCT;
    private ProgressDialog progressDialog;
    private boolean chargeTerminalState;
    private static final String TAG = "ChargeTerminalFragment";


    private ChargeTerminalFragment() {

    }

    public static ChargeTerminalFragment newInstance() {
        return new ChargeTerminalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_charge_terminal, container, false);
        context = view.getContext();
        checkBoxCT = view.findViewById(R.id.check_box_ct);
        TextView tv = view.findViewById(R.id.ct_mesg);

        if (!RFIDController.mConnectedReader.getHostName().startsWith("RFD40") && !RFIDController.mConnectedReader.getHostName().startsWith("RFD90")) {
            tv.setVisibility(View.VISIBLE);
            checkBoxCT.setEnabled(false);
            checkBoxCT.setVisibility(View.INVISIBLE);

        } else {
            tv.setVisibility(View.VISIBLE);
            checkBoxCT.setEnabled(true);
        }


        view.findViewById(R.id.saveConfigButton).setOnClickListener(v -> {
            saveConfigClicked(v);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.Config != null) {
            chargeTerminalState = RFIDController.mConnectedReader.Config.getChargeTerminalState();
            checkBoxCT.setChecked(chargeTerminalState);
        }
    }

    @Override
    public void onBackPressed() {
        if (getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        if (getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(MAIN_RFID_SETTINGS_TAB);
        }
    }

    //수동 저장
    public void saveConfigClicked(View v){
        if (chargeTerminalState != checkBoxCT.isChecked()) {
            saveCTState();
        }
    }


    private void saveCTState() {
        progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.ct_settings));
        progressDialog.show();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                RFIDController.mConnectedReader.Config.setChargeTerminalEnable(checkBoxCT.isChecked());
            } catch (InvalidUsageException | OperationFailureException e) {
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            handler.post(() -> {
                progressDialog.dismiss();
                Toast.makeText(context, R.string.status_success_message, Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof SettingsDetailActivity)
                    ((SettingsDetailActivity) getActivity()).callBackPressed();
                if (getActivity() instanceof ActiveDeviceActivity) {
                    ((ActiveDeviceActivity) getActivity()).callBackPressed();
                    ((ActiveDeviceActivity) getActivity()).loadNextFragment(MAIN_RFID_SETTINGS_TAB);
                }
            });
        });
    }

}
