package com.codegear.mariamc_rfid.rfidreader.home;

import static android.content.Intent.ACTION_SCREEN_OFF;
import static android.content.Intent.ACTION_SCREEN_ON;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.INVENTORY_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.LOCATE_TAG_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.PROFILES_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RAPID_READ_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.READERS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.READER_LIST_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_ABOUT_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_ACCESS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_PREFILTERS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SETTINGS_TAB;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.discover_connect.nfc.PairOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.access_operations.AccessOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.access_operations.AccessOperationsLockFragment;
import com.codegear.mariamc_rfid.rfidreader.common.Constants;
import com.codegear.mariamc_rfid.rfidreader.common.CustomProgressDialog;
import com.codegear.mariamc_rfid.rfidreader.common.CustomToast;
import com.codegear.mariamc_rfid.rfidreader.common.InventoryTimer;
import com.codegear.mariamc_rfid.rfidreader.common.MatchModeFileLoader;
import com.codegear.mariamc_rfid.rfidreader.common.asciitohex;
import com.codegear.mariamc_rfid.rfidreader.data_export.DataExportTask;
import com.codegear.mariamc_rfid.rfidreader.inventory.InventoryListItem;
import com.codegear.mariamc_rfid.rfidreader.inventory.RFIDInventoryFragment;
import com.codegear.mariamc_rfid.rfidreader.locate_tag.LocateOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.locate_tag.RangeGraph;
import com.codegear.mariamc_rfid.rfidreader.locate_tag.multitag_locate.MultiTagLocateResponseHandlerTask;
import com.codegear.mariamc_rfid.rfidreader.manager.FactoryResetFragment;
import com.codegear.mariamc_rfid.rfidreader.notifications.NotificationUtil;
import com.codegear.mariamc_rfid.rfidreader.rapidread.RapidReadFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.BluetoothHandler;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.RFIDReadersListFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.ScanAndPairFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.ScanPair;
import com.codegear.mariamc_rfid.rfidreader.rfid.ConnectionController;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.rfid.RfidListeners;
import com.codegear.mariamc_rfid.rfidreader.settings.AdvancedOptionItemFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.AdvancedOptionsContent;
import com.codegear.mariamc_rfid.rfidreader.settings.BackPressedFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ISettingsUtil;
import com.codegear.mariamc_rfid.rfidreader.settings.PreFilterFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ProfileContent;
import com.codegear.mariamc_rfid.rfidreader.settings.RegulatorySettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingListFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingsDetailActivity;
import com.codegear.mariamc_rfid.scanner.activities.UpdateFirmware;
import com.codegear.mariamc_rfid.wifi.ReaderWifiSettingsFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.common.ResponseHandlerInterfaces;
import com.codegear.mariamc_rfid.rfidreader.common.ResponseHandlerInterfaces.BatteryNotificationHandler;
import com.codegear.mariamc_rfid.rfidreader.common.ResponseHandlerInterfaces.ReaderDeviceFoundHandler;
import com.codegear.mariamc_rfid.rfidreader.common.ResponseHandlerInterfaces.TriggerEventHandler;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE;
import com.zebra.rfid.api3.Events;
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE;
import com.zebra.rfid.api3.HANDHELD_TRIGGER_TYPE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.LOCK_DATA_FIELD;
import com.zebra.rfid.api3.LOCK_PRIVILEGE;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.RfidWifiScanEvents;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
import com.zebra.rfid.api3.TagData;
import com.zebra.rfid.api3.WifiScanDataEventsListener;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;


public class RFIDBase implements Readers.RFIDReaderEventHandler, NavigationView.OnNavigationItemSelectedListener, ISettingsUtil, WifiScanDataEventsListener {
    //Tag to identify the currently displayed fragment
    protected static final String TAG_RFID_FRAGMENT = "RFIDHomeFragment";
    //Messages for progress bar
    private static final String MSG_READ = "태그 데이터 읽기 명령 실행 중";
    private static final String MSG_WRITE = "데이터 쓰기 명령 실행 중";
    private static final String MSG_LOCK = "잠금 명령 실행 중";
    private static final String MSG_KILL = "지우기 명령 실행 중";
    private static final int BEEP_DELAY_TIME_MIN = 0;
    private static final int BEEP_DELAY_TIME_MAX = 300;
    public static final String BRAND_ID = "brandid";
    public static final String EPC_LEN = "epclen";
    public static final String IS_BRANDID_CHECK = "brandidcheck";
    private CreateFileInterface createFileInterface;
    /**
     * method to start a timer task for LED glow for the duration of 10ms
     */
    public static Timer tLED;
    private static ArrayList<ReaderDeviceFoundHandler> readerDeviceFoundHandlers = new ArrayList<>();
    private static ArrayList<BatteryNotificationHandler> batteryNotificationHandlers = new ArrayList<>();
    private static RFIDBase mRfidBase;
    public Uri uri;
    /**
     * method to start a timer task to beep for the duration of 10ms
     */
    public Timer tbeep;
    /**
     * method to start a timer task to beep for locate functionality and configure the ON OFF duration.
     */
    public Timer locatebeep;

    protected boolean isInventoryAbortedNotifier;

    protected int accessTagCount;
    //To indicate indeterminate progress
    protected CustomProgressDialog progressDialog;
    protected Menu menu;
    NotificationManager notificationManager;
    MediaPlayer mPlayer;
    //Special layout for Navigation Drawer
    private DrawerLayout mDrawerLayout;
    //List view for navigation drawer items
    private CharSequence mTitle;
    private String[] mOptionTitles;
    ImageView ivBatteryLevel;

    public static AsyncTask<Void, Void, Boolean> DisconnectTask;

    //for beep and LED
    private boolean beepON = false;
    private boolean beepONLocate = false;
    private String TAG = "123RFIDMobile";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 10;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_CSV = 11;

    //common Result Intent broadcasted by DataWedge
    private static final String DW_APIRESULT_ACTION = "com.symbol.datawedge.api.RESULT_ACTION";
    private static final String scanner_status = "com.symbol.datawedge.scanner_status";
    private NavigationView navigationView;


    //public static EventHandler eventHandler;

    private static boolean activityVisible = true;
    private ActiveDeviceActivity mActivity;
    private Readers.RFIDReaderEventHandler mReaderstatuscallback;
    private android.content.BroadcastReceiver mIdlemodechangereciever = null;
    private android.content.BroadcastReceiver mDeviceIdlereceiver = null;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;

    }

    public static void activityPaused() {
        activityVisible = false;
    }

    //For multitag locate operation
    protected boolean isMultiTagLocationingAborted;

    /**
     * Method for registering the classes for device events like paired,unpaired, connected and disconnected.
     * The registered classes will get notified when device event occurs.
     *
     * @param readerDeviceFoundHandler - handler class to register with base receiver activity
     */
    public static void addReaderDeviceFoundHandler(ReaderDeviceFoundHandler readerDeviceFoundHandler) {
        readerDeviceFoundHandlers.add(readerDeviceFoundHandler);
    }

    public static void addBatteryNotificationHandler(BatteryNotificationHandler batteryNotificationHandler) {
        batteryNotificationHandlers.add(batteryNotificationHandler);
    }

    public static void removeReaderDeviceFoundHandler(ReaderDeviceFoundHandler readerDeviceFoundHandler) {
        readerDeviceFoundHandlers.remove(readerDeviceFoundHandler);
    }

    public static void removeBatteryNotificationHandler(BatteryNotificationHandler batteryNotificationHandler) {
        batteryNotificationHandlers.remove(batteryNotificationHandler);
    }


    private static FragmentManager supportmanager;
    ExtendedFloatingActionButton inventoryBT = null;
    private Toast myToast;
    private static final int TIME_DELAY = 4000;
    private static long lastToastShowTime = 0;

    public static RFIDBase getInstance() {

        if (mRfidBase == null) {
            mRfidBase = new RFIDBase();
        }
        return mRfidBase;
    }


    public void reInit(ActiveDeviceActivity mActivity) {
        if (Application.RFIDBAseEventHandler == null) {
            Application.RFIDBAseEventHandler = new EventHandler(mActivity);
        }

        //initializeConnectionSettings();

        try {
            if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.Events != null) {
                mActivity.getDeviceAdapter().setDeviceModelName(RFIDController.mConnectedReader.getHostName());
                RFIDController.mConnectedReader.Events.removeEventsListener(Application.RFIDBAseEventHandler);
                RFIDController.mConnectedReader.Events.addEventsListener(Application.RFIDBAseEventHandler);
                RFIDController.mConnectedReader.Events.removeWifiScanDataEventsListener(this);
                RFIDController.mConnectedReader.Events.addWifiScanDataEventsListener(this);
                RFIDController.mConnectedReader.Events.setHandheldEvent(true);
                RFIDController.mConnectedReader.reinitTransport();
                //RFIDController.settings_startTrigger = RFIDController.mConnectedReader.Config.getStartTrigger();
                //RFIDController.settings_stopTrigger = RFIDController.mConnectedReader.Config.getStopTrigger();
                Readers.attach(this);

            }
        } catch (InvalidUsageException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        } catch (OperationFailureException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        } catch (ClassCastException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        }

        InventoryTimer.getInstance().setActivity(mActivity);

    }

    public void onCreate(ActiveDeviceActivity mActivity) {

        this.mActivity = mActivity;


        if (Application.RFIDBAseEventHandler == null) {
            Application.RFIDBAseEventHandler = new EventHandler(mActivity);
        }

        initializeConnectionSettings();

        try {
            if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.Events != null) {
                RFIDController.mConnectedReader.Events.removeEventsListener(Application.RFIDBAseEventHandler);
                RFIDController.mConnectedReader.Events.addEventsListener(Application.RFIDBAseEventHandler);
                RFIDController.mConnectedReader.Events.removeWifiScanDataEventsListener(this);
                RFIDController.mConnectedReader.Events.addWifiScanDataEventsListener(this);
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

        InventoryTimer.getInstance().setActivity(mActivity);

        RFIDController.readers.attach(this);

        // Create a filter for the broadcast intent
        IntentFilter filter = new IntentFilter();
        // filter.addAction(scanner_status);
        filter.addAction(ACTION_SCREEN_OFF);
        filter.addAction(ACTION_SCREEN_ON);
        filter.addAction(DW_APIRESULT_ACTION);
        filter.addCategory("android.intent.category.DEFAULT");
        mActivity.registerReceiver(BroadcastReceiver, filter);

        RFIDController.getInstance().clearAllInventoryData();
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.getHostName() != null && RFIDController.mConnectedReader.getHostName().startsWith("MC33")) {
            Intent i = new Intent();
            i.setAction("com.symbol.datawedge.api.ACTION");
            i.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "DISABLE_PLUGIN");
            i.putExtra("SEND_RESULT", "false");
            i.putExtra("COMMAND_IDENTIFIER", Application.RFID_DATAWEDGE_DISABLE_SCANNER);  //Unique identifier
            mActivity.sendBroadcast(i);

        }

        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        mActivity.registerReceiver(mReceiver, bluetoothFilter);
        enableBatterySaverModeChangeListner();

        ivBatteryLevel = mActivity.findViewById(R.id.appbar_batteryLevelImage);

