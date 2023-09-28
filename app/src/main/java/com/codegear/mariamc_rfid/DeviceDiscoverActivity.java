package com.codegear.mariamc_rfid;

import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mConnectedReader;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.CowChronicleActivity;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.UserLoginActivity;
import com.codegear.mariamc_rfid.cowchronicle.consts.CowChronicleScreenEnum;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.ui.drawer.CustomDiscoverDrawer;
import com.codegear.mariamc_rfid.cowchronicle.utils.PermissionUtil;
import com.codegear.mariamc_rfid.rfidreader.home.RFIDEventHandler;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.InitReadersListFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.PasswordDialog;
import com.codegear.mariamc_rfid.scanner.helpers.Constants;
import com.codegear.mariamc_rfid.discover_connect.nfc.PairOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.common.ResponseHandlerInterfaces;
import com.codegear.mariamc_rfid.rfidreader.notifications.NotificationUtil;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.settings.BatteryFragment;
import com.codegear.mariamc_rfid.scanner.activities.BaseActivity;
import com.codegear.mariamc_rfid.scanner.activities.UpdateFirmware;
import com.codegear.mariamc_rfid.scanner.fragments.ReaderDetailsFragment;
import com.codegear.mariamc_rfid.scanner.helpers.ScannerAppEngine;
import com.zebra.rfid.api3.ENUM_TRANSPORT;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import com.zebra.scannercontrol.DCSSDKDefs;
import java.util.Locale;

public class DeviceDiscoverActivity extends BaseActivity implements Readers.RFIDReaderEventHandler, ResponseHandlerInterfaces.ReaderDeviceFoundHandler, ResponseHandlerInterfaces.BatteryNotificationHandler, ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate {

    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    Fragment fragment = null;
    public static RFIDEventHandler mEventHandler;
    private DeviceDiscoverActivity mDeviceDiscoverActivity;
    public String nfcData;
    public static ReaderDevice mConnectedReaderDetails;
    private Bundle mSavedInstanceState = null;
    private int vendorId = 0x05E0;
    private int productId = 0x1701;
    private static final String INTENT_ACTION_GRANT_USB = "com.zebra.rfid.app.USB_PERMISSION";

    public static final String DESTINATION_SCREEN_IS_COWCHRONICLE = "destination_screen_is_cowchronicle";
    public static final String ENABLE_AUTO_CONNECT_DEVICE = "disable_auto_connect_device";

    private Context mContext;

    private CustomDiscoverDrawer mCustomDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mDeviceDiscoverActivity = this;
        mSavedInstanceState = savedInstanceState;
        if (mConnectedReader != null) {
            Log.d(TAG, "There is no way you can come here ");
        }
        setContentView(R.layout.activity_discover);

