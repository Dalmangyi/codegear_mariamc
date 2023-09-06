package com.codegear.mariamc_rfid;

import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mConnectedReader;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.cowchronicle.activities.WebviewActivity;
import com.codegear.mariamc_rfid.cowchronicle.utils.PixelUtil;
import com.codegear.mariamc_rfid.rfidreader.common.CustomProgressDialog;
import com.codegear.mariamc_rfid.rfidreader.home.RFIDEventHandler;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.InitReadersListFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.PasswordDialog;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingsContent;
import com.codegear.mariamc_rfid.scanner.helpers.Constants;
import com.google.android.material.navigation.NavigationView;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.discover_connect.nfc.PairOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.common.ResponseHandlerInterfaces;
import com.codegear.mariamc_rfid.rfidreader.notifications.NotificationUtil;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.settings.BatteryFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingsDetailActivity;
import com.codegear.mariamc_rfid.scanner.activities.BaseActivity;
import com.codegear.mariamc_rfid.scanner.activities.NavigationHelpActivity;
import com.codegear.mariamc_rfid.scanner.activities.UpdateFirmware;
import com.codegear.mariamc_rfid.scanner.fragments.ReaderDetailsFragment;
import com.codegear.mariamc_rfid.scanner.helpers.AvailableScanner;
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
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 100;
    DrawerLayout mDrawerLayout;
    Fragment fragment = null;
    public static RFIDEventHandler mEventHandler;
    private DeviceDiscoverActivity mDeviceDiscoverActivity;
    public String nfcData;
    public static ReaderDevice mConnectedReaderDetails;
    private Bundle mSavedInstanceState = null;
    private int vendorId = 0x05E0;
    private int productId = 0x1701;
    private static final String INTENT_ACTION_GRANT_USB = "com.zebra.rfid.app.USB_PERMISSION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceDiscoverActivity = this;
        mSavedInstanceState = savedInstanceState;
        if (mConnectedReader != null) {
            Log.d(TAG, "There is no way you can come here ");
        }
        setContentView(R.layout.discover_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.dis_toolbar);
        setSupportActionBar(toolbar);
        toolbar = (Toolbar) findViewById(R.id.dis_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_empty_readers));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.discover_drawer_layout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.discover_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState(); //ActionBarDrawerToggle 누를때 앱 종료되는 현상 막기.

        //사이즈 측정
        View vDisMainMenu = getLayoutInflater().inflate(R.layout.dis_main_menu, null, false);
        vDisMainMenu.measure(0, 0);
        int disMainMenuHeight = vDisMainMenu.getMeasuredHeight();
        int screenHeight = PixelUtil.getScreenHeightPx(this);
        int navigationViewMenuHeight = (int) getResources().getDimension(R.dimen.drawer_navigationview_item_height);

        //NavigationView의 특정 Menu에 사이즈 적용
        NavigationView navigationView = (NavigationView) findViewById(R.id.disnav_view);
        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.menu_empty);
        final View v = getLayoutInflater().inflate(R.layout.drawer_menu_custom_item, null);
        v.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            int originalHeight = 0;

            @Override
            public void onViewAttachedToWindow(View v) {
                View parent = (View) v.getParent();
                if (parent != null) parent = (View) parent.getParent();

                if (parent != null) {
                    ViewGroup.LayoutParams p = parent.getLayoutParams();
                    originalHeight = p.height;
                    int drawerMenuCustomItemHeight = screenHeight - disMainMenuHeight - (navigationViewMenuHeight * 5);
                    if (drawerMenuCustomItemHeight < 0) {
                        drawerMenuCustomItemHeight = 0;
                    }
                    p.height = drawerMenuCustomItemHeight;
                    parent.setLayoutParams(p);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (originalHeight != 0) {
                    View parent = (View) v.getParent();
                    if (parent != null) parent = (View) parent.getParent();

                    if (parent != null) {
                        ViewGroup.LayoutParams p = parent.getLayoutParams();
                        p.height = originalHeight;
                    }
                }
            }
        });
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setActionView(v);
        menuItem.setIcon(null);
        menuItem.setTitle(null);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        deleteDWProfile();

        if (RFIDController.readers == null) {
            RFIDController.readers = new Readers(this.getApplicationContext(), ENUM_TRANSPORT.ALL);
        }
        // attach to reader list handler
        RFIDController.readers.attach(this);
        if (savedInstanceState == null) {
            //loadReaders(this);
            mEventHandler = new RFIDEventHandler();
        }
        //Scanner Initializations
        //Handling Runtime BT permissions for Android 12 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
                initialize();
            }

        } else {
            initialize();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialize();
            } else {
                Toast.makeText(this, "Bluetooth Permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            switchToFragment(fragment);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.menu_cowchronicle:
                Intent webviewIntent = new Intent(this, WebviewActivity.class);
                webviewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(webviewIntent);
                return true;

            case R.id.menu_readers:
                return true;

            case R.id.nav_fw_update:
                if (mConnectedReader != null && mConnectedReader.isConnected()) {
                    loadUpdateFirmware(MenuItemCompat.getActionView(item));
                } else {
                    Toast.makeText(this, "연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.nav_battery_statics:
                Intent detailsIntent = new Intent(this, SettingsDetailActivity.class);
                detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                detailsIntent.putExtra(com.codegear.mariamc_rfid.rfidreader.common.Constants.SETTING_ITEM_ID, Integer.parseInt(SettingsContent.ITEMS.get(3).id));
                startActivity(detailsIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadUpdateFirmware(View actionView) {
        Intent intent = new Intent(this, UpdateFirmware.class);
        intent.putExtra(Constants.SCANNER_ID, Application.currentConnectedScannerID);
        intent.putExtra(Constants.SCANNER_NAME, getIntent().getStringExtra(Constants.SCANNER_NAME));
        //startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }

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
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
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
