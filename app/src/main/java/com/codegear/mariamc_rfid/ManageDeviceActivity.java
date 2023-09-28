package com.codegear.mariamc_rfid;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.codegear.mariamc_rfid.rfidreader.home.RFIDBase;
import com.codegear.mariamc_rfid.scanner.helpers.Constants;
import com.codegear.mariamc_rfid.rfidreader.manager.DeviceResetFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.FactoryResetFragment;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;

import com.zebra.rfid.api3.STATUS_EVENT_TYPE;


public class ManageDeviceActivity extends AppCompatActivity implements Readers.RFIDReaderEventHandler, RfidEventsListener {

    private static final String MANAGEDEVICEFRAGMENT = "ManageDeviceFragment";
    private RFIDBase mRfidBase;
    Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_device_activity);
        this.setTitle("Device Logs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getExtras() != null) {
            switch (getIntent().getExtras().getInt(Constants.MNG_FRAGMENT_ID)) {
                case 0:
                    fragment = FactoryResetFragment.newInstance();
                    break;
                case 1:
                    fragment = DeviceResetFragment.newInstance();
                    break;
                case 2:
                    fragment = LoggerFragment.newInstance();
                    break;
            }
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        mRfidBase = RFIDBase.getInstance();
        mRfidBase.setReaderstatuscallback(this);
        if (fragment != null) {
            switchToFragment(fragment);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void switchToFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.manage_frame_layout, fragment, MANAGEDEVICEFRAGMENT).commit();
        }
    }

    public void resetFactoryDefault() throws InvalidUsageException, OperationFailureException {

        mRfidBase.resetFactoryDefault();
    }

    public boolean deviceReset(String commandString) throws InvalidUsageException, OperationFailureException {
        return mRfidBase.deviceReset();
    }

    @Override
    public void RFIDReaderAppeared(ReaderDevice readerDevice) {
        runOnUiThread(() -> {
            if (fragment != null) {
                if (fragment instanceof FactoryResetFragment) {
                    ((FactoryResetFragment) fragment).RFIDReaderAppeared(readerDevice);
                    //finish();

                } else if (fragment instanceof DeviceResetFragment) {
                    ((DeviceResetFragment) fragment).RFIDReaderAppeared(readerDevice);
                }
            }
        });
    }

    @Override
    public void RFIDReaderDisappeared(final ReaderDevice readerDevice) {
        //Intent intent;
        //intent = new Intent(getActivity(), DeviceDiscoverActivity.class);
        //intent.putExtra("enable_toolbar", false);
        //startActivity(intent);
        runOnUiThread(() -> {
            if (fragment != null) {
                if (fragment instanceof FactoryResetFragment) {
                    ((FactoryResetFragment) fragment).RFIDReaderDisappeared(readerDevice);

                } else if (fragment instanceof DeviceResetFragment) {
                    ((DeviceResetFragment) fragment).RFIDReaderDisappeared(readerDevice);
                } else if (fragment instanceof LoggerFragment) {
                    ((LoggerFragment) fragment).RFIDReaderDisappeared(readerDevice);
                }
            }
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRfidBase.resetReaderstatuscallback();


    }

    @Override
    protected void onResume() {
        super.onResume();
        mRfidBase.setReaderstatuscallback(this);
    }

    @Override
    public void eventReadNotify(RfidReadEvents rfidReadEvents) {

    }

    @Override
    public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
        if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.DISCONNECTION_EVENT) {
            if (fragment != null) {
                if (fragment instanceof FactoryResetFragment) {
                    ((FactoryResetFragment) fragment).eventStatusNotify(rfidStatusEvents);

                } else if (fragment instanceof FactoryResetFragment) {
                    ((DeviceResetFragment) fragment).eventStatusNotify(rfidStatusEvents);
                }
            }

            RFIDController.mConnectedReader = null;
        }
    }
}
