package com.codegear.mariamc_rfid.rfidreader.settings;

/**
 * Created by XJR746 on 09-10-2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;


public class LedFragment extends BackPressedFragment {
    public static final String SHARED_PREF_NAME = "Switch";
    Context context;
    SharedPreferences mSharedPreferences;
    private CheckBox checkboxled;
    private TextView ledText;
    private static final String TAG = "LedFragment";

    public LedFragment() {}


    public static LedFragment newInstance() {
        return new LedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_led, container, false);


        mSharedPreferences = getActivity().getSharedPreferences("LEDPreferences", getContext().MODE_PRIVATE);
        String connectedReader = RFIDController.mConnectedReader.getHostName();
        if (connectedReader.startsWith("RFD40") || connectedReader.startsWith("RFD90") || connectedReader.startsWith("RFD8500")) {
            RFIDController.ledState = mSharedPreferences.getBoolean("LED_STATE1", false);
        } else {
            RFIDController.ledState = mSharedPreferences.getBoolean("LED_STATE1", true);
        }

        context = rootview.getContext();
        ledText = (TextView) rootview.findViewById(R.id.ledText);
        checkboxled = (CheckBox) rootview.findViewById(R.id.checkboxled);
        rootview.findViewById(R.id.saveConfigButton).setOnClickListener(v -> {
            saveConfigClicked(v);
        });


        if (RFIDController.mConnectedReader != null) {
            if (connectedReader.startsWith("RFD40") || connectedReader.startsWith("RFD90") || connectedReader.startsWith("RFD8500")) {
                ledText.setTextColor(Color.LTGRAY);
                checkboxled.setChecked(false);
                checkboxled.setEnabled(false);
                Toast.makeText(getContext(), "이 기능은 " + connectedReader.substring(0, 5) + "에서 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                return rootview;
            }

            if (RFIDController.ledState) {
                checkboxled.setChecked(true);
            } else {
                checkboxled.setChecked(false);
            }

        }


        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        else if (getActivity() instanceof ActiveDeviceActivity)
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
    }

    //수동 저장
    public void saveConfigClicked(View v){
        if (RFIDController.mConnectedReader != null) {
            String connectedReader = RFIDController.mConnectedReader.getHostName();
            if (connectedReader.startsWith("RFD40") || connectedReader.startsWith("RFD90") || connectedReader.startsWith("RFD8500")) {

                Toast.makeText(getContext(), "이 기능은 " + connectedReader.substring(0, 5) + "에서 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        isSettingsChanged();
    }


    public void deviceDisconnected() {
        checkboxled.setChecked(false);
    }



    private void isSettingsChanged() {
        if (checkboxled.isChecked()) {
            if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected())
                try {
                    RFIDController.mConnectedReader.Config.setLedBlinkEnable(true);
                } catch (InvalidUsageException e) {
                    if (e != null && e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                } catch (OperationFailureException e) {
                    if (e != null && e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                }
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("LED_STATE1", true);
            editor.apply();
        } else {
            try {
                if (RFIDController.mConnectedReader != null) {
                    RFIDController.mConnectedReader.Config.setLedBlinkEnable(false);
                }
            } catch (InvalidUsageException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            } catch (OperationFailureException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("LED_STATE1", false);
            editor.apply();
        }
    }
}