        //Drawer & ActionBar 세팅
        mCustomDrawer = new CustomDiscoverDrawer(this);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_empty_readers));



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        deleteDWProfile();

        if (RFIDController.readers == null) {
            RFIDController.readers = new Readers(this.getApplicationContext(), ENUM_TRANSPORT.ALL);
        }
        // attach to reader list handler
        RFIDController.readers.attach(this);
        if (savedInstanceState == null) {
            mEventHandler = new RFIDEventHandler();
        }


        //블루투스 권한 요청
        PermissionUtil.reqPermissions(this, () -> {
            //모두 허가되었다면, 초기화 진행하기
            initialize();
        }, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT);

        Button btnNavigationBottom1 = findViewById(R.id.btnNavigationBottom1);
        Button btnNavigationBottom2 = findViewById(R.id.btnNavigationBottom2);
        btnNavigationBottom1.setOnClickListener(v -> {
            goIntentCowChronicle();
        });
        btnNavigationBottom2.setOnClickListener(v -> {
            goIntentCowTags();
        });
    }


    //카우크로니클 웹뷰로 이동
    private void goIntentCowChronicle(){
        //로그인 했을 경우만 웹뷰로 이동.
        if(UserStorage.getInstance().isLogin()){
            finishAffinity();
            Intent intent = new Intent(this, CowChronicleActivity.class);
            intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.WEBVIEW.toString());
            startActivity(intent);
        }
        //로그인 안했으면, 로그인 페이지로 이동.
        else{
            finishAffinity();
            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
        }
    }

    //전자이표 화면으로 이동
    private void goIntentCowTags(){
        //로그인 안했을 경우, 로그인 화면으로 이동.
        if(!UserStorage.getInstance().isLogin()){
            finishAffinity();
            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
        }
        else {
            //로그인 했고 기기가 연결 되어 있다면, 농장 선택화면으로 이동
            if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
                finishAffinity();
                Intent intent = new Intent(this, UserLoginActivity.class);
                startActivity(intent);
            }
            //로그인 했고 기기가 연결 안 되어있다면, 안내메세지 출력
            else {
                CustomDialog.showSimple(this, "기기를 연결해 주세요.");
            }
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            nfcData = ((Application) getApplication()).processNFCData(intent);
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
            processTAGData(intent);
    }

    public void deleteDWProfile() {
        Intent i = new Intent();
        i.setAction("com.symbol.datawedge.api.ACTION");
        String[] values = {"RFIDMobileApp"};
        i.putExtra("com.symbol.datawedge.api.DELETE_PROFILE", values);
        mDeviceDiscoverActivity.sendBroadcast(i);

    }

    private void processTAGData(Intent intent) {
        Log.i(TAG, "ProcessTAG data ");

        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_TAG);
        if (rawMessages != null && rawMessages.length > 0) {

            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }

            Log.i(TAG, "message size = " + messages.length);

            NdefMessage msg = (NdefMessage) rawMessages[0];
            String base = new String(msg.getRecords()[0].getPayload());
            String str = String.format(Locale.getDefault(), "Message entries=%d. Base message is %s", rawMessages.length, base);
            Log.i(TAG, "message  = " + str);

        }
    }


    public String copyNfcContent() {
        return nfcData;

    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (INTENT_ACTION_GRANT_USB.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            onPostPermissionGranted();
                        }
                    } else {
                        Log.d(TAG, "USB permission denied for device " + device);
                    }
                }
            }
            unregisterReceiver(usbReceiver);
        }
    };


    private void initialize() {
        PendingIntent permissionIntent;
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbManager.getDeviceList().size() > 0) {
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                if ((device.getVendorId() == vendorId) && (device.getProductId() == productId)) {
                    if (!usbManager.hasPermission(device)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                        } else {
                            permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), PendingIntent.FLAG_UPDATE_CURRENT);
                        }
                        IntentFilter filter = new IntentFilter(INTENT_ACTION_GRANT_USB);
                        registerReceiver(usbReceiver, filter);
                        usbManager.requestPermission(device, permissionIntent);
                        break;
                    } else {
                        onPostPermissionGranted();
                        break;
                    }
                }
            }
        } else {
            onPostPermissionGranted();
        }
    }

    private void onPostPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestBTEnable();
        } else {
            initializeDcsSdk(true);
        }
    }

    private void broadcastSCAisListening() {
        Intent intent = new Intent();
        intent.setAction("com.zebra.scannercontrol.LISTENING_STARTED");
        sendBroadcast(intent);
    }

    private void requestBTEnable() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityResultLauncher.launch(enableBtIntent);
        } else {
            initializeDcsSdk(true);
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> initializeDcsSdk(result.getResultCode() == AppCompatActivity.RESULT_OK));

    private void initializeDcsSdk(boolean enableBTConnect) {
        Application.sdkHandler.dcssdkEnableAvailableScannersDetection(true);
        if (enableBTConnect) {
            Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);
            Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_LE);
        }
        //Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI);
        Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_USB_CDC);


        addDevConnectionsDelegate(this);

        broadcastSCAisListening();

        if (mSavedInstanceState == null) {
            fragment = InitReadersListFragment.getInstance();
            ((InitReadersListFragment)fragment).setDestinationScreenCowChronicle(getIntent().getBooleanExtra(DESTINATION_SCREEN_IS_COWCHRONICLE, false));
            ((InitReadersListFragment)fragment).cowchronicleStartPage(getIntent().getStringExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE));
            ((InitReadersListFragment)fragment).enableAutoConnectDevice(getIntent().getBooleanExtra(ENABLE_AUTO_CONNECT_DEVICE, false));

            switchToFragment(fragment);
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Boolean ret = mCustomDrawer.onOptionsItemSelected(item);
        if (ret != null){
            return ret;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadUpdateFirmware(View actionView) {
        Intent intent = new Intent(this, UpdateFirmware.class);
        intent.putExtra(Constants.SCANNER_ID, Application.currentConnectedScannerID);
        intent.putExtra(Constants.SCANNER_NAME, getIntent().getStringExtra(Constants.SCANNER_NAME));
        //startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (currentFragment != null && (currentFragment instanceof PairOperationsFragment) || currentFragment instanceof ReaderDetailsFragment) {
            setActionBarTitle(getResources().getString(R.string.title_empty_readers));
            Fragment fragment = InitReadersListFragment.getInstance();
            if (fragment != null) {
                switchToFragment(fragment);
            }
        } else {
            minimizeApp();
        }
    }

    private void minimizeApp() {
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }


    @Override
    public void RFIDReaderAppeared(ReaderDevice readerDevice) {
        runOnUiThread(() -> {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
            if (fragment instanceof InitReadersListFragment) {
                ((InitReadersListFragment) fragment).RFIDReaderAppeared(readerDevice);
            }
        });
    }

    @Override
    public void RFIDReaderDisappeared(ReaderDevice readerDevice) {
        runOnUiThread(() -> {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
            if (fragment instanceof InitReadersListFragment) {
                ((InitReadersListFragment) fragment).RFIDReaderDisappeared(readerDevice);
            }
        });
    }

    @Override
    public void ReaderDeviceConnected(ReaderDevice device) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof InitReadersListFragment) {
            ((InitReadersListFragment) fragment).ReaderDeviceConnected(device);
        }
    }

    @Override
    public void ReaderDeviceDisConnected(ReaderDevice device) {
        PasswordDialog.isDialogShowing = false;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof InitReadersListFragment) {
            ((InitReadersListFragment) fragment).ReaderDeviceDisConnected(device);
            ((InitReadersListFragment) fragment).readerDisconnected(device, false);
        }
    }

    @Override
    public void ReaderDeviceConnFailed(ReaderDevice device) {

    }

    @Override
    public void deviceStatusReceived(int level, boolean charging, String cause) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CONTENT_FRAGMENT);
        if (fragment instanceof BatteryFragment) {
            ((BatteryFragment) fragment).deviceStatusReceived(level, charging, cause);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RFIDController.readers.deattach(this);
        if (mConnectedReader != null) {
            try {
                mConnectedReader.Events.removeEventsListener(mEventHandler);

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

    @Override
    public boolean scannerHasAppeared(int scannerID) {
        return false;
    }

    @Override
    public boolean scannerHasDisappeared(int scannerID) {
        return false;
    }

    @Override
    public boolean scannerHasConnected(int scannerID) {
        return false;
    }

    @Override
    public boolean scannerHasDisconnected(int scannerID) {
        return false;
    }

    public void sendNotification(String action, String data) {
        NotificationUtil.displayNotification(getApplicationContext(), action, data);
    }

    public void switchToFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings_content_frame, fragment, TAG_CONTENT_FRAGMENT).commit();
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void loadReaderDetails(ReaderDevice readerDevice) {
        connectedReaderDetails(readerDevice);
        fragment = ReaderDetailsFragment.newInstance();
        switchToFragment(fragment);
    }

    private void connectedReaderDetails(ReaderDevice readerDevice) {
        mConnectedReaderDetails = readerDevice;
    }

    public ReaderDevice connectedReaderDetails() {
        return mConnectedReaderDetails;
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//Non-default
            getResources();
        super.onConfigurationChanged(newConfig);

    }

    public NfcAdapter.CreateNdefMessageCallback _onNfcCreateCallback = new NfcAdapter.CreateNdefMessageCallback() {
        @Override
        public NdefMessage createNdefMessage(NfcEvent inputNfcEvent) {
            Log.i(TAG, "createNdefMessage");
            return createMessage();
        }
    };

    private NdefMessage createMessage() {
        String text = ("Hello there from another device!\n\n" + "Beam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(new NdefRecord[]{NdefRecord.createMime("application/com.bluefletch.nfcdemo.mimetype", text.getBytes())});
        return msg;
    }


}
