package com.codegear.mariamc_rfid.scanner.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.R;

public class PairingInstructionsRFD8500 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_instructions_rfd8500);

        Configuration configuration = getResources().getConfiguration();
        if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            if(configuration.smallestScreenWidthDp< Application.minScreenWidth){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }else{
            if(configuration.screenWidthDp<Application.minScreenWidth){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

    }

}