//         PP+ Battery support
        ivBatteryLevel.setOnClickListener(v -> mActivity.showBatteryStats());

        createFileInterface = mActivity;
        uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
    }

    public int getDeviceMode(String modelName, int currentScannerId) {
        if (modelName.startsWith("RFD40+")) {
            return Application.DEVICE_PREMIUM_PLUS_MODE;
        } else if (modelName.startsWith("RFD90")) {
            return Application.DEVICE_PREMIUM_PLUS_MODE;
        } else if (modelName.startsWith("RFD90+")) {
            return Application.DEVICE_PREMIUM_PLUS_MODE;
        } else if (modelName.startsWith("RFD40")) {
            String[] splitStr = modelName.split("-");
            if (splitStr[0].equals("RFD4030")) {
                if (splitStr[1].contains("G0")) {
                    return Application.DEVICE_STD_MODE;
                }
            } else if (splitStr[0].equals("RFD4031")) {
                if (splitStr[1].contains("G0")) {
                    return Application.DEVICE_STD_MODE;
                } else if (splitStr[1].contains("G1")) {
                    return Application.DEVICE_PREMIUM_PLUS_MODE;
                }
            }
        } else if (modelName.startsWith("RFD8500")) {
            String ScannerVersionInfo = Application.versionInfo.get("PL33");

            if (ScannerVersionInfo != null && ScannerVersionInfo.equals(""))
                return Application.DEVICE_STD_MODE;
            else return Application.DEVICE_PREMIUM_PLUS_MODE;
        }
        return Application.DEVICE_STD_MODE;
    }

    private void disableBatterySaverModeChangeListner() {

        if (mIdlemodechangereciever != null) mActivity.unregisterReceiver(mIdlemodechangereciever);
        if (mDeviceIdlereceiver != null) mActivity.unregisterReceiver(mDeviceIdlereceiver);
    }

    private void enableBatterySaverModeChangeListner() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDeviceIdlereceiver = new BroadcastReceiver() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onReceive(Context context, Intent intent) {
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

                    if (pm.isDeviceIdleMode()) {
                        Log.d(TAG, " Device Idle Mode");
                    } else {
                        Log.d(TAG, " Not in Device Idle Mode");
                    }
                }
            };

            mActivity.registerReceiver(mDeviceIdlereceiver, new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED));

            mIdlemodechangereciever = new BroadcastReceiver() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onReceive(Context context, Intent intent) {
                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

                    //if (pm.isPowerSaveMode())
                    {
                        Log.d(TAG, "In Power Save Mode");
                        if (RFIDController.mIsInventoryRunning) {
                            inventoryStartOrStop();
                            try {
                                RFIDController.mConnectedDevice.getRFIDReader().disconnect();
                                RFIDController.mConnectedDevice.getRFIDReader().Dispose();
                            } catch (InvalidUsageException e) {
                                if (e != null && e.getStackTrace().length > 0) {
                                    Log.e(TAG, e.getStackTrace()[0].toString());
                                }
                            } catch (OperationFailureException e) {
                                if (e != null && e.getStackTrace().length > 0) {
                                    Log.e(TAG, e.getStackTrace()[0].toString());
                                }
                            } catch (Exception e) {
                                //if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
                            }
                        }
                    }
                }
            };

            mActivity.registerReceiver(mIdlemodechangereciever, new IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED));
        }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        handleBTon();
                        if (RFIDReadersListFragment.getInstance().isVisible()) {

                            RFIDReadersListFragment.getInstance().loadUIData();
                        }
                        //Toast.makeText(context, "Bluetooth on", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // setButtonText("Turning Bluetooth on...");
                        break;
                }

            }

            final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            if (bondState == BluetoothDevice.BOND_NONE) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.getHostName() != null && RFIDController.mConnectedReader.getHostName().equals(device.getName())) {

                    if (RFIDController.mConnectedReader.isConnected()) {
                        try {
                            RFIDController.mConnectedReader.disconnect();
                        } catch (InvalidUsageException e) {
                            if (e != null && e.getStackTrace().length > 0) {
                                Log.e(TAG, e.getStackTrace()[0].toString());
                            }
                        } catch (OperationFailureException e) {
                            if (e != null && e.getStackTrace().length > 0) {
                                Log.e(TAG, e.getStackTrace()[0].toString());
                            }
                        }

                        clearConnectedReader();
                        //BluetoothHandler.pair(device.getAddress());
                    } else {
                        clearConnectedReader();
                    }

                } else if (RFIDController.LAST_CONNECTED_READER.equals(device.getName())) {
                    clearConnectedReader();

                }
            }
        }
    };

    private void handleBTon() {

    }

    private void handleBToff() {

        for (ReaderDevice readerDevice : RFIDController.readersList) {
            if (readerDevice.getTransport().equals("bluetooth") == true)
                RFIDController.readersList.remove(readerDevice);
        }

    }


    private void ledsettigs() {
        SharedPreferences sharedPref = mActivity.getSharedPreferences("LEDPreferences", Context.MODE_PRIVATE);
        RFIDController.ledState = sharedPref.getBoolean("LED_STATE1", true);

    }

    private void beeperSettings() {
        SharedPreferences sharedPref = mActivity.getSharedPreferences(mActivity.getString(R.string.pref_beeper), Context.MODE_PRIVATE);
        int volume;
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.getHostName() != null && RFIDController.mConnectedReader.getHostName().startsWith("MC33")) {
            volume = sharedPref.getInt(mActivity.getString(R.string.beeper_volume), 0);
        } else {
            volume = sharedPref.getInt(mActivity.getString(R.string.beeper_volume), 3);
        }

        int streamType = AudioManager.STREAM_DTMF;
        int percantageVolume = 100;
        if (volume == 0) {
            RFIDController.beeperVolume = BEEPER_VOLUME.HIGH_BEEP;
            percantageVolume = 100;
        }
        if (volume == 1) {
            RFIDController.beeperVolume = BEEPER_VOLUME.MEDIUM_BEEP;
            percantageVolume = 75;
        }
        if (volume == 2) {
            RFIDController.beeperVolume = BEEPER_VOLUME.LOW_BEEP;
            percantageVolume = 50;
        }
        if (volume == 3) {
            RFIDController.beeperVolume = BEEPER_VOLUME.QUIET_BEEP;
            percantageVolume = 0;
        }

        try {
            RFIDController.toneGenerator = new ToneGenerator(streamType, percantageVolume);
        } catch (RuntimeException exception) {
            RFIDController.toneGenerator = null;

        }
    }

    private String getReaderPassword(String address) {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(Constants.READER_PASSWORDS, 0);
        return sharedPreferences.getString(address, null);
    }


    private void EnableLogger() {
        if (RFIDController.mConnectedReader != null)
            RFIDController.mConnectedReader.Config.setLogLevel(Level.INFO);
    }

    private void PrintLogs() {
        if (RFIDController.mConnectedReader != null) {
            try {
                String str[] = RFIDController.mConnectedReader.Config.GetLogBuffer().split("\n");
                for (String st : str) {
                    Log.d(TAG, st);
                }
            } catch (InvalidUsageException e1) {
                //e1.printStackTrace();
            }
        }
    }

    /**
     * Method to initialize the connection settings like notifications, auto detection, auto reconnection etc..
     */
    private void initializeConnectionSettings() {
        SharedPreferences settings = mActivity.getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
        RFIDController.AUTO_DETECT_READERS = settings.getBoolean(Constants.AUTO_DETECT_READERS, true);
        RFIDController.AUTO_RECONNECT_READERS = settings.getBoolean(Constants.AUTO_RECONNECT_READERS, true);
        RFIDController.NOTIFY_READER_AVAILABLE = settings.getBoolean(Constants.NOTIFY_READER_AVAILABLE, false);
        RFIDController.NOTIFY_READER_CONNECTION = settings.getBoolean(Constants.NOTIFY_READER_CONNECTION, false);
        if (Build.MODEL.contains("MC33"))
            RFIDController.NOTIFY_BATTERY_STATUS = settings.getBoolean(Constants.NOTIFY_BATTERY_STATUS, false);
        else
            RFIDController.NOTIFY_BATTERY_STATUS = settings.getBoolean(Constants.NOTIFY_BATTERY_STATUS, true);
        RFIDController.EXPORT_DATA = settings.getBoolean(Constants.EXPORT_DATA, false);
        Application.TAG_LIST_MATCH_MODE = settings.getBoolean(Constants.TAG_LIST_MATCH_MODE, false);
        RFIDController.SHOW_CSV_TAG_NAMES = settings.getBoolean(Constants.SHOW_CSV_TAG_NAMES, false);
        RFIDController.asciiMode = settings.getBoolean(Constants.ASCII_MODE, false);
        RFIDController.sgtinMode = settings.getBoolean(Constants.SGTIN_MODE, false);
        RFIDController.NON_MATCHING = settings.getBoolean(Constants.NON_MATCHING, false);
        RFIDController.LAST_CONNECTED_READER = settings.getString(Constants.LAST_READER, "");
        LoadProfiles();
        beeperSettings();
        ledsettigs();
        LoadTagListCSV();
        loadBrandIdValues();
    }


    public void loadBrandIdValues() {
        SharedPreferences pref = mActivity.getSharedPreferences("BrandIdValues", 0);
        Application.strBrandID = pref.getString(BRAND_ID, "AAAA"); // getting String
        Application.iBrandIDLen = pref.getInt(EPC_LEN, 12); // getting Integer
        RFIDController.brandidcheckenabled = pref.getBoolean(IS_BRANDID_CHECK, false);
    }


    private void LoadProfiles() {
        ProfileContent content = new ProfileContent(mActivity);
        content.LoadDefaultProfiles();
    }


    public void onDestroy() {

        mActivity.unregisterReceiver(BroadcastReceiver);
        mActivity.unregisterReceiver(mReceiver);
        disableBatterySaverModeChangeListner();

        if(Application.RFIDBAseEventHandler != null){
            try {
                RFIDController.mConnectedReader.Events.removeEventsListener(Application.RFIDBAseEventHandler);
            } catch (InvalidUsageException e) {
                throw new RuntimeException(e);
            } catch (OperationFailureException e) {
                throw new RuntimeException(e);
            }
            Application.RFIDBAseEventHandler = null;
        }

        RFIDController.readers.deattach(this);

        if (myToast != null) myToast.cancel();

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_dpo:
                Intent detailsIntent = new Intent(mActivity, SettingsDetailActivity.class);
                detailsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                detailsIntent.putExtra(Constants.SETTING_ITEM_ID, R.id.battery);
                //mActivity.startActivity(detailsIntent);
                return true;
        }
        return true;
    }


    public void setTitle(CharSequence title) {
        mTitle = title;
        mActivity.getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_rapidread:
                //selectItem(1);
                mActivity.loadNextFragment(RAPID_READ_TAB);
                break;
            case R.id.nav_inventory:
                //selectItem(2);
                mActivity.loadNextFragment(INVENTORY_TAB);
                break;
            case R.id.nav_locatetag:
                //selectItem(3);
                mActivity.loadNextFragment(LOCATE_TAG_TAB);
                break;
            case R.id.nav_profiles:
                //selectItem(9);
                mActivity.loadNextFragment(PROFILES_TAB);
                break;
            case R.id.nav_settings:
                //selectItem(4);
                mActivity.loadNextFragment(RFID_SETTINGS_TAB);
                break;
            case R.id.nav_access_control:
                //selectItem(5);
                mActivity.loadNextFragment(RFID_ACCESS_TAB);
                break;
            case R.id.nav_prefilters:
                //selectItem(6);
                mActivity.loadNextFragment(RFID_PREFILTERS_TAB);
                break;
//            case R.id.nav_readerslist:
//                selectItem(7);
//                break;
        }
        DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onResume() {
        activityResumed();
    }


    /**
     * call back of activity,which will call before activity went to paused
     */

    public void onPause() {
        activityPaused();
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    public void onConfigurationChanged(Configuration newConfig) {

        // Checks whether a hardware keyboard is available
        //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (fragment != null && fragment instanceof RFIDInventoryFragment && newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            mActivity.findViewById(R.id.inventoryDataLayout).setVisibility(View.INVISIBLE);
            mActivity.findViewById(R.id.inventoryButton).setVisibility(View.INVISIBLE);
        } else if (fragment != null && fragment instanceof RFIDInventoryFragment && newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            mActivity.findViewById(R.id.inventoryDataLayout).setVisibility(View.VISIBLE);
            mActivity.findViewById(R.id.inventoryButton).setVisibility(View.VISIBLE);
        }
    }


    public void onBackPressed() {
        {
            Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
            if (fragment != null && fragment instanceof BackPressedFragment) {
                ((BackPressedFragment) fragment).onBackPressed();
            } else if (fragment != null /*&& fragment instanceof RFIDInventoryFragment*/) {
                //stop Timer
                InventoryTimer.getInstance().stopTimer();
                RFIDController.getInstance().stopTimer();
                //
                if (DisconnectTask != null) DisconnectTask.cancel(true);

                //Alert Dialog
                showMessageOKCancel("이 애플리케이션을 닫으시겠습니까?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //disconnect from reader
                        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.Events != null) {

                            try {
                                if (RFIDController.mConnectedReader.isConnected()) {
                                    if (RFIDController.mIsInventoryRunning)
                                        RFIDController.mConnectedReader.Actions.Inventory.stop();
                                    RFIDController.mConnectedReader.Events.removeEventsListener(Application.RFIDBAseEventHandler);
                                    RFIDController.mConnectedReader.Events.removeWifiScanDataEventsListener(RFIDBase.this);
                                }
                                RFIDController.mConnectedReader.disconnect();
                            } catch (InvalidUsageException e) {
                                if (e != null && e.getStackTrace().length > 0) {
                                    Log.e(TAG, e.getStackTrace()[0].toString());
                                }
                            } catch (OperationFailureException e) {
                                if (e != null && e.getStackTrace().length > 0) {
                                    Log.e(TAG, e.getStackTrace()[0].toString());
                                }
                            }
                        }
                        RFIDController.mConnectedReader = null;
                        // update dpo icon in settings list
                        AdvancedOptionsContent.ITEMS.get(AdvancedOptionsContent.DPO_ITEM_INDEX).icon = R.drawable.title_dpo_disabled;
                        RFIDController.getInstance().clearSettings();
                        RFIDController.mConnectedDevice = null;
                        RFIDController.mConnectedReader = null;
                        RFIDController.readersList.clear();
                        if (RFIDController.readers != null) {
                            RFIDController.readers.deattach(RFIDBase.this);
                            RFIDController.readers.Dispose();
                            RFIDController.readers = null;
                        }
                        RFIDController.reset();

                    }
                });
            } else {

            }
        }

    }

    /**
     * Callback method to handle the click of start/stop button in the inventory fragment
     */
    public synchronized void inventoryStartOrStop() {


        if (MatchModeFileLoader.getInstance(mActivity.getApplicationContext()).isImportTaskRunning()) {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.loading_csv), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Loading CSV");
            return;
        }
        if (Application.mIsMultiTagLocatingRunning == true) {
            Toast.makeText(mActivity.getApplicationContext(), "작업 진행 중 - 명령이 허용되지 않음.", Toast.LENGTH_SHORT).show();
            return;
        }
        //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (fragment != null && fragment instanceof RFIDInventoryFragment) {
            inventoryBT = mActivity.findViewById(R.id.inventoryButton);
        } else if (fragment != null && fragment instanceof RapidReadFragment) {
            inventoryBT = mActivity.findViewById(R.id.rr_inventoryButton);
        }

        //tagListMatchNotice = false;
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
            if (!RFIDController.mIsInventoryRunning) {
                RFIDController.clearInventoryData();
                //button.setText("STOP");
                if (inventoryBT != null) {
                    inventoryBT.setIconResource(R.drawable.ic_play_stop);
                }
                //Here we send the inventory command to start reading the tags
                if (fragment != null && fragment instanceof RFIDInventoryFragment) {
                    Spinner memoryBankSpinner = ((Spinner) mActivity.findViewById(R.id.inventoryOptions));
                    memoryBankSpinner.setSelection(Application.memoryBankId);
                    memoryBankSpinner.setEnabled(false);
                    ((RFIDInventoryFragment) fragment).resetTagsInfo();
                }
                //set flag value
                RFIDController.isInventoryAborted = false;
                RFIDController.getInstance().getTagReportingFields();
                //if (!Application.mConnectedReader.getHostName().startsWith("RFD8500") && Application.batchMode != 2 || Application.mConnectedReader.getHostName().startsWith("RFD8500") && Application.batchMode != 2)
                {
                    PrepareMatchModeList();
                }
                // UI update for inventory fragment
                if (fragment != null && fragment instanceof RFIDInventoryFragment && Application.TAG_LIST_MATCH_MODE) {
                    // TODO: This logic requires updates adpater being assigned particular list
                    if (Application.memoryBankId == 0) {
                        ((RFIDInventoryFragment) fragment).getAdapter().originalInventoryList = Application.tagsReadInventory;
                        ((RFIDInventoryFragment) fragment).getAdapter().searchItemsList = Application.tagsReadInventory;
                        ((RFIDInventoryFragment) fragment).getAdapter().notifyDataSetChanged();
                    } else if (Application.memoryBankId == 1) {  //matching tags
                        ((RFIDInventoryFragment) fragment).getAdapter().originalInventoryList = Application.matchingTagsList;
                        ((RFIDInventoryFragment) fragment).getAdapter().searchItemsList = Application.matchingTagsList;
                        ((RFIDInventoryFragment) fragment).getAdapter().notifyDataSetChanged();
                    } else if (Application.memoryBankId == 2) {  //missing tags
                        Application.missingTagsList.addAll(Application.tagsListCSV);
                        ((RFIDInventoryFragment) fragment).getAdapter().originalInventoryList = Application.missingTagsList;
                        ((RFIDInventoryFragment) fragment).getAdapter().searchItemsList = Application.missingTagsList;
                        ((RFIDInventoryFragment) fragment).getAdapter().notifyDataSetChanged();
                    } else if (Application.memoryBankId == 3) {  //unknown tags
                        ((RFIDInventoryFragment) fragment).getAdapter().originalInventoryList = Application.unknownTagsList;
                        ((RFIDInventoryFragment) fragment).getAdapter().searchItemsList = Application.unknownTagsList;
                        ((RFIDInventoryFragment) fragment).getAdapter().notifyDataSetChanged();
                    }
                    Application.tagsReadForSearch.addAll(((RFIDInventoryFragment) fragment).getAdapter().searchItemsList);
                }
                // UI update for RR fragment
                if (fragment != null && fragment instanceof RapidReadFragment) {
                    Application.memoryBankId = -1;
                    ((RapidReadFragment) fragment).resetTagsInfo();
                    if (Application.TAG_LIST_MATCH_MODE) {
                        if (Application.missedTags > 9999) {
                            TextView uniqueTags = (TextView) mActivity.findViewById(R.id.uniqueTagContent);
                            //orignal size is 60sp - reduced size 45sp
                            uniqueTags.setTextSize(45);
                        }
                    }
                    ((RapidReadFragment) fragment).updateTexts();
                }
                // perform read or inventory
                if (fragment != null && fragment instanceof RFIDInventoryFragment && !RFIDController.regionNotSet && !((RFIDInventoryFragment) fragment).getMemoryBankID().equalsIgnoreCase("none") && !Application.TAG_LIST_MATCH_MODE) {
                    //If memory bank is selected, call read command with appropriate memory bank
                    RFIDController.getInstance().inventoryWithMemoryBank(((RFIDInventoryFragment) fragment).getMemoryBankID(), new RfidListeners() {
                        @Override
                        public void onSuccess(Object object) {
                            Log.d(TAG, "onSuccess");
                        }

                        @Override
                        public void onFailure(Exception exception) {

                            //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
                            Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
                            if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                                ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(((OperationFailureException) exception).getResults());
                            sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((OperationFailureException) exception).getVendorMessage());
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                            if (inventoryBT != null) {
                                inventoryBT.setIconResource(android.R.drawable.ic_media_play);
                            }
                        }
                    });
                } else {
                    //Perform inventory
                    try {
                        RFIDController.mIsInventoryRunning = true;
                        RFIDController.getInstance().performInventory(new RfidListeners() {
                            @Override
                            public void onSuccess(Object object) {
                                //Log.d(TAG, "onSuccess");
                                RFIDController.tagListMatchNotice = false;
                                RFIDController.mIsInventoryRunning = true;
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
                                if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                                    ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(((OperationFailureException) exception).getResults());


                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((OperationFailureException) exception).getVendorMessage());
                            }

                            @Override
                            public void onFailure(String message) {
                                if (inventoryBT != null) {
                                    inventoryBT.setIconResource(android.R.drawable.ic_media_play);
                                }
                            }
                        });
                    } catch (Exception e) {
                        if (e != null && e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }
                }
                //Perform stop inventory
            } else if (RFIDController.mIsInventoryRunning) {
                RFIDController.mInventoryStartPending = false;
                if (fragment != null && fragment instanceof RFIDInventoryFragment) {
                    ((Spinner) mActivity.findViewById(R.id.inventoryOptions)).setEnabled(true);
                }
                //button.setText("START");
                if (inventoryBT != null) {
                    inventoryBT.setIconResource(android.R.drawable.ic_media_play);
                }

                RFIDController.isInventoryAborted = true;
                //Here we send the abort command to stop the inventory
                try {
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
                            if (fragment instanceof RFIDInventoryFragment)
                                ((RFIDInventoryFragment) fragment).resetInventoryDetail();
                            else if (fragment instanceof RapidReadFragment)
                                ((RapidReadFragment) fragment).resetInventoryDetail();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            if (exception == null || exception instanceof OperationFailureException) {
                                operationHasAborted();
                            }
                        }

                        @Override
                        public void onFailure(String message) {
                            if (inventoryBT != null) {
                                inventoryBT.setIconResource(android.R.drawable.ic_media_play);
                            }
                        }
                    });
                    Log.d(TAG, "Inventory.stop");
                } catch (Exception e) {
                    if (e != null && e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                }
            }
        } else
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback method to handle the click of start/stop button in the multitag locate fragment
     *
     * @param v - Button Clicked
     */
    public void multiTagLocateStartOrStop(View v) {
        //if (isBluetoothEnabled() || !RFIDController.mConnectedReader.getHostName().startsWith("RFD8500")) {
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
            if (Application.multiTagLocateTagListExist) {
                //Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
                //Fragment fragment = mActivity.getCurrentFragment(mActivity.RFID_TAB);
                if (!Application.mIsMultiTagLocatingRunning) {
                    ((FloatingActionButton) v).setImageResource(R.drawable.ic_play_stop);

                    Application.mIsMultiTagLocatingRunning = true;
                    new AsyncTask<Void, Void, Boolean>() {
                        private InvalidUsageException invalidUsageException;
                        private OperationFailureException operationFailureException;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            ((FloatingActionButton) v).setEnabled(false);
                            ((FloatingActionButton) v).setClickable(false);
                        }

                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            try {
                                RFIDController.mConnectedReader.Actions.MultiTagLocate.perform();
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
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            if (invalidUsageException != null) {
                                if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                                    ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(operationFailureException.getResults());
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, invalidUsageException.getInfo());
                            } else if (operationFailureException != null) {
                                if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                                    ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(operationFailureException.getResults());
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, operationFailureException.getVendorMessage());
                            }
                            if (invalidUsageException == null && operationFailureException == null) {
                                ((LocateOperationsFragment) fragment).enableGUIComponents(false);
                            }

                            //new Handler().postDelayed(new Runnable() {
                            //    @Override
                            //    public void run() {
                            // This method will be executed once the timer is over
                            ((FloatingActionButton) v).setClickable(true);
                            ((FloatingActionButton) v).setEnabled(true);

                            //   }
                            //},1000);// set time as per your requiremen

                        }
                    }.execute();
                } else if (Application.mIsMultiTagLocatingRunning) {
                    new AsyncTask<Void, Void, Boolean>() {
                        private InvalidUsageException invalidUsageException;
                        private OperationFailureException operationFailureException;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            ((FloatingActionButton) v).setEnabled(false);
                            ((FloatingActionButton) v).setClickable(false);
                        }

                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            try {
                                RFIDController.mConnectedReader.Actions.MultiTagLocate.stop();
                                if (((RFIDController.settings_startTrigger != null && (RFIDController.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD || RFIDController.settings_startTrigger.getTriggerType() == START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC))) || (Application.isBatchModeInventoryRunning != null && Application.isBatchModeInventoryRunning))
                                    operationHasAborted();
                            } catch (InvalidUsageException e) {
                                invalidUsageException = e;
                                if (e != null && e.getStackTrace().length > 0) {
                                    Log.e(TAG, e.getStackTrace()[0].toString());
                                }
                            } catch (OperationFailureException e) {
                                operationFailureException = e;
                                if (e != null && e.getStackTrace().length > 0) {
                                    Log.e(TAG, e.getStackTrace()[0].toString());
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            if (invalidUsageException != null) {
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, invalidUsageException.getInfo());
                            } else if (operationFailureException != null) {
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, operationFailureException.getVendorMessage());
                            }
                            if (invalidUsageException == null && operationFailureException == null) {
                                ((LocateOperationsFragment) fragment).enableGUIComponents(true);
                            }

                            // new Handler().postDelayed(new Runnable() {
                            //    @Override
                            ///    public void run() {
                            // This method will be executed once the timer is over
                            ((FloatingActionButton) v).setClickable(true);
                            ((FloatingActionButton) v).setEnabled(true);
                            //   }
                            //},1000);// set time as per your requiremen

                        }
                    }.execute();
                    ((FloatingActionButton) v).setImageResource(android.R.drawable.ic_media_play);
                    isMultiTagLocationingAborted = true;
                }
            } else
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_no_data_loaded), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        //} else
        //    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    public void multiTagLocateAddTagItem(View v) {
        //if (isBluetoothEnabled() || !RFIDController.mConnectedReader.getHostName().startsWith("RFD8500")) {
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
            if (Application.multiTagLocateTagListExist || Application.multiTagLocatelastTag) {
                if (!Application.mIsMultiTagLocatingRunning) {
                    String tagID = ((AutoCompleteTextView) mActivity.findViewById(R.id.multiTagLocate_epc)).getText().toString();
                    if (RFIDController.asciiMode) {
                        tagID = asciitohex.convert(tagID).toUpperCase();
                    }
                    if (!tagID.isEmpty()) {
                        if (Application.multiTagLocateTagListMap.containsKey(tagID)) {
                            try {
                                if (RFIDController.mConnectedReader.Actions.MultiTagLocate.addItem(tagID, Application.multiTagLocateTagMap.get(tagID)) == 0) {
                                    Application.multiTagLocateTagListMap.get(tagID).setReadCount(0);
                                    Application.multiTagLocateTagListMap.get(tagID).setProximityPercent((short) 0);
                                    Application.multiTagLocateActiveTagItemList.add(Application.multiTagLocateTagListMap.get(tagID));
                                    ((LocateOperationsFragment) mActivity.getCurrentFragment(RFID_TAB)).handleLocateTagResponse();
                                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_add_item_success), Toast.LENGTH_SHORT).show();
                                    if (Application.multiTagLocatelastTag == true) {
                                        Application.multiTagLocateTagListExist = true;
                                        Application.multiTagLocatelastTag = false;
                                    }
                                } else
                                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_add_item_failed), Toast.LENGTH_SHORT).show();
                            } catch (InvalidUsageException e) {
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getInfo());
                            } catch (OperationFailureException e) {
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getVendorMessage());
                            }
                        } else
                            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_add_item_failed), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_operation_running), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_no_data_loaded), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        //} else
        //    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    public void multiTagLocateDeleteTagItem(View v) {
        //if (isBluetoothEnabled() || !RFIDController.mConnectedReader.getHostName().startsWith("RFD8500")) {
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
            if (Application.multiTagLocateTagListExist) {
                if (!Application.mIsMultiTagLocatingRunning) {
                    String tagID = ((AutoCompleteTextView) mActivity.findViewById(R.id.multiTagLocate_epc)).getText().toString();
                    if (RFIDController.asciiMode) {
                        tagID = asciitohex.convert(tagID).toUpperCase();
                    }
                    if (!tagID.isEmpty()) {
                        if (Application.multiTagLocateTagListMap.containsKey(tagID)) {
                            try {
                                if (RFIDController.mConnectedReader.Actions.MultiTagLocate.deleteItem(tagID) == 0) {
                                    if (Application.multiTagLocateActiveTagItemList.size() == 1) {
                                        Application.multiTagLocatelastTag = true;
                                    }
                                    Application.multiTagLocateActiveTagItemList.remove(Application.multiTagLocateTagListMap.get(tagID));
                                    ((LocateOperationsFragment) mActivity.getCurrentFragment(RFID_TAB)).handleLocateTagResponse();
                                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_delete_item_success), Toast.LENGTH_SHORT).show();
                                    if (Application.multiTagLocateActiveTagItemList.size() == 0) {
                                        Application.multiTagLocateTagListExist = false;
                                    }
                                } else
                                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_delete_item_failed), Toast.LENGTH_SHORT).show();
                            } catch (InvalidUsageException e) {
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getInfo());
                            } catch (OperationFailureException e) {
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getVendorMessage());
                            }
                        } else
                            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_delete_item_failed), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_operation_running), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_no_data_loaded), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        //} else
        //    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    public void multiTagLocateClearTagItems(View v) {
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
            if (Application.multiTagLocateTagListExist) {
                if (!Application.mIsMultiTagLocatingRunning) {

                    try {
                        int check = RFIDController.mConnectedReader.Actions.MultiTagLocate.clearItems();
                        if (check == 0) {
                            Application.multiTagLocateActiveTagItemList.clear();
                            ((LocateOperationsFragment) mActivity.getCurrentFragment(RFID_TAB)).handleLocateTagResponse();
                            if (Application.multiTagLocateActiveTagItemList.size() == 0) {
                                Application.multiTagLocateTagListExist = false;
                            }

                        }
                    } catch (InvalidUsageException e) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getInfo());
                    } catch (OperationFailureException e) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getVendorMessage());
                    }
                } else
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_operation_running), Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_no_data_loaded), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
    }

    public void multiTagLocateReset(View v) {
        //if (isBluetoothEnabled() || !RFIDController.mConnectedReader.getHostName().startsWith("RFD8500")) {
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
            if (Application.multiTagLocateTagListExist || Application.multiTagLocatelastTag) {
                if (!Application.mIsMultiTagLocatingRunning) {
                    Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
                    if (fragment instanceof LocateOperationsFragment) {
                        ((LocateOperationsFragment) fragment).resetMultiTagLocateDetail(false);
                    }
                } else
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_operation_running), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.multiTag_locate_error_no_data_loaded), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();
        //} else
        //    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_bluetooth_disabled), Toast.LENGTH_SHORT).show();
    }

    private void PrepareMatchModeList() {
        Log.d(TAG, "PrepareMatchModeList");
        if (Application.TAG_LIST_MATCH_MODE && !Application.TAG_LIST_LOADED) {
            //This for loop will reset all the items in the tagsListCSV(making Tag count to zero)
            for (int i = 0; i < Application.tagsListCSV.size(); i++) {
                InventoryListItem inv = null;
                if (Application.tagsListCSV.get(i).getCount() != 0) {
                    inv = Application.tagsListCSV.remove(i);
                    InventoryListItem inventoryListItem = new InventoryListItem(inv.getTagID(), 0, null, null, null, null, null, null);
                    inventoryListItem.setTagDetails(inv.getTagDetails());
                    Application.tagsListCSV.add(i, inventoryListItem);
                } else {
                    if (Application.tagsListCSV.get(i).isVisible()) {
                        Application.tagsListCSV.get(i).setVisible(false);
                    }
                }
            }
            Application.UNIQUE_TAGS_CSV = Application.tagsListCSV.size();
            Application.tagsReadInventory.addAll(Application.tagsListCSV);
            Application.inventoryList.putAll(Application.tagListMap);
            Application.missedTags = Application.tagsListCSV.size();
            Application.matchingTags = 0;
            Application.TAG_LIST_LOADED = true;
            Log.d(TAG, "PrepareMatchModeList done");
        }
    }

    /**
     * Method called when read button in AccessOperationsFragment is clicked
     *
     * @param v - Read Button
     */
    public void accessOperationsReadClicked(View v) {
        AutoCompleteTextView tagIDField = mActivity.findViewById(R.id.accessRWTagID);
        final String tagId = (RFIDController.asciiMode == true ? asciitohex.convert(tagIDField.getText().toString()) : tagIDField.getText().toString());
        String offsetText = ((EditText) mActivity.findViewById(R.id.accessRWOffsetValue)).getText().toString();
        String lengthText = ((EditText) mActivity.findViewById(R.id.accessRWLengthValue)).getText().toString();
        final TextView accessRWData = mActivity.findViewById(R.id.accessRWData);
        String accessRWPassword = ((EditText) mActivity.findViewById(R.id.accessRWPassword)).getText().toString();
        String bankItem = ((Spinner) mActivity.findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString();
        progressDialog = new CustomProgressDialog(mActivity, MSG_READ);
        progressDialog.show();
        //final Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (accessRWData != null) {
            accessRWData.setText("");
        }
        timerDelayRemoveDialog(Constants.RESPONSE_TIMEOUT, progressDialog, "Read");

        String tagValue;
        if (RFIDController.asciiMode == true) tagValue = asciitohex.convert(tagId);
        else tagValue = tagId;


        if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
            Toast.makeText(mActivity.getApplicationContext(), "활성 연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!RFIDController.mConnectedReader.isCapabilitiesReceived()) {
            Toast.makeText(mActivity.getApplicationContext(), "기능이 업데이트되지 않았습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (tagValue.isEmpty()) {
            Toast.makeText(mActivity.getApplicationContext(), "태그 ID를 입력하세요.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (offsetText.isEmpty()) {
            Toast.makeText(mActivity.getApplicationContext(), "offset을 채워주세요.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (lengthText.isEmpty()) {
            Toast.makeText(mActivity.getApplicationContext(), "length를 채워주세요.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
            RFIDController.getInstance().accessOperationsRead(tagId, offsetText, lengthText, accessRWPassword, bankItem, new RfidListeners() {
                @Override
                public void onSuccess(Object object) {
                    progressDialog.dismiss();
                    if (RFIDController.isAccessCriteriaRead && !RFIDController.mIsInventoryRunning) {
                        if (fragment instanceof AccessOperationsFragment)
                            ((AccessOperationsFragment) fragment).handleTagResponse((TagData) object);
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                    progressDialog.dismiss();
                    if (exception instanceof InvalidUsageException) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((InvalidUsageException) exception).getInfo());
                    } else if (exception instanceof OperationFailureException) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((OperationFailureException) exception).getVendorMessage());
                    } else {
                        Toast.makeText(mActivity.getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String message) {
                    progressDialog.dismiss();
                    Toast.makeText(mActivity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * Method called when write button in AccessOperationsFragment is clicked
     *
     * @param v - Write Button
     */
    public void accessOperationsWriteClicked(View v) {
        AutoCompleteTextView tagIDField = mActivity.findViewById(R.id.accessRWTagID);
        final String tagId = (RFIDController.asciiMode == true ? asciitohex.convert(tagIDField.getText().toString()) : tagIDField.getText().toString());
        String offsetText = ((EditText) mActivity.findViewById(R.id.accessRWOffsetValue)).getText().toString();
        String lengthText = ((EditText) mActivity.findViewById(R.id.accessRWLengthValue)).getText().toString();
        final String accessRWData = ((EditText) mActivity.findViewById(R.id.accessRWData)).getText().toString();
        String accessRWpassword = ((EditText) mActivity.findViewById(R.id.accessRWPassword)).getText().toString();
        String bankItem = ((Spinner) mActivity.findViewById(R.id.accessRWMemoryBank)).getSelectedItem().toString();
        progressDialog = new CustomProgressDialog(mActivity, MSG_WRITE);
        progressDialog.show();
        timerDelayRemoveDialog(Constants.RESPONSE_TIMEOUT, progressDialog, "Write");
        String tagValue;
        if (RFIDController.asciiMode == true) tagValue = asciitohex.convert(tagId);
        else tagValue = tagId;
        if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
            Toast.makeText(mActivity.getApplicationContext(), "활성 연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!RFIDController.mConnectedReader.isCapabilitiesReceived()) {
            Toast.makeText(mActivity.getApplicationContext(), "장치 기능이 업데이트되지 않았습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (tagValue.isEmpty()) {
            Toast.makeText(mActivity.getApplicationContext(), "태그ID를 채워주세요.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (offsetText.isEmpty()) {
            Toast.makeText(mActivity.getApplicationContext(), "offset을 채워주세요.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (lengthText.isEmpty()) {
            Toast.makeText(mActivity.getApplicationContext(), "length를 채워주세요.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else
            RFIDController.getInstance().accessOperationsWrite(tagValue, offsetText, lengthText, accessRWData, accessRWpassword, bankItem, new RfidListeners() {
                @Override
                public void onSuccess(Object object) {
                    progressDialog.dismiss();
                    startbeepingTimer();
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.msg_write_succeed), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    progressDialog.dismiss();
                    if (exception instanceof InvalidUsageException) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((InvalidUsageException) exception).getInfo());
                    } else if (exception instanceof OperationFailureException) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((OperationFailureException) exception).getResults().toString());
                    }

                }

                @Override
                public void onFailure(String message) {
                    progressDialog.dismiss();
                    Toast.makeText(mActivity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

    }

    /**
     * Method called when lock button in AccessOperationsFragment is clicked
     *
     * @param v - Lock button
     */
    public void accessOperationLockClicked(View v) {
        AutoCompleteTextView tagIDField = mActivity.findViewById(R.id.accessLockTagID);
        final String tagId = (RFIDController.asciiMode == true ? asciitohex.convert(tagIDField.getText().toString()) : tagIDField.getText().toString());
        String accessRWpassword = ((EditText) mActivity.findViewById(R.id.accessLockPassword)).getText().toString();
        progressDialog = new CustomProgressDialog(mActivity, MSG_LOCK);
        progressDialog.show();
        timerDelayRemoveDialog(Constants.RESPONSE_TIMEOUT, progressDialog, "Lock");
        LOCK_DATA_FIELD lockDataField = null;
        LOCK_PRIVILEGE lockPrivilege = null;
        boolean ALL_Memory_Bank = false;
        //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (fragment != null && fragment instanceof AccessOperationsFragment) {
            Fragment innerFragment = ((AccessOperationsFragment) fragment).getCurrentlyViewingFragment();
            if (innerFragment != null && innerFragment instanceof AccessOperationsLockFragment) {
                AccessOperationsLockFragment lockFragment = ((AccessOperationsLockFragment) innerFragment);
                String lockMemoryBank = lockFragment.getLockMemoryBank();
                if (lockMemoryBank != null && !lockMemoryBank.isEmpty()) {
                    if (lockMemoryBank.equalsIgnoreCase("epc"))
                        lockDataField = LOCK_DATA_FIELD.LOCK_EPC_MEMORY;
                    else if (lockMemoryBank.equalsIgnoreCase("tid"))
                        lockDataField = LOCK_DATA_FIELD.LOCK_TID_MEMORY;
                    else if (lockMemoryBank.equalsIgnoreCase("user"))
                        lockDataField = LOCK_DATA_FIELD.LOCK_USER_MEMORY;
                    else if (lockMemoryBank.equalsIgnoreCase("access pwd"))
                        lockDataField = LOCK_DATA_FIELD.LOCK_ACCESS_PASSWORD;
                    else if (lockMemoryBank.equalsIgnoreCase("kill pwd"))
                        lockDataField = LOCK_DATA_FIELD.LOCK_KILL_PASSWORD;
                    else if (lockMemoryBank.equalsIgnoreCase("all")) ALL_Memory_Bank = true;
                    lockPrivilege = lockFragment.getLockAccessPermission();

                }
            }
        }
        String tagValue;
        if (RFIDController.asciiMode == true) tagValue = asciitohex.convert(tagId);
        else tagValue = tagId;
        if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
            Toast.makeText(mActivity.getApplicationContext(), "활성 연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!RFIDController.mConnectedReader.isCapabilitiesReceived()) {
            Toast.makeText(mActivity.getApplicationContext(), "장치 기능이 업데이트되지 않았습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (tagValue.isEmpty()) {
            Toast.makeText(mActivity.getApplicationContext(), "태그ID를 채워주세요.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else
            RFIDController.getInstance().accessOperationLock(tagValue, accessRWpassword, lockDataField, lockPrivilege, ALL_Memory_Bank, new RfidListeners() {
                @Override
                public void onSuccess(Object object) {
                    progressDialog.dismiss();
                    startbeepingTimer();
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.msg_lock_succeed), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    progressDialog.dismiss();
                    if (exception instanceof InvalidUsageException) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((InvalidUsageException) exception).getInfo());
                    } else if (exception instanceof OperationFailureException) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((OperationFailureException) exception).getVendorMessage());
                    }

                }

                @Override
                public void onFailure(String message) {
                    progressDialog.dismiss();
                    Toast.makeText(mActivity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

    }

    /**
     * Method called when kill button in AccessOperationsFragment is clicked
     *
     * @param v - Kill button
     */
    public void accessOperationsKillClicked(View v) {
        AutoCompleteTextView tagIDField = mActivity.findViewById(R.id.accessKillTagID);
        final String tagId = (RFIDController.asciiMode == true ? asciitohex.convert(tagIDField.getText().toString()) : tagIDField.getText().toString());
        String accessRWpassword = ((EditText) mActivity.findViewById(R.id.accessKillPassword)).getText().toString();
        //final Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        progressDialog = new CustomProgressDialog(mActivity, MSG_KILL);
        progressDialog.show();
        timerDelayRemoveDialog(Constants.RESPONSE_TIMEOUT, progressDialog, "Kill");
        String tagValue;
        if (RFIDController.asciiMode == true) tagValue = asciitohex.convert(tagId);
        else tagValue = tagId;
        if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
            Toast.makeText(mActivity.getApplicationContext(), "활성 연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (!RFIDController.mConnectedReader.isCapabilitiesReceived()) {
            Toast.makeText(mActivity.getApplicationContext(), "장치 기능이 업데이트되지 않았습니다.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (tagValue.isEmpty()) {
            Toast.makeText(mActivity.getApplicationContext(), "태그Id를 채워주세요.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else
            RFIDController.getInstance().accessOperationsKill(tagValue, accessRWpassword, new RfidListeners() {
                @Override
                public void onSuccess(Object object) {
                    progressDialog.dismiss();
                    startbeepingTimer();
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.msg_kill_succeed), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception exception) {
                    progressDialog.dismiss();
                    if (exception instanceof InvalidUsageException) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((InvalidUsageException) exception).getInfo());
                    } else if (exception instanceof OperationFailureException) {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((OperationFailureException) exception).getVendorMessage());
                    }
                }

                @Override
                public void onFailure(String message) {
                    progressDialog.dismiss();
                    Toast.makeText(mActivity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     * Method called when stop in locationing is clicked
     *
     * @param v - Locationing stop clicked
     */
    public synchronized void locationingButtonClicked(final View v) {

        if (Application.mIsMultiTagLocatingRunning == true) {
            Toast.makeText(mActivity.getApplicationContext(), "작업 진행 중 - 명령이 허용되지 않음", Toast.LENGTH_SHORT).show();
            return;
        }
        FloatingActionButton btn_locate = mActivity.findViewById(R.id.btn_locate);
        EditText lt_et_epc = (AutoCompleteTextView) mActivity.findViewById(R.id.lt_et_epc);
        String locateTag = lt_et_epc.getText().toString();

        if (locateTag != null && !RFIDController.isLocatingTag && !locateTag.isEmpty()) {

            lt_et_epc.setFocusable(false);
            if (btn_locate != null) {
                btn_locate.setImageResource(R.drawable.ic_play_stop);
            }
            RangeGraph locationBar = mActivity.findViewById(R.id.locationBar);
            locationBar.setValue(0);
            locationBar.invalidate();
            locationBar.requestLayout();
        } else {
            RFIDController.isLocationingAborted = true;
            if (btn_locate != null) {
                btn_locate.setImageResource(android.R.drawable.ic_media_play);
            }
            (mActivity.findViewById(R.id.lt_et_epc)).setFocusableInTouchMode(true);
            (mActivity.findViewById(R.id.lt_et_epc)).setFocusable(true);
        }
        RFIDController.getInstance().locationing(locateTag, new RfidListeners() {
            @Override
            public void onSuccess(Object object) {
                //  progressDialog.dismiss();
            }

            @Override
            public void onFailure(Exception exception) {
                //  progressDialog.dismiss();
                //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
                Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
                if (exception instanceof InvalidUsageException) {
                    if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                        ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(RFIDResults.RFID_API_PARAM_ERROR);
                    sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((InvalidUsageException) exception).getInfo());
                } else if (exception instanceof OperationFailureException) {
                    if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                        ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(((OperationFailureException) exception).getResults());
                    sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, ((OperationFailureException) exception).getVendorMessage());
                }
            }

            @Override
            public void onFailure(String message) {
                //  progressDialog.dismiss();
                Toast.makeText(mActivity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if (btn_locate != null) {
                    btn_locate.setImageResource(android.R.drawable.ic_media_play);
                }
                //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
                Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
                if (fragment instanceof ResponseHandlerInterfaces.ResponseStatusHandler)
                    ((ResponseHandlerInterfaces.ResponseStatusHandler) fragment).handleStatusResponse(RFIDResults.RFID_API_UNKNOWN_ERROR);
            }
        });
    }

    /**
     * Method to change operation status and ui in app on recieving abort status
     */
    private void operationHasAborted() {
        //final Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        ConnectionController.operationHasAborted(new RfidListeners() {
            @Override
            public void onSuccess(Object object) {
                if (RFIDController.mIsInventoryRunning) {
                    if (RFIDController.isInventoryAborted) {
                        RFIDController.mIsInventoryRunning = false;
                        RFIDController.isInventoryAborted = true; //false
                        RFIDController.isTriggerRepeat = null;
                        if (InventoryTimer.getInstance().isTimerRunning())
                            InventoryTimer.getInstance().stopTimer();
                        if (fragment instanceof RFIDInventoryFragment)
                            ((RFIDInventoryFragment) fragment).resetInventoryDetail();
                        else if (fragment instanceof RapidReadFragment)
                            ((RapidReadFragment) fragment).resetInventoryDetail();
                        //export Data to the file
                        if (RFIDController.EXPORT_DATA)
                            if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty()) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                    createFileInterface.createFile1(uri);
                                    //  exportData();
                                } else {
                                    checkForExportPermission(REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            }
                    }
                } else if (RFIDController.isLocatingTag) {
                    if (RFIDController.isLocationingAborted) {
                        RFIDController.isLocatingTag = false;
                        RFIDController.isLocationingAborted = false;
                        if (fragment instanceof LocateOperationsFragment)
                            ((LocateOperationsFragment) fragment).resetLocationingDetails(false);

                    }
                } else if (Application.mIsMultiTagLocatingRunning) {
                    if (isMultiTagLocationingAborted) {
                        Application.mIsMultiTagLocatingRunning = false;
                        isMultiTagLocationingAborted = false;
                    }
                }
            }

            @Override
            public void onFailure(Exception exception) {
            }

            @Override
            public void onFailure(String message) {
            }
        });


    }

    public void exportData(Uri uri) {
        if (RFIDController.mConnectedReader != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            new DataExportTask(mActivity.getApplicationContext(), Application.tagsReadInventory, RFIDController.mConnectedReader.getHostName(), Application.TOTAL_TAGS, Application.UNIQUE_TAGS, RFIDController.mRRStartedTime, uri).execute();

        }
    }


    public void LoadTagListCSV() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            MatchModeFileLoader.getInstance(mContext).LoadMatchModeCSV();
        } else {
            checkForExportPermission(REQUEST_CODE_ASK_PERMISSIONS_CSV);
        }
    }

    void checkForExportPermission(final int code) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // No explanation needed, we can request the permission.

                showMessageOKCancel("인벤토리를 내보내려면 외부 저장소에 쓰기 권한이 필요합니다.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
                    }
                });

            } else {
                switch (code) {
                    case REQUEST_CODE_ASK_PERMISSIONS:
                        createFileInterface.createFile1(uri);
                        //  exportData(uri);
                        break;
                    case REQUEST_CODE_ASK_PERMISSIONS_CSV:
                        MatchModeFileLoader.getInstance(mActivity.getApplicationContext()).LoadMatchModeCSV();
                        break;
                }
            }
        } else {

            switch (code) {
                case REQUEST_CODE_ASK_PERMISSIONS:
                    createFileInterface.createFile1(uri);
                    // exportData(uri);
                    break;
                case REQUEST_CODE_ASK_PERMISSIONS_CSV:
                    MatchModeFileLoader.getInstance(mActivity.getApplicationContext()).LoadMatchModeCSV();
                    break;
            }

        }

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createFileInterface.createFile1(uri);
                // exportData();
            }
        } else if (requestCode == REQUEST_CODE_ASK_PERMISSIONS_CSV) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MatchModeFileLoader.getInstance(mActivity.getApplicationContext()).LoadMatchModeCSV();
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity).setMessage(message).setPositiveButton("확인", okListener).setNegativeButton("취소", null).create().show();
    }

    /**
     * method to set DPO status on Action bar
     *
     * @param level
     */
    public void setActionBarBatteryStatus(final int level) {
        ivBatteryLevel.setImageLevel(level);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = mActivity.getMenuInflater();
        inflater.inflate(R.menu.common_menu, menu);
        this.menu = menu;
        if (RFIDController.BatteryData != null)
            setActionBarBatteryStatus(RFIDController.BatteryData.getLevel());
        else{
            setActionBarBatteryStatus(0);
        }
        return true;
    }
    /**
     * method lear inventory data like total tags, unique tags, read rate etc..
     */
    /**
     * RR button in {@link RapidReadFragment} is clicked
     *
     * @param view - Button clicked
     */
    public void rrClicked(View view) {
        selectNavigationMenuItem(0);
        mActivity.loadNextFragment(RAPID_READ_TAB);
        //selectItem(1);
    }

    /**
     * Inventory button in {@link RFIDInventoryFragment} is clicked
     *
     * @param view - Button clicked
     */
    public void invClicked(View view) {
        selectNavigationMenuItem(1);
        //selectItem(2);
        mActivity.loadNextFragment(INVENTORY_TAB);
    }

    /**
     * Locationing button in {@link LocateOperationsFragment} is clicked
     *
     * @param view - Button clicked
     */
    public void locateClicked(View view) {
        selectNavigationMenuItem(2);
        //selectItem(3);
        mActivity.loadNextFragment(LOCATE_TAG_TAB);
    }

    /**
     * Settings button in {@link SettingListFragment} is clicked
     *
     * @param view - Button clicked
     */
    public void settClicked(View view) {
        selectNavigationMenuItem(5);
        //selectItem(4);
        mActivity.loadNextFragment(RFID_SETTINGS_TAB);

    }

    /**
     * Access button in {@link AccessOperationsFragment} is clicked
     *
     * @param view - Button clicked
     */
    public void accessClicked(View view) {
        selectNavigationMenuItem(3);
        mActivity.loadNextFragment(RFID_ACCESS_TAB);
    }

    /**
     * Filter button in {@link PreFilterFragment} is clicked
     *
     * @param view - Button clicked
     */
    public void filterClicked(View view) {
        selectNavigationMenuItem(6);
        mActivity.loadNextFragment(RFID_PREFILTERS_TAB);
    }

    /**
     * About option in {@link AboutFragment} is selected
     */
    public void aboutClicked() {
        selectNavigationMenuItem(7);
        mActivity.loadNextFragment(RFID_ABOUT_TAB);
    }

    private void readerReconnected(ReaderDevice readerDevice) {
        // store app reader
        RFIDController.mConnectedDevice = readerDevice;
        RFIDController.mConnectedReader = readerDevice.getRFIDReader();
        if (RFIDController.isBatchModeInventoryRunning != null && RFIDController.isBatchModeInventoryRunning) {

            RFIDController.getInstance().clearInventoryData();
            RFIDController.mIsInventoryRunning = true;
            Application.memoryBankId = 0;
            RFIDController.getInstance().startTimer();
            //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
            Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
            if (fragment instanceof ResponseHandlerInterfaces.BatchModeEventHandler) {
                ((ResponseHandlerInterfaces.BatchModeEventHandler) fragment).batchModeEventReceived();
                //update battery status as well here
                if (RFIDController.BatteryData != null) {
                    setActionBarBatteryStatus(RFIDController.BatteryData.getLevel());
                }
                else{
                    setActionBarBatteryStatus(0);
                }
            }
        } else try {
            RFIDController.getInstance().updateReaderConnection(false);
        } catch (InvalidUsageException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        } catch (OperationFailureException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        }
        ReaderDeviceConnected(readerDevice);
    }

    /**
     * Method to notify device disconnection
     *
     * @param readerDevice
     */
    private void readerDisconnected(ReaderDevice readerDevice) {
        RFIDController.getInstance().stopTimer();
        //updateConnectedDeviceDetails(readerDevice, false);
        if (RFIDController.NOTIFY_READER_CONNECTION)
            sendNotification(Constants.ACTION_READER_DISCONNECTED, "Disconnected from " + readerDevice.getName());

        if (RFIDController.readersList.contains(readerDevice) == false)
            RFIDController.readersList.add(readerDevice);


        ReaderDeviceDisConnected(readerDevice);
        setActionBarBatteryStatus(0);

        RFIDController.mConnectedDevice = null;
        RFIDController.mConnectedReader = null;
        RFIDController.is_disconnection_requested = false;
        RFIDController.getInstance().clearSettings();

    }

    public void inventoryAborted() {
        InventoryTimer.getInstance().stopTimer();
        RFIDController.mIsInventoryRunning = false;
    }

    public void ReaderDeviceConnected(ReaderDevice device) {

        if (!Application.isReaderConnectedThroughBluetooth || BluetoothHandler.isDevicePaired(device.getName())) {
            //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
            Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
            if (fragment instanceof RFIDReadersListFragment) {
                ((RFIDReadersListFragment) fragment).ReaderDeviceConnected(device);
            } else if (fragment instanceof AboutFragment) {
                ((AboutFragment) fragment).deviceConnected();
            } else if (fragment instanceof AdvancedOptionItemFragment) {
                ((AdvancedOptionItemFragment) fragment).settingsListUpdated();
            }
//        else if(fragment instanceof AccessOperationsFragment)
//            ((AccessOperationsFragment) fragment).deviceConnected(device);
            if (readerDeviceFoundHandlers != null && readerDeviceFoundHandlers.size() > 0) {
                for (ReaderDeviceFoundHandler readerDeviceFoundHandler : readerDeviceFoundHandlers)
                    readerDeviceFoundHandler.ReaderDeviceConnected(device);
            }
            if (RFIDController.NOTIFY_READER_CONNECTION)
                sendNotification(Constants.ACTION_READER_CONNECTED, "Connected to " + device.getName());
        } else {

            try {
                RFIDController.mConnectedReader.disconnect();
            } catch (InvalidUsageException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            } catch (OperationFailureException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }

            clearConnectedReader();

        }
    }

    public void ReaderDeviceDisConnected(ReaderDevice device) {

        if ((UpdateFirmware.isWaitingForFWUpdateToComplete == true) || (Application.isFirmwareUpdateInProgress == true)) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        if (RFIDController.mIsInventoryRunning) {
            inventoryAborted();
            //export Data to the file if inventory is running in batch mode
            if (RFIDController.isBatchModeInventoryRunning != null && !RFIDController.isBatchModeInventoryRunning)
                if (RFIDController.EXPORT_DATA) {
                    if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty()) {
                        createFileInterface.createFile1(uri);
                        //  new DataExportTask(mActivity.getApplicationContext(), tagsReadInventory, device.getName(), TOTAL_TAGS, UNIQUE_TAGS, mRRStartedTime).execute();
                    }
                }
            RFIDController.isBatchModeInventoryRunning = false;
        }
        if (RFIDController.isLocatingTag) {
            RFIDController.isLocatingTag = false;
        }
        //update dpo icon in settings list
        AdvancedOptionsContent.ITEMS.get(AdvancedOptionsContent.DPO_ITEM_INDEX).icon = R.drawable.title_dpo_disabled;
        RFIDController.isAccessCriteriaRead = false;
        accessTagCount = 0;
        //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(READERS_TAB);
        if (fragment instanceof RFIDReadersListFragment) {
            ((RFIDReadersListFragment) fragment).readerDisconnected(device, false);
            ((RFIDReadersListFragment) fragment).ReaderDeviceDisConnected(device);
        } else if (fragment instanceof LocateOperationsFragment) {
            ((LocateOperationsFragment) fragment).resetLocationingDetails(true);
        } else if (fragment instanceof AboutFragment) {
            ((AboutFragment) fragment).resetVersionDetail();
        } else if (fragment instanceof AdvancedOptionItemFragment) {
            ((AdvancedOptionItemFragment) fragment).settingsListUpdated();
        }
        fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (fragment instanceof RFIDInventoryFragment) {
            ((RFIDInventoryFragment) fragment).resetInventoryDetail();
        } else if (fragment instanceof RapidReadFragment) {
            ((RapidReadFragment) fragment).resetInventoryDetail();
        }


        if (readerDeviceFoundHandlers != null && readerDeviceFoundHandlers.size() > 0) {
            for (ReaderDeviceFoundHandler readerDeviceFoundHandler : readerDeviceFoundHandlers)
                readerDeviceFoundHandler.ReaderDeviceDisConnected(device);
        }
        if (RFIDController.mConnectedReader != null /*&& !AUTO_RECONNECT_READERS*/) {
            try {
                mActivity.disconnect(Application.currentScannerId);
                Application.sdkHandler.dcssdkTerminateCommunicationSession(Application.currentScannerId);
                RFIDController.mConnectedReader.disconnect();

            } catch (InvalidUsageException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            } catch (OperationFailureException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            try {
                RFIDController.mConnectedReader.Dispose();
            } catch (Exception e) {
                //if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
            }
            RFIDController.mConnectedReader = null;
        }
    }

    @Override
    public void RFIDReaderAppeared(ReaderDevice device) {
        mActivity.runOnUiThread(() -> {
            Log.d(TAG, "RFIDBase RFIDReaderAppeared " + device.getName());
            if (RFIDController.readersList.contains(device) == false) {
                //Already in the list
                //do a auto connect
                //return;
                RFIDController.readersList.add(device);
            }


            Log.d(TAG, "RFIDBase RFIDReaderAppeared new device" + device.getName());
            mActivity.reInit();

            if (UpdateFirmware.isWaitingForFWUpdateToComplete == true) {

                Fragment fragment = mActivity.getCurrentFragment(SETTINGS_TAB);
                if (fragment instanceof UpdateFirmware) {
                    ((UpdateFirmware) fragment).RFIDReaderAppeared(device);
                }
                // if (readersList.contains(device) == false)
                //    readersList.add(device);
                // return;
                mActivity.setCurrentTabFocus(READERS_TAB);
            }

            // if (readersList.contains(device) == false)
            //     readersList.add(device);

            if (RFIDController.mConnectedDevice != null) {
                if (RFIDController.mConnectedDevice.getName().equals(device.getName()) == false) {
                    if (RFIDController.mConnectedReader != null)
                        RFIDController.mConnectedReader.reinitTransport();
                }
            }

            Fragment fragment = mActivity.getCurrentFragment(SETTINGS_TAB);
            if (fragment instanceof FactoryResetFragment) {
                ((FactoryResetFragment) fragment).RFIDReaderAppeared(device);
                mActivity.setCurrentTabFocus(READERS_TAB);
            }

            fragment = mActivity.getCurrentFragment(READERS_TAB);
            if (fragment instanceof RFIDReadersListFragment) {
                ((RFIDReadersListFragment) fragment).RFIDReaderAppeared(device);
            } else {//  if (fragment != null)

                if (fragment instanceof PairOperationsFragment) {
                    ((PairOperationsFragment) fragment).RFIDReaderAppeared(device);

                }
                mActivity.loadNextFragment(READER_LIST_TAB);
                //
                if (RFIDController.AUTO_RECONNECT_READERS && RFIDController.LAST_CONNECTED_READER != null && RFIDController.LAST_CONNECTED_READER.length() != 0 && (RFIDController.mConnectedDevice == null || !RFIDController.mConnectedDevice.getRFIDReader().isConnected())) {

                    mActivity.setCurrentTabFocus(READERS_TAB);

                    //Fragment fragment = mActivity.getCurrentFragment(READERS_TAB);
                    //AutoConnectDevice();
                    //Application.isFirmwareUpdateInProgress = false;
                    RFIDController.mReaderDisappeared = null;
                    mActivity.setCurrentTabFocus(READERS_TAB);
                    if (fragment instanceof RFIDReadersListFragment == false) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                Fragment fragment = mActivity.getCurrentFragment(READERS_TAB);
                                if (fragment instanceof RFIDReadersListFragment) {
                                    ((RFIDReadersListFragment) fragment).RFIDReaderAppeared(device);
                                }
                                //       else  if (fragment instanceof PairOperationsFragment) {
                                //               ((PairOperationsFragment) fragment).RFIDReaderAppeared(device);
                                //       }
                            }
                        }, Application.AUTORECONNECT_DELAY);
                    }

                }
            }


            if (RFIDController.NOTIFY_READER_AVAILABLE) {
                if (!device.getName().equalsIgnoreCase("null"))
                    sendNotification(Constants.ACTION_READER_AVAILABLE, device.getName() + " is available.");
            }
            if (RFIDController.AUTO_RECONNECT_READERS && RFIDController.LAST_CONNECTED_READER != null && RFIDController.LAST_CONNECTED_READER.length() != 0 && (RFIDController.mConnectedDevice == null || !RFIDController.mConnectedDevice.getRFIDReader().isConnected())) {

                RFIDController.mReaderDisappeared = null;
            }
        });
    }


    public void setReaderstatuscallback(Readers.RFIDReaderEventHandler readerStatusCallback) {
        mReaderstatuscallback = readerStatusCallback;
    }

    public void resetReaderstatuscallback() {
        mReaderstatuscallback = null;
    }

    /*
     * On factory reset clean up
     */

    public void onFactoryReset(ReaderDevice device) {
        Log.d(TAG, "RFIDBase onFactoryReset");

        if (Application.scanPair == null) {
            Application.scanPair = new ScanPair();
        }

        ScanAndPairFragment scanAndPairFragment;
        scanAndPairFragment = ScanAndPairFragment.newInstance();
        Application.scanPair.Init(mActivity, scanAndPairFragment);
        BluetoothHandler btConnection = new BluetoothHandler();
        btConnection.init(mActivity, Application.scanPair);
        btConnection.unpairReader(device.getName());

        if (RFIDController.readersList.contains(device)) RFIDController.readersList.remove(device);

    }

    private ReaderDevice getReaderDevice(ReaderDevice readerdevice) {

        try {
            for (ReaderDevice device : RFIDController.readersList) {
                if (device.getName().equals(readerdevice.getName()) && device.getSerialNumber().equals(readerdevice.getSerialNumber().substring(0, device.getSerialNumber().length()))) {
                    return device;
                }
            }
            //check also if its part of connected device
            if (RFIDController.mConnectedDevice != null && RFIDController.mConnectedDevice.getName().equals(readerdevice.getName()) && RFIDController.mConnectedDevice.getSerialNumber().equals(readerdevice.getSerialNumber().substring(0, RFIDController.mConnectedDevice.getSerialNumber().length()))) {
                return RFIDController.mConnectedDevice;
            }
        } catch (NullPointerException e) {
            return null;
        }
        return null;
    }

    @Override
    public void RFIDReaderDisappeared(ReaderDevice readerdevice) {
        mActivity.runOnUiThread(() -> {
            Fragment fragment = null;
            ReaderDevice device = getReaderDevice(readerdevice);
            if (device == null) {
                Log.d(TAG, "Dettached device not in the list ");
                if (RFIDController.readersList.contains(readerdevice)) {
                    Log.d(TAG, "Dettached device removed from the list ");
                    RFIDController.readersList.remove(readerdevice);
                    fragment = mActivity.getCurrentFragment(READERS_TAB);
                    if (fragment instanceof RFIDReadersListFragment) {
                        ((RFIDReadersListFragment) fragment).removePairedDeviceList(device);
                        ((RFIDReadersListFragment) fragment).loadUIData();
                    } else {

                    }
                }

                return;
            }

            Log.d(TAG, "RFIDBase RFIDReaderDisAppeared");
            DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            if (Application.isFirmwareUpdateInProgress == true) {

                fragment = mActivity.getCurrentFragment(SETTINGS_TAB);
                if (fragment instanceof UpdateFirmware) {
                    ((UpdateFirmware) fragment).RFIDReaderDisappeared(device);
                }
            }
            // update reader list
            fragment = mActivity.getCurrentFragment(READERS_TAB);
            if (fragment instanceof RFIDReadersListFragment) {
                ((RFIDReadersListFragment) fragment).removePairedDeviceList(device);
                ((RFIDReadersListFragment) fragment).loadUIData();
            }

            fragment = mActivity.getCurrentFragment(RFID_TAB);
            if (fragment instanceof LocateOperationsFragment) {
                ((LocateOperationsFragment) fragment).handleReaderDisapeared();
            }
            RFIDController.mReaderDisappeared = device;
            if ((RFIDController.mConnectedDevice != null) && RFIDController.mConnectedDevice.getAddress().equals(device.getAddress())) {
                if (RFIDController.mConnectedReader != null) {

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.disconnect(Application.currentConnectedScannerID);
                        }
                    });

                    try {
                        RFIDController.mConnectedReader.disconnect();
                        RFIDController.mConnectedReader.Dispose();
                        RFIDController.mConnectedReader = null;
                        readerDisconnected(device);
                    } catch (Exception e) {

                        RFIDController.mConnectedReader = null;
                        readerDisconnected(device);
                    }
                }
            } else {
                if ((RFIDController.mConnectedDevice != null) && (RFIDController.mConnectedDevice.getName().equals(device.getName()) == false))
                    return;
            }

            if (RFIDController.autoConnectDeviceTask != null) {
                RFIDController.autoConnectDeviceTask.cancel(true);
            }
            //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
            if (mReaderstatuscallback != null) {
                mReaderstatuscallback.RFIDReaderDisappeared(device);

            } else {
                fragment = mActivity.getCurrentFragment(READERS_TAB);
                if (fragment instanceof RFIDReadersListFragment) {
                    ((RFIDReadersListFragment) fragment).RFIDReaderDisappeared(device);
                } else if (fragment instanceof RFIDInventoryFragment) {
                    ((RFIDInventoryFragment) fragment).RFIDReaderDisappeared(device);
                } else if (fragment instanceof AccessOperationsFragment) {
                    ((AccessOperationsFragment) fragment).RFIDReaderDisappeared(device);
                } else if (fragment instanceof PairOperationsFragment) {
                    return;
                }
            }
            if (RFIDController.NOTIFY_READER_AVAILABLE)
                sendNotification(Constants.ACTION_READER_AVAILABLE, device.getName() + " is unavailable.");

            // readers.deattach(this);
            if (Application.isFirmwareUpdateInProgress == true) {
                if (mReaderstatuscallback != null) {
                    mReaderstatuscallback.RFIDReaderDisappeared(device);
                }
                return;
                //mActivity.loadNextFragment(mActivity.INVENTORY_TAB);
                //mActivity.finish();
            }

            fragment = mActivity.getCurrentFragment(SETTINGS_TAB);
            if (fragment instanceof FactoryResetFragment) {
                ((FactoryResetFragment) fragment).RFIDReaderDisappeared(device);

            }
            if (fragment instanceof RegulatorySettingsFragment) {
                ((RegulatorySettingsFragment) fragment).deviceDisconnected();
            }

            Toast.makeText(mActivity.getApplicationContext(), "장치가 사라졌습니다", Toast.LENGTH_LONG).show();

        });
    }


    /**
     * Method which will called once notification received from reader.
     * update the operation status in the application based on notification type
     *
     * @param rfidStatusEvents - notification received from reader
     */
    private void notificationFromGenericReader(RfidStatusEvents rfidStatusEvents) {
        final Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.DISCONNECTION_EVENT) {
            String readername = rfidStatusEvents.StatusEventData.DisconnectionEventData.m_DisconnectionEvent.getreadername();
            if ((RFIDController.mConnectedReader != null) && (readername.equals(RFIDController.mConnectedDevice.getName())))
                DisconnectTask = new UpdateDisconnectedStatusTask(RFIDController.mConnectedReader.getHostName()).execute();
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.INVENTORY_START_EVENT) {
            if (!RFIDController.isAccessCriteriaRead && !RFIDController.isLocatingTag && !Application.mIsMultiTagLocatingRunning) {
                RFIDController.mIsInventoryRunning = true;
                InventoryTimer.getInstance().startTimer();
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.INVENTORY_STOP_EVENT) {
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            //tagListMatchNotice = false;
            //TODO: revisit why to clear here
            //accessTagCount = 0;
            //RFIDController.isAccessCriteriaRead = false;
            RFIDController.getInstance().startTimer();
            if (RFIDController.mIsInventoryRunning || InventoryTimer.getInstance().isTimerRunning() == true) {
                InventoryTimer.getInstance().stopTimer();
            } else if (RFIDController.isGettingTags) {
                RFIDController.isGettingTags = false;
                if (RFIDController.mConnectedReader != null)
                    RFIDController.mConnectedReader.Actions.purgeTags();
                if (RFIDController.EXPORT_DATA) {
                    if (Application.TAG_LIST_MATCH_MODE) {
                        if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty() && fragment instanceof RFIDInventoryFragment) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createFileInterface.createFile1(uri);
                                    //  new DataExportTask(mActivity.getApplicationContext(), ((RFIDInventoryFragment) fragment).getAdapter().searchItemsList, mConnectedDevice.getName(), TOTAL_TAGS, UNIQUE_TAGS, mRRStartedTime).execute();
                                }
                            });
                        } else if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty() && fragment instanceof RapidReadFragment && Application.UNIQUE_TAGS != 0) {
                            RFIDController.currentFragment = "RapidReadFragment";
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createFileInterface.createFile1(uri);
                                    // new DataExportTask(mActivity.getApplicationContext(), tagsReadInventory, mConnectedDevice.getName(), TOTAL_TAGS, UNIQUE_TAGS, mRRStartedTime).execute();
                                }
                            });
                        }
                    } else if (Application.tagsReadInventory != null && !Application.tagsReadInventory.isEmpty()) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createFileInterface.createFile1(uri);
                                //new DataExportTask(mActivity.getApplicationContext(), tagsReadInventory, mConnectedDevice.getName(), TOTAL_TAGS, UNIQUE_TAGS, mRRStartedTime).execute();
                            }
                        });
                    }
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fragment instanceof RFIDReadersListFragment) {
                            //((ReadersListFragment) fragment).cancelProgressDialog();
                            if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.ReaderCapabilities.getModelName() != null) {
                                ((RFIDReadersListFragment) fragment).capabilitiesRecievedforDevice();
                            }
                        }
                    }
                });
            }
            if (!RFIDController.getInstance().getRepeatTriggers()) {
                if (RFIDController.mIsInventoryRunning) RFIDController.isInventoryAborted = true;
                else if (RFIDController.isLocatingTag) RFIDController.isLocationingAborted = true;
                operationHasAborted();
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.OPERATION_END_SUMMARY_EVENT) {
            if (fragment instanceof RapidReadFragment) mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((RapidReadFragment) fragment).updateInventoryDetails();
                }
            });
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT && isActivityVisible()) {

            Log.d(TAG, "notificationFromGenericReader " + fragment + " screen " + m_ScreenOn + "trigger type = " + rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldTriggerType().ordinal);

            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldTriggerType().ordinal == HANDHELD_TRIGGER_TYPE.HANDHELD_TRIGGER_DUAL.ordinal) {
                Log.d(TAG, "notificationFromGenericReader if Dual " + HANDHELD_TRIGGER_TYPE.HANDHELD_TRIGGER_DUAL.ordinal);
                //return;
            }
            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldTriggerType().ordinal != HANDHELD_TRIGGER_TYPE.HANDHELD_TRIGGER_RFID.ordinal) {
                return;
            }

            Log.d(TAG, "Notification Trigger Event " + rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent());
            Boolean triggerPressed = false;
            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED)
                triggerPressed = true;
            Log.d(TAG, "notificationFromGenericReader " + fragment + " screen " + m_ScreenOn + "trigger type = " + rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldTriggerType().ordinal);
            if (m_ScreenOn) {
                if (triggerPressed && isTriggerImmediateorRepeat(triggerPressed) && fragment instanceof TriggerEventHandler) {
                    ((TriggerEventHandler) fragment).triggerPressEventRecieved();
                } else if (!triggerPressed && isTriggerImmediateorRepeat(triggerPressed) && fragment instanceof TriggerEventHandler) {
                    ((TriggerEventHandler) fragment).triggerReleaseEventRecieved();
                    //tagListMatchNotice = false;
                }
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.BATTERY_EVENT) {
            final Events.BatteryData batteryData = rfidStatusEvents.StatusEventData.BatteryData;
            RFIDController.BatteryData = batteryData;
            setActionBarBatteryStatus(batteryData.getLevel());
            if (batteryNotificationHandlers != null && batteryNotificationHandlers.size() > 0) {
                for (BatteryNotificationHandler batteryNotificationHandler : batteryNotificationHandlers)
                    batteryNotificationHandler.deviceStatusReceived(batteryData.getLevel(), batteryData.getCharging(), batteryData.getCause());
            }
            if (RFIDController.NOTIFY_BATTERY_STATUS && batteryData.getCause() != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String cause = batteryData.getCause();
                        if (batteryData.getCause().trim().equalsIgnoreCase(Constants.MESSAGE_BATTERY_CRITICAL))
                            sendNotification(Constants.ACTION_READER_BATTERY_CRITICAL, mActivity.getString(R.string.battery_status__critical_message));
                        else if (batteryData.getCause().trim().equalsIgnoreCase(Constants.MESSAGE_BATTERY_LOW))
                            sendNotification(Constants.ACTION_READER_BATTERY_CRITICAL, mActivity.getString(R.string.battery_status_low_message));
                        else if (batteryData.getLevel() < Constants.BATTERY_CRITICAL) {
                            sendNotification(Constants.ACTION_READER_BATTERY_CRITICAL, mActivity.getString(R.string.battery_status_critical_message));
                        } else if (batteryData.getLevel() < Constants.BATTERY_LOW) {
                            sendNotification(Constants.ACTION_READER_BATTERY_LOW, mActivity.getString(R.string.battery__status_low_message));
                        }
                    }
                });
            }
        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.BATCH_MODE_EVENT) {
            Log.d(TAG, "notify-event - Notification_BatchModeEvent");
            RFIDController.mConnectedReader.Actions.getBatchedTags();
            InventoryTimer.getInstance().startTimer();
            RFIDController.isBatchModeInventoryRunning = true;


            //starting battery polling
            RFIDController.getInstance().startTimer();
            //RFIDController.getInstance().clearInventoryData();

            RFIDController.mIsInventoryRunning = true;
            Application.memoryBankId = 0;
            PrepareMatchModeList();
            //isTriggerRepeat = rfidStatusEvents.StatusEventData.BatchModeEventData.get_RepeatTrigger();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (fragment instanceof ResponseHandlerInterfaces.BatchModeEventHandler) {
                        ((ResponseHandlerInterfaces.BatchModeEventHandler) fragment).batchModeEventReceived();
                    }
                    if (fragment instanceof RFIDReadersListFragment) {
                        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.ReaderCapabilities.getModelName() == null) {
                            ((RFIDReadersListFragment) fragment).capabilitiesRecievedforDevice();
                        }
                    }
                }
            });


        } else if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.WPA_EVENT) {
            Log.d(TAG, "notificationFromGenericReader " + " WPA_EVENT " + "Type: " + rfidStatusEvents.StatusEventData.WPAEventData.getType() + " ssid :" + rfidStatusEvents.StatusEventData.WPAEventData.getssid());

            String scanStatus = rfidStatusEvents.StatusEventData.WPAEventData.getType();
            Fragment wififragment = mActivity.getCurrentFragment(READERS_TAB);
            if (wififragment instanceof ReaderWifiSettingsFragment)
                ((ReaderWifiSettingsFragment) wififragment).readWifiScanNotification(scanStatus);

        }
    }

    /*
     *method to check if both start and stop trigger is IMMEDIATE or repeat trigger
     */
    public Boolean isTriggerImmediateorRepeat(Boolean trigPress) {
        if (trigPress && RFIDController.settings_startTrigger.getTriggerType().toString().equalsIgnoreCase(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE.toString()) && (!RFIDController.settings_stopTrigger.getTriggerType().toString().equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_HANDHELD_WITH_TIMEOUT.toString()))) {
            return true;
        } else if (!trigPress && !RFIDController.settings_startTrigger.getTriggerType().toString().equalsIgnoreCase(START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD.toString()) && (RFIDController.settings_stopTrigger.getTriggerType().toString().equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE.toString()))) {
            return true;
        } else return false;
    }

    /**
     * method to send connect command request to reader
     * after connect button clicked on connect password pairTaskDailog
     *
     * @param password     - reader password
     * @param readerDevice
     */
    public void connectClicked(String password, ReaderDevice readerDevice) {
        //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (fragment instanceof RFIDReadersListFragment) {
            ((RFIDReadersListFragment) fragment).ConnectwithPassword(password, readerDevice);
        }
    }

    /**
     * method which will exe cute after cancel button clicked on connect pwd pairTaskDailog
     *
     * @param readerDevice
     */
    public void cancelClicked(ReaderDevice readerDevice) {
        //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (fragment instanceof RFIDReadersListFragment) {
            ((RFIDReadersListFragment) fragment).readerDisconnected(readerDevice, true);
        }
    }

    public void startbeepingTimer() {
        if (RFIDController.beeperVolume != BEEPER_VOLUME.QUIET_BEEP) {
            if (!beepON) {
                beepON = true;
                beep();
                if (tbeep == null) {
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            stopbeepingTimer();
                            beepON = false;
                        }
                    };
                    tbeep = new Timer();
                    tbeep.schedule(task, 10);
                }
            }
        }
    }

    /**
     * method to stop timer
     */
    public void stopbeepingTimer() {
        if (tbeep != null && RFIDController.toneGenerator != null) {
            RFIDController.toneGenerator.stopTone();
            tbeep.cancel();
            tbeep.purge();
        }
        tbeep = null;
    }

    public void beep() {
        if (RFIDController.toneGenerator != null) {
            int toneType = ToneGenerator.TONE_PROP_BEEP;
            RFIDController.toneGenerator.startTone(toneType);
        }
    }

    public void startlocatebeepingTimer(int proximity) {
        if (RFIDController.beeperVolume != BEEPER_VOLUME.QUIET_BEEP) {
            int POLLING_INTERVAL1 = BEEP_DELAY_TIME_MIN + (((BEEP_DELAY_TIME_MAX - BEEP_DELAY_TIME_MIN) * (100 - proximity)) / 100);
            if (!beepONLocate) {
                beepONLocate = true;
                beep();
                if (locatebeep == null) {
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            stoplocatebeepingTimer();
                            beepONLocate = false;
                        }
                    };
                    locatebeep = new Timer();
                    locatebeep.schedule(task, POLLING_INTERVAL1, 10);
                }
            }
        }
    }

    /**
     * method to stop timer locate beep
     */
    public void stoplocatebeepingTimer() {
        if (locatebeep != null && RFIDController.toneGenerator != null) {
            RFIDController.toneGenerator.stopTone();
            locatebeep.cancel();
            locatebeep.purge();
        }
        locatebeep = null;
    }

    public void setTriggerMode(ENUM_TRIGGER_MODE triggerMode) {


        Thread trigThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (RFIDController.mConnectedReader != null) {
                    try {
                        RFIDController.mConnectedReader.Config.setTriggerMode(triggerMode, true);
                    } catch (InvalidUsageException e) {
                        if (e != null && e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    } catch (OperationFailureException e) {
                        if (e != null && e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    }
                }
            }
        });
        trigThread.start();

    }

    public void RfidDisconnect() {

        if (RFIDController.mConnectedReader == null) return;

        if (RFIDController.mConnectedReader.isConnected()) {
            RFIDController.is_disconnection_requested = true;
            try {
                RFIDController.mConnectedReader.disconnect();
            } catch (InvalidUsageException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            } catch (OperationFailureException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            //
            ReaderDeviceDisConnected(RFIDController.mConnectedDevice);
        }
    }

    public void resetFactoryDefault() throws InvalidUsageException, OperationFailureException {


        if ((RFIDController.mConnectedReader != null) && (RFIDController.mConnectedReader.isConnected())) {
            RFIDController.mConnectedReader.Config.resetFactoryDefaults();
        }
    }


    public boolean deviceReset() throws InvalidUsageException, OperationFailureException {
        try {


            if ((RFIDController.mConnectedReader != null) && (RFIDController.mConnectedReader.isConnected())) {
                RFIDController.mConnectedReader.Actions.reset();
            }
        } catch (OperationFailureException e) {
            return true;
        }
        return true;
    }

    public void eventWifiScanNotify(RfidWifiScanEvents rfidwifiscanevents) {
        Events.WifiScanEventData data = rfidwifiscanevents.getWifiScanEventData();
        Log.d(TAG, "eventWifiScanNotify: " + " ssid: " + data.wifiscandata.getssid() + " macaddress:" + data.wifiscandata.getmacaddress() + " key:" + data.wifiscandata.getkey() + "\n");
        Fragment fragment = mActivity.getCurrentFragment(READERS_TAB);
        if (fragment instanceof ReaderWifiSettingsFragment) {
            ((ReaderWifiSettingsFragment) fragment).updateScanResult(data.wifiscandata);
        }
    }


    public class EventHandler implements RfidEventsListener {

        private ActiveDeviceActivity mActivity;

        public EventHandler(ActiveDeviceActivity mActivity) {
            this.mActivity = mActivity;
        }


        @Override
        public void eventReadNotify(RfidReadEvents e) {
            if(Application.useCowChronicleTagging){
                return;
            }

            if (RFIDController.mConnectedReader != null) {
                if (!RFIDController.mConnectedReader.Actions.MultiTagLocate.isMultiTagLocatePerforming()) {
                    final TagData[] myTags = RFIDController.mConnectedReader.Actions.getReadTags(100);
                    final int tagCount = (myTags != null ? myTags.length : -1);
                    Log.d(TAG, "RFIDBase eventReadNotify tagCount:"+tagCount);

                    if (myTags != null) {
                        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);

                        for (TagData tagData : myTags) {
                            if (tagData.isContainsLocationInfo()) {
                                RFIDController.TagProximityPercent = tagData.LocationInfo.getRelativeDistance();
                                if (RFIDController.TagProximityPercent > 0) {
                                    startlocatebeepingTimer(RFIDController.TagProximityPercent);
                                }
                                if (fragment instanceof LocateOperationsFragment)
                                    ((LocateOperationsFragment) fragment).handleLocateTagResponse();
                            } else {
                                if (RFIDController.isAccessCriteriaRead && !RFIDController.mIsInventoryRunning) {
                                    accessTagCount++;
                                } else {
                                    if (tagData.getOpStatus() == null || tagData.getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
                                        if (Application.TAG_LIST_MATCH_MODE)
                                            new MatchingTagsResponseHandlerTask(tagData, fragment).execute();
                                        else
                                            new ResponseHandlerTask(tagData, fragment).execute();
                                    }
                                }
                            }
                        }
                    }
                } else { ////multi-tag locationing results
                    final TagData[] myTags = RFIDController.mConnectedReader.Actions.getMultiTagLocateTagInfo(100);
                    if (myTags != null) {
                        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
                        for (TagData tagData : myTags) {
                            if (tagData.isContainsMultiTagLocateInfo()) {
                                new MultiTagLocateResponseHandlerTask(mActivity, tagData, fragment).execute();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
            Log.d(TAG, "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
            notificationFromGenericReader(rfidStatusEvents);
        }

    }

    /**
     * Async Task, which will handle tag data response from reader. This task is used to check whether tag is in inventory list or not.
     * If tag is not in the list then it will add the tag data to inventory list. If tag is there in inventory list then it will update the tag details in inventory list.
     */
    public class ResponseHandlerTask extends AsyncTask<Void, Void, Boolean> {
        private TagData tagData;
        private InventoryListItem inventoryItem;
        private InventoryListItem oldObject;
        private Fragment fragment;
        private String memoryBank;
        private String memoryBankData;
        private String mTagData;

        ResponseHandlerTask(TagData tagData, Fragment fragment) {
            this.tagData = tagData;
            this.fragment = fragment;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean added = false;
            try {
                //Tag is already present. Update the fields and increment the count
                mTagData = tagData.getTagID();
                if (tagData.getOpCode() != null) {
                    if (tagData.getOpCode().toString().equalsIgnoreCase("ACCESS_OPERATION_READ")) {
                        if (tagData.getOpStatus().toString().equalsIgnoreCase("ACCESS_SUCCESS")) {
                            memoryBank = tagData.getMemoryBank().toString();
                            memoryBankData = tagData.getMemoryBankData().toString();
                        } else {
                            return false;
                        }

                    }
                } else {
                    memoryBank = "none";
                    memoryBankData = mTagData;
                }

                if (Application.inventoryList.containsKey(mTagData)) {
                    inventoryItem = new InventoryListItem(mTagData, 1, null, null, null, null, null, null);
                    int index = Application.inventoryList.get(mTagData);
                    if (index >= 0) {
                        oldObject = Application.tagsReadInventory.get(index);
                        int tagSeenCount = 0;
                        if (Integer.toString(tagData.getTagSeenCount()) != null)
                            tagSeenCount = tagData.getTagSeenCount();
                        if (tagSeenCount != 0) {
                            Application.TOTAL_TAGS += tagSeenCount;
                            oldObject.incrementCountWithTagSeenCount(tagSeenCount);
                        } else {
                            Application.TOTAL_TAGS++;
                            oldObject.incrementCount();
                        }
                        if (oldObject.getMemoryBankData() != null && !oldObject.getMemoryBankData().equalsIgnoreCase(memoryBankData))
                            oldObject.setMemoryBankData(memoryBankData);
                        if (RFIDController.pc)
                            oldObject.setPC(Integer.toHexString(tagData.getPC()));
                        if (RFIDController.phase)
                            oldObject.setPhase(Integer.toString(tagData.getPhase()));
                        if (RFIDController.channelIndex)
                            oldObject.setChannelIndex(Integer.toString(tagData.getChannelIndex()));
                        if (RFIDController.rssi)
                            oldObject.setRSSI(Integer.toString(tagData.getPeakRSSI()));
                        if (RFIDController.brandidcheckenabled) {
                            if (tagData.getBrandIDStatus()) {
                                //oldObject.brandIDfound = true;
                                oldObject.setBrandIDStatus(true);
                                RFIDController.bFound = true;
                                //Log.i("MainActivity", "getBrandIDStatus" + oldObject.getBrandIDStatus());
                            } else {
                                oldObject.setBrandIDStatus(false);
                            }
                        }
                    }
                } else {
                    //Tag is encountered for the first time. Add it.
                    if (RFIDController.inventoryMode == 0 || (RFIDController.inventoryMode == 1 && Application.UNIQUE_TAGS <= Constants.UNIQUE_TAG_LIMIT)) {
                        int tagSeenCount = 0;
                        if (Integer.toString(tagData.getTagSeenCount()) != null)
                            tagSeenCount = tagData.getTagSeenCount();
                        if (tagSeenCount != 0) {
                            Application.TOTAL_TAGS += tagSeenCount;
                            inventoryItem = new InventoryListItem(mTagData, tagSeenCount, null, null, null, null, null, null);
                        } else {
                            Application.TOTAL_TAGS++;
                            inventoryItem = new InventoryListItem(mTagData, 1, null, null, null, null, null, null);
                        }
                        added = Application.tagsReadInventory.add(inventoryItem);
                        if (added) {
                            Application.inventoryList.put(mTagData, Application.UNIQUE_TAGS);
//                            if (tagData.getOpCode() != null)
//                                if (tagData.getOpCode().toString().equalsIgnoreCase("ACCESS_OPERATION_READ")) {
//                                    memoryBank = tagData.getMemoryBank().toString();
//                                    memoryBankData = tagData.getMemoryBankData().toString();
//
//                                }
                            oldObject = Application.tagsReadInventory.get(Application.UNIQUE_TAGS);
                            oldObject.setMemoryBankData(memoryBankData);
                            oldObject.setMemoryBank(memoryBank);
                            if (RFIDController.pc)
                                oldObject.setPC(Integer.toHexString(tagData.getPC()));
                            if (RFIDController.phase)
                                oldObject.setPhase(Integer.toString(tagData.getPhase()));
                            if (RFIDController.channelIndex)
                                oldObject.setChannelIndex(Integer.toString(tagData.getChannelIndex()));
                            if (RFIDController.rssi)
                                oldObject.setRSSI(Integer.toString(tagData.getPeakRSSI()));
                            Application.UNIQUE_TAGS++;
                            if (RFIDController.brandidcheckenabled) {
                                if (tagData.getBrandIDStatus()) {
                                    //oldObject.brandIDfound = true;
                                    oldObject.setBrandIDStatus(true);
                                    RFIDController.bFound = true;
                                    //Log.i("MainActivity", "getBrandIDStatus" + oldObject.getBrandIDStatus());
                                } else {
                                    oldObject.setBrandIDStatus(false);
                                }
                            }
                        }
                    }
                }
                // beep on each tag read
                startbeepingTimer();
            } catch (IndexOutOfBoundsException e) {
                //logAsMessage(TYPE_ERROR, TAG, e.getMessage());
                oldObject = null;
                added = false;
            } catch (Exception e) {
                // logAsMessage(TYPE_ERROR, TAG, e.getMessage());
                oldObject = null;
                added = false;
            }
            inventoryItem = null;
            memoryBank = null;
            memoryBankData = null;
            return added;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            cancel(true);
            if (oldObject != null && fragment instanceof ResponseHandlerInterfaces.ResponseTagHandler) {
                ((ResponseHandlerInterfaces.ResponseTagHandler) fragment).handleTagResponse(oldObject, result);
            }
            oldObject = null;
        }
    }

    //TextView tv_alert_retry_count;
    CustomProgressDialog retryCountDialog = null;
    boolean isCancelPressed = false;

    private void displayRetryCountDialog(final Context context) {
        //Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        Fragment fragment = mActivity.getCurrentFragment(RFID_TAB);
        if (!(fragment instanceof RFIDReadersListFragment) && context != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isCancelPressed = false;
                    retryCountDialog = new CustomProgressDialog(context, "Connecting to " + RFIDController.LAST_CONNECTED_READER + "\n" + "Retry Count : 1");
                    try {
                        if (!mActivity.isFinishing()) retryCountDialog.show();
                    } catch (WindowManager.BadTokenException ex) {
                        if (ex != null) {
                            Log.e(TAG, ex.getMessage());
                        }
                    }
                }
            });

        }
    }

    int retryCount;
    private Context mContext = mActivity;

    protected class UpdateDisconnectedStatusTask extends AsyncTask<Void, Void, Boolean> {
        private final String device;
        // store current reader state
        private final ReaderDevice readerDevice;
        long disconnectedTime;
        boolean bConnected = false;

        public UpdateDisconnectedStatusTask(String device) {
            this.device = device;
            disconnectedTime = System.currentTimeMillis();
            // store current reader state
            readerDevice = RFIDController.mConnectedDevice;
            //
            //mReaderDisappeared = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (readerDevice != null && readerDevice.getName().equalsIgnoreCase(device)) {

                        Fragment fragment = mActivity.getCurrentFragment(SETTINGS_TAB);
                        if (fragment instanceof FactoryResetFragment) {
                            ((FactoryResetFragment) fragment).RFIDReaderDisappeared(readerDevice);
                            mActivity.setCurrentTabFocus(READERS_TAB);
                        }
                        readerDisconnected(readerDevice);
                    } else {
                        readerDisconnected(new ReaderDevice(device, null));
                    }
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!isCancelled()) {

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG, "onCancelled disconnect" + readerDevice);
            try {
                if (readerDevice != null) readerDevice.getRFIDReader().disconnect();
            } catch (InvalidUsageException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            } catch (OperationFailureException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
        }
    }

    private void deviceReconnect(ReaderDevice readerDevice) throws InvalidUsageException, OperationFailureException {

        //Need to reconnect both scanner and rfid here for premium plus devices
        readerDevice.getRFIDReader().reconnect();
    }

    void StoreConnectedReader() {
        if (RFIDController.AUTO_RECONNECT_READERS && RFIDController.mConnectedReader != null) {
            RFIDController.LAST_CONNECTED_READER = RFIDController.mConnectedReader.getHostName();
            SharedPreferences settings = mActivity.getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(Constants.LAST_READER, RFIDController.LAST_CONNECTED_READER);
            editor.commit();
        }
    }

    void clearConnectedReader() {
        SharedPreferences settings = mActivity.getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.LAST_READER, "");
        editor.commit();
        RFIDController.LAST_CONNECTED_READER = "";
        RFIDController.mConnectedDevice = null;
    }

    private boolean m_ScreenOn = true;
    // Broadcast receiver to receive the scanner_status, and disable the scanner
    public BroadcastReceiver BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_SCREEN_OFF:
                    //Log.d(TAG, "BroadcastReceiver: " + action + " " + RFIDController.mConnectedReader.getHostName());
                    if (!RFIDController.mIsInventoryRunning) m_ScreenOn = false;
                    break;
                case ACTION_SCREEN_ON:
                    //Log.d(TAG, "BroadcastReceiver: " + action + " " + RFIDController.mConnectedReader.getHostName());
                    m_ScreenOn = true;
                    break;
                case scanner_status:
                    //Log.d(TAG, intent.getExtras().getString("STATUS"));
                    break;
                case DW_APIRESULT_ACTION: {
                    String command = intent.getStringExtra("COMMAND");
                    String commandidentifier = intent.getStringExtra("COMMAND_IDENTIFIER");
                    String result = intent.getStringExtra("RESULT");
                    if (command != null && command.equals("com.symbol.datawedge.api.SET_CONFIG")) {
                        if (commandidentifier.equals(Application.RFID_DATAWEDGE_PROFILE_CREATION)) {
                            Bundle bundle = new Bundle();
                            String resultInfo = "";
                            if (intent.hasExtra("RESULT_INFO")) {
                                bundle = intent.getBundleExtra("RESULT_INFO");
                                resultInfo = bundle.getString("RESULT_CODE");
                            }
                            if (result.equals("SUCCESS")) {
                                disableScanner();

                            } else {
                                //   Log.d(TAG, "Failed to Disable scanner " + resultInfo);
                            }
                            Set<String> keys = bundle.keySet();
                            resultInfo = "";
                            for (String key : keys) {
                                resultInfo += key + ": " + bundle.getString(key) + "\n";
                            }
                            Log.d(TAG, "Disable scanner " + resultInfo);
                        }
                    }
                }
                break;
            }
        }
    };


    public static InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!isAllowed(source.charAt(i))) return "";
            }
            return null;
        }

        String allowed = "0123456789ABCDEFabcdef";

        private boolean isAllowed(char c) {
            if (RFIDController.asciiMode == false) {
                for (char ch : allowed.toCharArray()) {
                    if (ch == c) return true;
                }
                return false;
            }
            return true;
        }
    };

    private class MatchingTagsResponseHandlerTask extends AsyncTask<Void, Void, Boolean> {

        private TagData tagData;
        private InventoryListItem inventoryItem;
        private InventoryListItem oldObject;
        private Fragment fragment;
        private String memoryBank;
        private String memoryBankData;
        //private Toast myToast;

        MatchingTagsResponseHandlerTask(TagData tagData, Fragment fragment) {
            this.tagData = tagData;
            this.fragment = fragment;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //TODO: background task with UI thread runnable requires fix, all lists are assigned to list adapter so always run from ui thread
            mActivity.runOnUiThread(new Runnable() {
                @SuppressLint("WrongConstant")
                @Override
                public void run() {
                    boolean added = false;

                    //RFIDController.isCSVtagsLoaded=true;
                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.tag_match_complete), Toast.LENGTH_SHORT).show();
                    try {
                        if (Application.inventoryList.containsKey(tagData.getTagID())) {
                            inventoryItem = new InventoryListItem(tagData.getTagID(), 1, null, null, null, null, null, null);
                            int index = Application.inventoryList.get(tagData.getTagID());
                            if (index >= 0) {
                                if (Application.tagListMap.containsKey(tagData.getTagID()))
                                    Application.tagsReadInventory.get(index).setTagStatus("MATCH");
                                else
                                    Application.tagsReadInventory.get(index).setTagStatus("UNKNOWN");
                                Application.TOTAL_TAGS++;
                                //Tag is already present. Update the fields and increment the count
                                if (tagData.getOpCode() != null)
                                    if (tagData.getOpCode().toString().equalsIgnoreCase("ACCESS_OPERATION_READ")) {
                                        memoryBank = tagData.getMemoryBank().toString();
                                        memoryBankData = tagData.getMemoryBankData().toString();
                                    }
                                if (Application.memoryBankId == 1) {  //matching tags
                                    if (Application.tagListMap.containsKey(tagData.getTagID()) && !Application.matchingTagsList.contains(Application.tagsReadInventory.get(index))) {
                                        Application.matchingTagsList.add(Application.tagsReadInventory.get(index));
                                        Application.tagsReadForSearch.add(Application.tagsReadInventory.get(index));
                                        added = true;
                                    }
                                } else if (Application.memoryBankId == 2 && Application.tagListMap.containsKey(tagData.getTagID())) {
                                    if (Application.missingTagsList.contains(Application.tagsReadInventory.get(index))) {
                                        Application.missingTagsList.remove(Application.tagsReadInventory.get(index));
                                        Application.tagsReadForSearch.remove(Application.tagsReadInventory.get(index));
                                        added = true;
                                    }
                                }
                                oldObject = Application.tagsReadInventory.get(index);
                                if (oldObject.getCount() == 0) {
                                    Application.missedTags--;
                                    Application.matchingTags++;
                                    Application.UNIQUE_TAGS++;
                                }
                                oldObject.incrementCount();
                                if (oldObject.getMemoryBankData() != null && !oldObject.getMemoryBankData().equalsIgnoreCase(memoryBankData))
                                    oldObject.setMemoryBankData(memoryBankData);
                                //oldObject.setEPCId(inventoryItem.getEPCId());
                                oldObject.setPC(Integer.toString(tagData.getPC()));
                                oldObject.setPhase(Integer.toString(tagData.getPhase()));
                                oldObject.setChannelIndex(Integer.toString(tagData.getChannelIndex()));
                                if (RFIDController.rssi)
                                    oldObject.setRSSI(Integer.toString(tagData.getPeakRSSI()));
                            }

                        } else {
                            //Tag is encountered for the first time. Add it.
                            if (RFIDController.inventoryMode == 0 || (RFIDController.inventoryMode == 1 && Application.UNIQUE_TAGS_CSV <= Constants.UNIQUE_TAG_LIMIT)) {
                                int tagSeenCount = tagData.getTagSeenCount();
                                if (tagSeenCount != 0) {
                                    Application.TOTAL_TAGS += tagSeenCount;
                                    inventoryItem = new InventoryListItem(tagData.getTagID(), tagSeenCount, null, null, null, null, null, null);
                                } else {
                                    Application.TOTAL_TAGS++;
                                    inventoryItem = new InventoryListItem(tagData.getTagID(), 1, null, null, null, null, null, null);
                                }
                                if (Application.tagListMap.containsKey(tagData.getTagID()))
                                    inventoryItem.setTagStatus("MATCH");
                                else inventoryItem.setTagStatus("UNKNOWN");
                                if (Application.memoryBankId == 1)
                                    Application.tagsReadInventory.add(inventoryItem);
                                else if (Application.memoryBankId == 3) {
                                    inventoryItem.setTagDetails("unknown");
                                    added = Application.tagsReadInventory.add(inventoryItem);
                                    Application.unknownTagsList.add(inventoryItem);
                                    Application.tagsReadForSearch.add(inventoryItem);
                                } else {
                                    if (inventoryItem.getTagDetails() == null) {
                                        inventoryItem.setTagDetails("unknown");
                                    }
                                    added = Application.tagsReadInventory.add(inventoryItem);
                                    if (Application.memoryBankId != 2)
                                        Application.tagsReadForSearch.add(inventoryItem);
                                }
                                if (added || Application.memoryBankId == 1) {
                                    Application.inventoryList.put(tagData.getTagID(), Application.UNIQUE_TAGS_CSV);
                                    if (tagData.getOpCode() != null)
                                        if (tagData.getOpCode().toString().equalsIgnoreCase("ACCESS_OPERATION_READ")) {
                                            memoryBank = tagData.getMemoryBank().toString();
                                            memoryBankData = tagData.getMemoryBankData().toString();
                                        }
                                    oldObject = Application.tagsReadInventory.get(Application.UNIQUE_TAGS_CSV);
                                    oldObject.setMemoryBankData(memoryBankData);
                                    oldObject.setMemoryBank(memoryBank);
                                    oldObject.setPC(Integer.toString(tagData.getPC()));
                                    oldObject.setPhase(Integer.toString(tagData.getPhase()));
                                    oldObject.setChannelIndex(Integer.toString(tagData.getChannelIndex()));
                                    if (RFIDController.rssi)
                                        oldObject.setRSSI(Integer.toString(tagData.getPeakRSSI()));
                                    Application.UNIQUE_TAGS++;
                                    Application.UNIQUE_TAGS_CSV++;
                                }
                            }
                        }
                        // Notify user when tags from tag list are read atleast once  8613
                        // beep on each tag read
                        startbeepingTimer();
                    } catch (IndexOutOfBoundsException e) {
                        //logAsMessage(TYPE_ERROR, TAG, e.getMessage());
                        oldObject = null;
                        added = false;
                    } catch (Exception e) {
                        //logAsMessage(TYPE_ERROR, TAG, e.getMessage());
                        oldObject = null;
                        added = false;
                    }
                    tagData = null;
                    inventoryItem = null;
                    memoryBank = null;
                    memoryBankData = null;
                    // call notifyDataSetChanged from same runnalbe instead of onPostExecute
                    if (oldObject != null && fragment instanceof ResponseHandlerInterfaces.ResponseTagHandler) {
                        ((ResponseHandlerInterfaces.ResponseTagHandler) fragment).handleTagResponse(oldObject, false);
                    }
                    if (Application.matchingTags != 0 && Application.missedTags == 0 && !RFIDController.tagListMatchNotice) {
                        RFIDController.tagListMatchNotice = true;


                        Toast.makeText(mActivity.getApplicationContext(), mActivity.getResources().getString(R.string.tag_match_complete), Toast.LENGTH_SHORT).show();

                        if (RFIDController.tagListMatchAutoStop) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //mActivity.performtagmatchClick();
                                    inventoryStartOrStop();

                                }
                            });
                        }
                    }
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            oldObject = null;
        }

    }

    public void selectNavigationMenuItem(int pos) {
        navigationView.getMenu().getItem(pos).setChecked(true);
    }

    public static void setAccessProfile(boolean bSet) {
        RFIDController.getInstance().setAccessProfile(bSet);
    }

    /**
     * method to stop progress pairTaskDailog on timeout
     *
     * @param time
     * @param d
     * @param command
     */
    public void timerDelayRemoveDialog(long time, final Dialog d, final String command) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (d != null && d.isShowing()) {
                    d.dismiss();
                    //TODO: cross check on selective flag clearing
                    if (RFIDController.isAccessCriteriaRead) {
                        if (accessTagCount == 0) {
                            if (command.equals("Read"))
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, mActivity.getString(R.string.err_read_access_op_failed));
                            if (command.equals("Write"))
                                sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, mActivity.getString(R.string.err_access_op_failed));
                        }
                        RFIDController.isAccessCriteriaRead = false;
                    } else {
                        sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, command + " timeout");
                        if (isActivityVisible()) callBackPressed();
                    }
                    RFIDController.isAccessCriteriaRead = false;
                    accessTagCount = 0;
                }
            }
        }, time);
    }

    /**
     * Method to send the notification
     *
     * @param action - intent action
     * @param data   - notification message
     */


    public void sendNotification(String action, String data) {
        if (isActivityVisible()) {
            if (action.equalsIgnoreCase(Constants.ACTION_READER_BATTERY_CRITICAL) || action.equalsIgnoreCase(Constants.ACTION_READER_BATTERY_LOW)) {
                new CustomToast(mActivity, R.layout.toast_layout, data).show();
            } else {
                Toast.makeText(mActivity, data, Toast.LENGTH_SHORT).show();
            }
            NotificationUtil.displayNotification(mActivity.getApplicationContext(), action, data);
        } else {
        }
    }


    /**
     * Method to be called from Fragments of this activity after handling the response from the reader(success / failure)
     */
    public void callBackPressed() {

    }

    public void deleteDWProfile() {
        Intent i = new Intent();
        i.setAction("com.symbol.datawedge.api.ACTION");
        String[] values = {"RFIDMobileApp"};
        i.putExtra("com.symbol.datawedge.api.DELETE_PROFILE", values);
        mActivity.sendBroadcast(i);

    }


    public void disableScanner() {
        Intent i = new Intent();
        i.setAction("com.symbol.datawedge.api.ACTION");
        i.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "DISABLE_PLUGIN");
        i.putExtra("SEND_RESULT", "false");
        i.putExtra("COMMAND_IDENTIFIER", Application.RFID_DATAWEDGE_DISABLE_SCANNER);  //Unique identifier
        mActivity.sendBroadcast(i);
    }

    public void enableScanner() {
        Intent i = new Intent();
        i.setAction("com.symbol.datawedge.api.ACTION");
        i.putExtra("com.symbol.datawedge.api.SCANNER_INPUT_PLUGIN", "ENABLE_PLUGIN");
        i.putExtra("SEND_RESULT", "false");
        i.putExtra("COMMAND_IDENTIFIER", Application.RFID_DATAWEDGE_ENABLE_SCANNER);
        mActivity.sendBroadcast(i);
    }


    public interface CreateFileInterface {
        public void createFile1(Uri uri);
    }

}
