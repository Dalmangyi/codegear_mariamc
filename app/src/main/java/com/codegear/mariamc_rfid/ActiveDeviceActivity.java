package com.codegear.mariamc_rfid;

import static com.codegear.mariamc_rfid.application.Application.DEVICE_PREMIUM_PLUS_MODE;
import static com.codegear.mariamc_rfid.application.Application.DEVICE_STD_MODE;
import static com.codegear.mariamc_rfid.application.Application.RFD_DEVICE_MODE;
import static com.codegear.mariamc_rfid.application.Application.TAG_LIST_LOADED;
import static com.codegear.mariamc_rfid.application.Application.TAG_LIST_MATCH_MODE;
import static com.codegear.mariamc_rfid.application.Application.UNIQUE_TAGS_CSV;
import static com.codegear.mariamc_rfid.application.Application.inventoryList;
import static com.codegear.mariamc_rfid.application.Application.matchingTags;
import static com.codegear.mariamc_rfid.application.Application.missedTags;
import static com.codegear.mariamc_rfid.application.Application.tagListMap;
import static com.codegear.mariamc_rfid.application.Application.tagsListCSV;
import static com.codegear.mariamc_rfid.application.Application.tagsReadInventory;
import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mConnectedReader;
import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mIsInventoryRunning;
import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mReaderDisappeared;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.ANTENNA_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.APPLICATION_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.ASSERT_DEVICE_INFO_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.BARCODE_SYMBOLOGIES_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.BARCODE_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.BATTERY_STATISTICS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.BEEPER_ACTION_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.CHARGE_TERMINAL_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.DEVICE_PAIR_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.DEVICE_RESET_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.DPO_SETTING_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.FACTORY_RESET_FRAGMENT_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.INVENTORY_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.KEYREMAP_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.LOCATE_TAG_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.LOGGER_FRAGMENT_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.MAIN_GENERAL_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.MAIN_HOME_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.MAIN_RFID_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.NONOPER_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RAPID_READ_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.READERS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.READER_DETAILS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.READER_LIST_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.READER_WIFI_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_ACCESS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_ADVANCED_OPTIONS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_BEEPER_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_LED_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_PREFILTERS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_PROFILES_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_REGULATORY_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_WIFI_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SAVE_CONFIG_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SCAN_ADVANCED_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SCAN_DATAVIEW_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SCAN_HOME_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SCAN_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SCAN_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SINGULATION_CONTROL_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.START_STOP_TRIGGER_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.STATIC_IP_CONFIG;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.TAG_REPORTING_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.UPDATE_FIRMWARE_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.USB_MIFI_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.Constants.DEBUG_TYPE.TYPE_DEBUG;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_ACTION_HIGH_HIGH_LOW_LOW_BEEP;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_2_OF_5;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_AZTEC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODEBAR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_11;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_128;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_39;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_93;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_COMPOSITE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_COUPON;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_DATAMARIX;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_EAN_JAN;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_OTHER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_UPC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_EAN_JAN;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_DATABAR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_DATAMATRIX;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_QR_CODE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_MAXICODE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_MSI;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_OCR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER_1D;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER_2D;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_PDF;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_POSTAL_CODES;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_QR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_UNUSED_ID;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_DECODE_COUNT_UPC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_2_OF_5;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_AZTEC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODEBAR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_11;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_128;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_39;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_93;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_COMPOSITE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_COUPON;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_DATAMARIX;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_EAN_JAN;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_OTHER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_UPC;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_EAN_JAN;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_DATABAR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_DATAMATRIX;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_QR_CODE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_MAXICODE;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_MSI;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_OCR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER_1D;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER_2D;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_PDF;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_POSTAL_CODES;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_QR;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_UNUSED_ID;
import static com.zebra.scannercontrol.RMDAttributes.RMD_ATTR_VALUE_SSA_HISTOGRAM_UPC;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.codegear.mariamc_rfid.cowchronicle.ui.activities.CowChronicleActivity;
import com.codegear.mariamc_rfid.cowchronicle.consts.CowChronicleScreenEnum;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.UserLoginActivity;
import com.codegear.mariamc_rfid.cowchronicle.device.RFIDSingleton;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.utils.PixelUtil;
import com.codegear.mariamc_rfid.rfidreader.common.Constants;
import com.codegear.mariamc_rfid.rfidreader.common.CustomProgressDialog;
import com.codegear.mariamc_rfid.rfidreader.home.RFIDBase;
import com.codegear.mariamc_rfid.rfidreader.locate_tag.LocateOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ApplicationSettingsFragment;
import com.codegear.mariamc_rfid.scanner.activities.SsaSetSymbologyActivity;
import com.codegear.mariamc_rfid.scanner.activities.SymbologiesFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.discover_connect.nfc.PairOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.access_operations.AccessOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.common.MatchModeFileLoader;
import com.codegear.mariamc_rfid.rfidreader.inventory.InventoryListItem;
import com.codegear.mariamc_rfid.rfidreader.inventory.RFIDInventoryFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.FactoryResetFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.ManagerFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.ScanHomeSettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.rapidread.RapidReadFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.RFIDReadersListFragment;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.settings.AdvancedOptionItemFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.AdvancedOptionsContent;
import com.codegear.mariamc_rfid.rfidreader.settings.AntennaSettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.BackPressedFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.BatteryFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.BatteryStatsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.BeeperFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ChargeTerminalFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.DPOSettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ISettingsUtil;
import com.codegear.mariamc_rfid.rfidreader.settings.KeyRemapFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.LedFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.PreFilterFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ProfileFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.RegulatorySettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SaveConfigurationsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingListFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingsDetailActivity;
import com.codegear.mariamc_rfid.rfidreader.settings.SingulationControlFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.StartStopTriggersFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.TagReportingFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.UsbMiFiFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.WifiFragment;
import com.codegear.mariamc_rfid.scanner.activities.AssertFragment;
import com.codegear.mariamc_rfid.scanner.activities.BaseActivity;
import com.codegear.mariamc_rfid.scanner.activities.BatteryStatistics;
import com.codegear.mariamc_rfid.scanner.activities.BeeperActionsFragment;
import com.codegear.mariamc_rfid.scanner.activities.ImageActivity;
import com.codegear.mariamc_rfid.scanner.activities.IntelligentImageCaptureActivity;
import com.codegear.mariamc_rfid.scanner.activities.LEDActivity;
import com.codegear.mariamc_rfid.scanner.activities.NavigationHelpActivity;
import com.codegear.mariamc_rfid.scanner.activities.ScaleActivity;
import com.codegear.mariamc_rfid.scanner.activities.ScanSpeedAnalyticsActivity;
import com.codegear.mariamc_rfid.scanner.activities.UpdateFirmware;
import com.codegear.mariamc_rfid.scanner.activities.VibrationFeedback;
import com.codegear.mariamc_rfid.scanner.fragments.AdvancedFragment;
import com.codegear.mariamc_rfid.scanner.fragments.BarcodeFragment;
import com.codegear.mariamc_rfid.scanner.fragments.ReaderDetailsFragment;
import com.codegear.mariamc_rfid.scanner.fragments.SettingsFragment;
import com.codegear.mariamc_rfid.scanner.fragments.Static_ipconfig;
import com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter;
import com.codegear.mariamc_rfid.scanner.helpers.ActiveDevicePremiumAdapter;
import com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceStandardAdapter;
import com.codegear.mariamc_rfid.scanner.helpers.DotsProgressBar;
import com.codegear.mariamc_rfid.scanner.helpers.ScannerAppEngine;
import com.codegear.mariamc_rfid.scanner.receivers.NotificationsReceiver;
import com.codegear.mariamc_rfid.wifi.ReaderWifiSettingsFragment;
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.FirmwareUpdateEvent;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActiveDeviceActivity extends BaseActivity implements AdvancedOptionItemFragment.OnAdvancedListFragmentInteractionListener, ActionBar.TabListener, ScannerAppEngine.IScannerAppEngineDevEventsDelegate, ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate, ISettingsUtil, NavigationView.OnNavigationItemSelectedListener, RFIDBase.CreateFileInterface {
    private static final String TAG_RFID_FRAGMENT = "RFID_FRAGMENT";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private static boolean activityVisible;
    private ViewPager viewPager;
    ActiveDeviceAdapter mAdapter;
    TabLayout tabLayout;
    static int picklistMode;
    ExtendedFloatingActionButton inventoryBT = null;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 10;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_CSV = 11;
    private static RFIDBase mRFIDBase;
    private TabLayoutMediator mTabLayoutMediator;
    public static final String BRAND_ID = "brandid";
    public static final String EPC_LEN = "epclen";
    public static final String IS_BRANDID_CHECK = "brandidcheck";
    private ActiveDeviceActivity mActiveDeviceActivity;
    private boolean onSaveInstanceState = false;
    private Dialog dialogFwRebooting;
    private DotsProgressBar dotProgressBar;
    protected ProgressDialog progressDialog;
    public String nfcData;
    public static ReaderDevice mConnectedReaderDetails;


    public boolean isPagerMotorAvailable() {
        return pagerMotorAvailable;
    }

    static boolean pagerMotorAvailable;
    private int scannerID;
    private int scannerType;
    TextView barcodeCount;
    public int iBarcodeCount;


    static MyAsyncTask cmdExecTask = null;
    Button btnFindScanner = null;
    List<Integer> ssaSupportedAttribs;
    DrawerLayout mDrawerLayout;
    RelativeLayout rlNavigationDeviceContainer;
    ImageView iv_batteryLevel, iv_headerImageView;
    TextView battery_percentage;
    Button btnDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mConnectedReader != null) {
            initializeView();
        } else {
            Intent intent = new Intent(this, DeviceDiscoverActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finishAffinity();
            startActivity(intent);
            finish();
        }

    }

    private void initializeView() {

        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (configuration.smallestScreenWidthDp < Application.minScreenWidth) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            if (configuration.screenWidthDp < Application.minScreenWidth) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        setContentView(R.layout.activity_active_scanner);
        ssaSupportedAttribs = new ArrayList<Integer>();
        mActiveDeviceActivity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("빠른 태그");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mActiveDeviceActivity, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();


        rlNavigationDeviceContainer = findViewById(R.id.rlNavigationDeviceContainer);
        iv_batteryLevel = (ImageView) findViewById(R.id.ivBatterylevel);
        battery_percentage = (TextView) findViewById(R.id.tvBatteryPercentage);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(v -> {
            //연결 끊기.
            RFIDBase.getInstance().ReaderDeviceDisConnected(RFIDController.mConnectedDevice);
            deviceDisconnect();

            //현재 보고 있는 페이지 새로고침
            int viewPagerCurrentItem = viewPager.getCurrentItem();
            viewPager.setAdapter(mAdapter);
            viewPager.setCurrentItem(viewPagerCurrentItem);


            mDrawerLayout.closeDrawer(GravityCompat.START);
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(mActiveDeviceActivity);
        View headerImageView = navigationView.getHeaderView(0);
        iv_headerImageView = headerImageView.findViewById(R.id.imageView);
        iv_headerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (RFIDController.BatteryData != null) {
                    deviceStatusReceived(RFIDController.BatteryData.getLevel(), RFIDController.BatteryData.getCharging(), RFIDController.BatteryData.getCause());
                }


                refreshEmptyMenuItemHeight();

                //연결 상태 확인
                if (RFIDController.mConnectedReader != null) {
                    rlNavigationDeviceContainer.setVisibility(View.VISIBLE);

                    //기기 이름
                    String strDeviceHostName = RFIDController.mConnectedReader.getHostName();
                    btnDisconnect.setText("연결 해제하기\n"+strDeviceHostName);
                }
                else {
                    rlNavigationDeviceContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                battery_percentage.setText(String.valueOf(0) + "%");
                iv_batteryLevel.setImageLevel(0);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        addDevConnectionsDelegate(mActiveDeviceActivity);
        scannerID = getIntent().getIntExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, -1);
        BaseActivity.lastConnectedScannerID = scannerID;
        String scannerName = getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME);
        String address = getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ADDRESS);
        scannerType = getIntent().getIntExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_TYPE, -1);

        picklistMode = getIntent().getIntExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.PICKLIST_MODE, 0);
        pagerMotorAvailable = getIntent().getBooleanExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.PAGER_MOTOR_STATUS, false);

        Application.currentScannerId = scannerID;
        Application.currentScannerName = scannerName;
        Application.currentScannerAddress = address;

        mRFIDBase = RFIDBase.getInstance();
        mRFIDBase.onCreate(mActiveDeviceActivity);
        viewPager = (ViewPager) findViewById(R.id.activeScannerPager);

        if (mConnectedReader != null) {
            RFD_DEVICE_MODE = mRFIDBase.getDeviceMode(mConnectedReader.getHostName(), Application.currentScannerId);
            if (RFD_DEVICE_MODE == DEVICE_STD_MODE)
                mAdapter = new ActiveDeviceStandardAdapter(this, getSupportFragmentManager(), DEVICE_STD_MODE);
            else
                mAdapter = new ActiveDevicePremiumAdapter(this, getSupportFragmentManager(), DEVICE_PREMIUM_PLUS_MODE);

            mAdapter.setDeviceModelName(mConnectedReader.getHostName());
            viewPager.setAdapter(mAdapter);
            viewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
                @Override
                public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

                    viewPager.getAdapter().notifyDataSetChanged();

                }
            });
        }

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mAdapter.setCurrentActivePosition(tab.getPosition());
                Log.d(TAG, tab.toString());
                switch (tab.getPosition()) {
                    case RFID_TAB:
                        if (mAdapter.getRFIDMode() == RFID_ACCESS_TAB) {
                            // mAdapter.notifyDataSetChanged();
                        } else if (mAdapter.getRFIDMode() == INVENTORY_TAB) {
                            // mAdapter.notifyDataSetChanged();
                            Fragment fragment = getCurrentFragment(RFID_TAB);

                            if (fragment != null && fragment instanceof RFIDInventoryFragment) {
                                //  ((RFIDInventoryFragment)fragment).onRFIDFragmentSelected();
                            }

                        } else if (mAdapter.getRFIDMode() == RAPID_READ_TAB) {
                            Fragment fragment = getCurrentFragment(RFID_TAB);
                            if (fragment != null && fragment instanceof RapidReadFragment) {
                                //       ((RapidReadFragment)fragment).onRapidReadSelected();
                            }
                        }
                        break;

                    case SETTINGS_TAB:
                        break;
                    case SCAN_TAB:
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, tab.toString());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, tab.toString());
            }
        });

        mAdapter.notifyDataSetChanged();
        iBarcodeCount = 0;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                com.codegear.mariamc_rfid.scanner.helpers.Constants.logAsMessage(TYPE_DEBUG, getClass().getSimpleName(), " Position is --- " + position);
                mAdapter.setCurrentActivePosition(position);
                switch (position) {
                    case READERS_TAB:
                        if (mAdapter.getReaderListMode() == UPDATE_FIRMWARE_TAB) {
                            getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(mAdapter.getSettingsTab())).commit();
                            getSupportFragmentManager().beginTransaction().addToBackStack(null);
                            getSupportFragmentManager().executePendingTransactions();
                            mAdapter.setReaderListMode(MAIN_HOME_SETTINGS_TAB);
                        }
                        loadNextFragment(READER_LIST_TAB);
                        break;

                    case RFID_TAB:
                        if (mAdapter.getReaderListMode() == UPDATE_FIRMWARE_TAB) {
                            getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(mAdapter.getSettingsTab())).commit();
                            getSupportFragmentManager().beginTransaction().addToBackStack(null);
                            getSupportFragmentManager().executePendingTransactions();
                            mAdapter.setReaderListMode(MAIN_HOME_SETTINGS_TAB);

                        } else {

                            if (getCurrentFragment(mAdapter.getSettingsTab()) != null) {
                                getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(mAdapter.getSettingsTab())).commit();
                                getSupportFragmentManager().beginTransaction().addToBackStack(null);
                                getSupportFragmentManager().executePendingTransactions();
                            }

                        }


                        if ((mConnectedReader != null) && mConnectedReader.getHostName().startsWith("RFD8500"))
                            setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE);

                        if (mAdapter.getRFIDMode() == RAPID_READ_TAB)
                            loadNextFragment(RAPID_READ_TAB);
                        else if (mAdapter.getRFIDMode() == INVENTORY_TAB)
                            loadNextFragment(INVENTORY_TAB);
                        else if (mAdapter.getRFIDMode() == RFID_ACCESS_TAB)
                            loadNextFragment(RFID_ACCESS_TAB);
                        else if (mAdapter.getRFIDMode() == RFID_PREFILTERS_TAB)
                            loadNextFragment(RFID_PREFILTERS_TAB);
                        else if (mAdapter.getRFIDMode() == LOCATE_TAG_TAB)
                            loadNextFragment(LOCATE_TAG_TAB);
                        else if (mAdapter.getRFIDMode() == RFID_SETTINGS_TAB)
                            loadNextFragment(RFID_SETTINGS_TAB);
                        else if (mAdapter.getRFIDMode() == NONOPER_TAB)
                            loadNextFragment(RAPID_READ_TAB);

                        break;

                    case SCAN_TAB:
                        if (RFD_DEVICE_MODE == DEVICE_PREMIUM_PLUS_MODE) {
                            if (getCurrentFragment(mAdapter.getSettingsTab()) != null) {
                                getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(mAdapter.getSettingsTab())).commit();
                                getSupportFragmentManager().beginTransaction().addToBackStack(null);
                                getSupportFragmentManager().executePendingTransactions();
                            }

                            loadNextFragment(SCAN_DATAVIEW_TAB);
                            if (mAdapter.getReaderListMode() == UPDATE_FIRMWARE_TAB) {
                                getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(mAdapter.getSettingsTab())).commit();
                                getSupportFragmentManager().beginTransaction().addToBackStack(null);
                                getSupportFragmentManager().executePendingTransactions();
                                mAdapter.setReaderListMode(MAIN_HOME_SETTINGS_TAB);
                            }
                            if ((mConnectedReader != null) && mConnectedReader.getHostName().startsWith("RFD8500"))
                                setTriggerMode(ENUM_TRIGGER_MODE.BARCODE_MODE);
                            break;

                        } else if (RFD_DEVICE_MODE == DEVICE_STD_MODE) {
                            getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(mAdapter.getSettingsTab())).commit();
                            getSupportFragmentManager().beginTransaction().addToBackStack(null);
                            getSupportFragmentManager().executePendingTransactions();

                        }
                    case SETTINGS_TAB:
                        loadNextFragment(MAIN_HOME_SETTINGS_TAB);
                        break;

                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        if (getIntent().getBooleanExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SHOW_BARCODE_VIEW, false))
            viewPager.setCurrentItem(BARCODE_TAB);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) mActiveDeviceActivity.getSystemService(ns);
        if (nMgr != null) {
            nMgr.cancel(NotificationsReceiver.DEFAULT_NOTIFICATION_ID);
        }

        //ViewPager 이동
        final int startTAB = getIntent().getIntExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.INTENT_START_TAB, RFID_TAB);
        viewPager.setCurrentItem(startTAB);
        mAdapter.setCurrentActivePosition(startTAB);

        //내부 Fragment 이동
        final int nextTAB = getIntent().getIntExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.INTENT_NEXT_TAB, -1);
        if(nextTAB != -1){
            loadNextFragment(nextTAB);
        }


        reInit();

        try {
            if (RFIDController.regionNotSet == false) {
                RFIDController.getInstance().updateReaderConnection(false);
            }
        } catch (InvalidUsageException e) {
            Log.d(TAG, "Returned SDK Exception");
        } catch (OperationFailureException e) {
            Log.d(TAG, "Returned SDK Exception");
        }
        initScanner();
        setParentActivity(this);
        createDWProfile();

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
        //로그인 했을 경우, 농장 선택화면으로 이동.
        if(UserStorage.getInstance().isLogin()){
            finishAffinity();
            Intent intent = new Intent(this, CowChronicleActivity.class);
            intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.FARM_SELECT.toString());
            startActivity(intent);
        }
        //로그인 안했으면, 로그인 페이지로 이동.
        else{
            finishAffinity();
            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
        }
    }

    private void refreshEmptyMenuItemHeight(){
        //사이즈 측정
        View vDisMainMenu = getLayoutInflater().inflate(R.layout.main_menu, null, false);
        vDisMainMenu.measure(0, 0);
        int disMainMenuHeight = vDisMainMenu.getMeasuredHeight();
        int screenHeight = PixelUtil.getScreenHeightPx(this);
        int navigationViewMenuHeight = (int) getResources().getDimension(R.dimen.drawer_navigationview_item_height);

        //하단 기기정보 사이즈 측정
        rlNavigationDeviceContainer.measure(0, 0);
        int rlNavigationDeviceContainerHeight = vDisMainMenu.getMeasuredHeight();
        ViewGroup.MarginLayoutParams rlNavigationDeviceContainerMarginLayoutParams = (ViewGroup.MarginLayoutParams) rlNavigationDeviceContainer.getLayoutParams();
        int rlNavigationDeviceContainerTotalHeight= rlNavigationDeviceContainerHeight + rlNavigationDeviceContainerMarginLayoutParams.bottomMargin;


        //기기로그인이 안되어 있다면, 기기정보 사이즈를 0으로 설정.
        if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
            rlNavigationDeviceContainerTotalHeight = 0;
        }


        //NavigationView의 특정 Menu에 사이즈 적용
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        int finalRlNavigationDeviceContainerTotalHeight = rlNavigationDeviceContainerTotalHeight;


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
                    int drawerMenuCustomItemHeight = 0;
                    if(RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()){
                        drawerMenuCustomItemHeight = screenHeight - disMainMenuHeight - finalRlNavigationDeviceContainerTotalHeight - (navigationViewMenuHeight * 5);
                    }else{
                        drawerMenuCustomItemHeight = screenHeight - disMainMenuHeight - (navigationViewMenuHeight * 5);
                    }

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
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.menu_empty);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setActionView(v);
        menuItem.setIcon(null);
        menuItem.setTitle(null);
    }

    public void initBatchRequest(View v) {
        String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_BATCH_REQUEST, inXML, null, scannerID);
    }

    public void deviceStatusReceived(final int level, final boolean charging, final String cause) {
        battery_percentage.setText(String.valueOf(level) + "%");
        iv_batteryLevel.setImageLevel(level);

    }


    @Override
    protected void onResume() {
        super.onResume();


        if (waitingForFWReboot) {
            showFwRebootdialog();

        }

        if ((onSaveInstanceState == true) && (mReaderDisappeared != null) && (RFIDController.regionNotSet == false)) {
            //loadNextFragment(MAIN_HOME_SETTINGS_TAB);
            setCurrentTabFocus(READERS_TAB);
        }
        onSaveInstanceState = false;

        mRFIDBase.activityResumed();
        if (RFIDController.regionNotSet == true) {

            Intent detailsIntent = new Intent(getApplicationContext(), SettingsDetailActivity.class);
            detailsIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            detailsIntent.putExtra(Constants.SETTING_ITEM_ID, R.id.regulatory);
            detailsIntent.putExtra(Constants.SETTING_ON_FACTORY, true);
            startActivityForResult(detailsIntent, 0);

        } else if (Application.updateReaderConnection == true) {

            try {
                RFIDController.getInstance().updateReaderConnection(false);
                Application.updateReaderConnection = false;
            } catch (InvalidUsageException e) {
                Log.d(TAG, "Returned SDK Exception");
            } catch (OperationFailureException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
        } else {

        }
    }


    public void showFwRebootdialog() {
        String fwStatus;
        dialogFwRebooting = new Dialog(mActiveDeviceActivity);
        dialogFwRebooting.setContentView(R.layout.dialog_fw_rebooting);

        TextView Status = (TextView) dialogFwRebooting.findViewById(R.id.fwstatus);
        if (Application.isFirmwareUpdateSuccess == true) {
            fwStatus = "펌웨어 업데이트 성공";  //Toast.makeText(this, "Firmware update Success", Toast.LENGTH_SHORT).show();
        } else {
            fwStatus = "펌웨어 업데이트 실패";  //Toast.makeText(this, "Firmware update Failed", Toast.LENGTH_SHORT).show();
            Status.setTextColor(Color.RED);
        }

        //dialogFwRebooting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        TextView counter = (TextView) dialogFwRebooting.findViewById(R.id.counter);
        Status.setText(fwStatus + Status.getText());


        dotProgressBar = (DotsProgressBar) dialogFwRebooting.findViewById(R.id.progressBar);
        dotProgressBar.setDotsCount(6);


        dialogFwRebooting.setCancelable(false);
        dialogFwRebooting.setCanceledOnTouchOutside(false);
        dialogFwRebooting.show();
    }



    public void reInit() {

        removeDevEventsDelegate(this);
        addDevEventsDelegate(this);
        removeDevConnectiosDelegate(this);
        addDevConnectionsDelegate(this);
        //addMissedBarcodes();

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        if (nMgr != null) {
            nMgr.cancel(NotificationsReceiver.DEFAULT_NOTIFICATION_ID);
        }

        scannerID = Application.currentConnectedScannerID;
        mRFIDBase.reInit(this);
        //SetTunnelMode(null);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            nfcData = ((Application) getApplication()).processNFCData(intent);
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))
            processTAGData(intent);

    }

    private void processTAGData(Intent intent) {
        Log.d(TAG, "ProcessTAG data ");

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeDevEventsDelegate(this);
        removeDevConnectiosDelegate(this);
        if (mRFIDBase != null) {
            mRFIDBase.onDestroy();
        }
    }

    //앱 초기화
    private void minimizeApp() {
        //설정앱에선 초기화 였지만, 카우크로니클과 합쳐진 형태에선 초기화를 로그인화면으로 보내는 걸로 해야 로직상 깔끔함.
        if(!UserStorage.getInstance().isLogin()){
            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
        }else{
            //로그인을 했다면, 카우크로니클 화면으로 이동. SingleTask 여서, 마지막 화면으로 이동됨.
            Intent intent = new Intent(this, CowChronicleActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        int position = getCurrentTabPosition();
        Fragment fragment = getCurrentFragment(position);
        switch (position) {
            case READERS_TAB:
                if (fragment instanceof PairOperationsFragment || fragment instanceof ReaderDetailsFragment || fragment instanceof ReaderWifiSettingsFragment) {
                    loadNextFragment(READER_LIST_TAB);
                } else {
                    minimizeApp();
                }
                break;
            case RFID_TAB:
                if (fragment instanceof PreFilterFragment) {
                    ((PreFilterFragment) fragment).onBackPressed();
                    loadNextFragment(INVENTORY_TAB);

                } else if (fragment instanceof LocateOperationsFragment || fragment instanceof AccessOperationsFragment) {
                    loadNextFragment(RAPID_READ_TAB);

                } else {
                    minimizeApp();
                }
                break;
            case SCAN_TAB:
                if (RFD_DEVICE_MODE == DEVICE_PREMIUM_PLUS_MODE) {
                    minimizeApp();
                }
                break;
            case SETTINGS_TAB:
                if (fragment != null && fragment instanceof BackPressedFragment) {
                    ((BackPressedFragment) fragment).onBackPressed();
                }
                if (fragment instanceof ManagerFragment) {
                    loadNextFragment(MAIN_HOME_SETTINGS_TAB);

                } else if (fragment instanceof SettingListFragment) {
                    loadNextFragment(MAIN_HOME_SETTINGS_TAB);

                } else if (fragment instanceof BeeperActionsFragment) {
                    loadNextFragment(SCAN_SETTINGS_TAB);
                } else if (fragment instanceof SymbologiesFragment) {
                    loadNextFragment(SCAN_SETTINGS_TAB);
                } else if (fragment instanceof ScanHomeSettingsFragment) {
                    loadNextFragment(MAIN_HOME_SETTINGS_TAB);
                } else if (fragment instanceof SettingsFragment) {
                    loadNextFragment(MAIN_HOME_SETTINGS_TAB);
                } else if (fragment instanceof AdvancedFragment) {
                    loadNextFragment(MAIN_HOME_SETTINGS_TAB);
                } else if ((fragment instanceof AdvancedOptionItemFragment) || (fragment instanceof ProfileFragment) || (fragment instanceof LedFragment)) {
                    loadNextFragment(MAIN_RFID_SETTINGS_TAB);
                    //RFID_ADVANCED_OPTIONS_TAB
                } else if (fragment instanceof BeeperFragment) {

                } else if (fragment instanceof RegulatorySettingsFragment) {

                } else if (fragment instanceof WifiFragment) {

                } else if (fragment instanceof ChargeTerminalFragment) {

                } else if ((fragment instanceof AntennaSettingsFragment)) {
                    loadNextFragment(RFID_ADVANCED_OPTIONS_TAB);

                } else if (fragment instanceof StartStopTriggersFragment) {

                } else if (fragment instanceof SingulationControlFragment) {
                    //loadNextFragment(RFID_ADVANCED_OPTIONS_TAB);

                } else if (fragment instanceof DPOSettingsFragment) {

                } else if (fragment instanceof SaveConfigurationsFragment) {

                } else if (fragment instanceof TagReportingFragment) {

                } else if (fragment instanceof FactoryResetFragment) {
                    loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
                } else if (fragment instanceof LoggerFragment) {
                    loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
                } else if (fragment instanceof ApplicationSettingsFragment) {
                    loadNextFragment(MAIN_HOME_SETTINGS_TAB);
                } else if (fragment instanceof KeyRemapFragment) {
                    loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
                } else if (fragment instanceof UpdateFirmware) {
                    loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
                } else if (fragment instanceof AssertFragment) {
                    loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
                } else if (fragment instanceof Static_ipconfig) {
                    loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
                } else if (fragment instanceof BatteryStatsFragment) {
                    loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
                } else if (fragment instanceof BatteryFragment) {
                    loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
                } else if (fragment instanceof UsbMiFiFragment) {

                } else {
                    minimizeApp();
                }

                break;

        }
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        com.codegear.mariamc_rfid.scanner.helpers.Constants.logAsMessage(TYPE_DEBUG, getClass().getSimpleName(), "onTabSelected() Position is --- " + tab.getPosition());
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    public void startFirmware(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_START_NEW_FIRMWARE, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void abortFirmware(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_ABORT_UPDATE_FIRMWARE, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void loadLedActions(View view) {
        Intent intent = new Intent(this, LEDActivity.class);
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, scannerID);
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME, getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME));
        startActivity(intent);
    }

    public void loadBeeperActions(View view) {
        loadNextFragment(BEEPER_ACTION_TAB);
    }

    public void beeperAction(View view) {
        int position = getCurrentTabPosition();
        Fragment fragment = getCurrentFragment(position);
        if (fragment instanceof BeeperActionsFragment)
            ((BeeperActionsFragment) fragment).beeperAction(view);
    }

    public void factoryResetClicked(View view) {
        loadNextFragment(FACTORY_RESET_FRAGMENT_TAB);
    }

    public void enableLoggingClicked(View view) {
        if ((RFIDController.mConnectedReader != null) && RFIDController.mConnectedReader.getHostName().startsWith("MC33")) {
            Toast.makeText(this, "MC33는 실시간 로그가 지원되지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        loadNextFragment(LOGGER_FRAGMENT_TAB);
        return;
    }

    public void generalSettingsClicked(View view) {
        loadNextFragment(MAIN_GENERAL_SETTINGS_TAB);
    }

    public void showRFIDSettings(View view) {
        loadNextFragment(RFID_SETTINGS_TAB);
    }

    public void scanSettingsClicked(View view) {

        loadNextFragment(SCAN_SETTINGS_TAB);
    }

    public void applicationSettingsClicked(View view) {
        loadNextFragment(APPLICATION_SETTINGS_TAB);
        return;
    }


    public void deviceResetClicked(View view) {
        loadNextFragment(DEVICE_RESET_TAB);
    }

    public void showDeviceInfoClicked(View view) {
        loadNextFragment(ASSERT_DEVICE_INFO_TAB);

    }

    public void staticIpConfig(View view) {
        loadNextFragment(STATIC_IP_CONFIG);
    }

    public void keyRemapClicked(View view) {
        if (RFIDController.mConnectedReader != null) {
            if (RFIDController.mConnectedReader.getHostName().startsWith("RFD40") || RFIDController.mConnectedReader.getHostName().startsWith("RFD90")) {
                loadNextFragment(KEYREMAP_TAB);
            } else {
                view.setEnabled(false);
                Toast.makeText(this, " " + mConnectedReader.getHostName()+"는 트리거 매핑 기능을 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "장치가 연결되지 않은 경우 트리거 매핑 기능이 지원되지 않습니다.", Toast.LENGTH_LONG).show();
        }
    }


    public void loadAssert(View view) {
        loadNextFragment(ASSERT_DEVICE_INFO_TAB);

    }

    public void symbologiesClicked(View view) {
        loadNextFragment(BARCODE_SYMBOLOGIES_TAB);
    }

    public void enableScanning(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_SCAN_ENABLE, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void disableScanning(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_SCAN_DISABLE, null);
        cmdExecTask.execute(new String[]{in_xml});
    }


    public void aimOn(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_AIM_ON, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void aimOff(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_AIM_OFF, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void vibrationFeedback(View view) {

        Intent intent = new Intent(this, VibrationFeedback.class);
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, scannerID);
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME, getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME));
        startActivity(intent);

    }

    public void setTriggerMode(ENUM_TRIGGER_MODE triggerMode) {

        mRFIDBase.setTriggerMode(triggerMode);

    }

    public void pullTrigger(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_PULL_TRIGGER, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void releaseTrigger(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_RELEASE_TRIGGER, null);
        cmdExecTask.execute(new String[]{in_xml});

    }

    public void SetTunnelMode(View view) {
        String inXML = "<inArgs><scannerID>" + Application.currentConnectedScannerID + "</scannerID><cmdArgs><arg-int>" + 18 + "</arg-int></cmdArgs></inArgs>";
        StringBuilder outXML = new StringBuilder();
        executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_SET_ACTION, inXML, outXML, Application.currentConnectedScannerID);
    }

    public int getPickListMode() {
        //String in_xml = "<inArgs><scannerID>" + Application.currentConnectedScannerID + "</scannerID><cmdArgs><arg-xml><attrib_list>402</attrib_list></arg-xml></cmdArgs></inArgs>";
        int attrVal = 0;
        String in_xml = "<inArgs><scannerID>" + Application.currentConnectedScannerID + "</scannerID><cmdArgs><arg-xml><attrib_list>402</attrib_list></arg-xml></cmdArgs></inArgs>";
        StringBuilder outXML = new StringBuilder();
        executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GET, in_xml, outXML, Application.currentConnectedScannerID);

        try {
            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(new StringReader(outXML.toString()));
            int event = parser.getEventType();
            String text = null;
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("value")) {
                            attrVal = Integer.parseInt(text != null ? text.trim() : null);
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return attrVal;
    }


    public int getScannerID() {
        return scannerID;
    }

    private void addMissedBarcodes() {
        if (barcodeQueue.size() != iBarcodeCount) {

            for (int i = iBarcodeCount; i < barcodeQueue.size(); i++) {
                scannerBarcodeEvent(barcodeQueue.get(i).getBarcodeData(), barcodeQueue.get(i).getBarcodeType(), barcodeQueue.get(i).getFromScannerID());
            }
        }
    }

    @Override
    public synchronized void scannerBarcodeEvent(byte[] barcodeData, int barcodeType, int scannerID) {


        if (viewPager.getCurrentItem() != BARCODE_TAB) {
            Log.d(TAG, "Cached barcode Data");
            return;
        }

        Log.d(TAG, "Rendering barcode Data");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BarcodeFragment barcodeFragment = (BarcodeFragment) mAdapter.getRegisteredFragment(BARCODE_TAB);
                if (barcodeFragment != null) {

                    barcodeFragment.showBarCode();

                    barcodeCount = (TextView) findViewById(R.id.barcodesListCount);
                    barcodeCount.setText("스캔된 바코드: " + Integer.toString(++iBarcodeCount));
                    if (iBarcodeCount > 0) {
                        Button btnClear = (Button) findViewById(R.id.btnClearList);
                        btnClear.setEnabled(true);
                    }
                    if (!Application.isFirmwareUpdateInProgress) {
                        //viewPager.setCurrentItem(BARCODE_TAB);
                    }

                }
            }
        });

    }

    @Override
    public void scannerFirmwareUpdateEvent(FirmwareUpdateEvent firmwareUpdateEvent) {
        int setTab = RFD_DEVICE_MODE == DEVICE_STD_MODE ? SCAN_TAB : SETTINGS_TAB;
        Fragment fragment = getCurrentFragment(setTab);
        if (fragment instanceof UpdateFirmware)
            ((UpdateFirmware) fragment).scannerFirmwareUpdateEvent(firmwareUpdateEvent);
    }

    @Override
    public void scannerImageEvent(byte[] imageData) {
    }

    @Override
    public void scannerVideoEvent(byte[] videoData) {
    }

    public void clearList(View view) {
        BarcodeFragment barcodeFragment = (BarcodeFragment) mAdapter.getRegisteredFragment(BARCODE_TAB);
        if (barcodeFragment != null) {
            barcodeFragment.clearList();
            barcodeCount = (TextView) findViewById(R.id.barcodesListCount);
            iBarcodeCount = 0;
            barcodeCount.setText("스캔된 바코드: " + Integer.toString(iBarcodeCount));
            Button btnClear = (Button) findViewById(R.id.btnClearList);
            btnClear.setEnabled(false);
        }
    }

    public void scanTrigger(View view) {
        String in_xml = "<inArgs><scannerID>" + Application.currentConnectedScannerID + "</scannerID></inArgs>";
        cmdExecTask = new MyAsyncTask(Application.currentConnectedScannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_PULL_TRIGGER, null);
        cmdExecTask.execute(new String[]{in_xml});
    }

    /**
     * Navigate to Scale view
     *
     * @param view
     */
    public void loadScale(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        new AsyncTaskScaleAvailable(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_READ_WEIGHT, this, ScaleActivity.class).execute(new String[]{in_xml});

    }

    public void locationingButtonClicked(final View v) {
        mRFIDBase.locationingButtonClicked(v);
    }

    public void accessOperationsReadClicked(View v) {
        mRFIDBase.accessOperationsReadClicked(v);
    }

    public void accessOperationLockClicked(View v) {
        mRFIDBase.accessOperationLockClicked(v);
    }

    public void accessOperationsWriteClicked(View v) {
        mRFIDBase.accessOperationsWriteClicked(v);
    }

    public void accessOperationsKillClicked(View v) {
        mRFIDBase.accessOperationsKillClicked(v);
    }

    public synchronized void locationingButtonClicked(FloatingActionButton btn_locate) {
        mRFIDBase.locationingButtonClicked(btn_locate);
    }

    public synchronized void multiTagLocateStartOrStop(View view) {
        mRFIDBase.multiTagLocateStartOrStop(view);
    }

    public synchronized void multiTagLocateAddTagItem(View view) {
        mRFIDBase.multiTagLocateAddTagItem(view);
    }

    public synchronized void multiTagLocateDeleteTagItem(View view) {
        mRFIDBase.multiTagLocateDeleteTagItem(view);
    }

    public synchronized void multiTagLocateReset(View view) {
        mRFIDBase.multiTagLocateReset(view);
    }

    public synchronized void multiTagLocateClearTagItems(View view) {
        mRFIDBase.multiTagLocateClearTagItems(view);
    }

    public void showBatteryStatsClicked(View view) {
        loadNextFragment(BATTERY_STATISTICS_TAB);
    }

    public void showBatteryStats() {
        viewPager.setCurrentItem(mAdapter.getSettingsTab());
        loadNextFragment(BATTERY_STATISTICS_TAB);
    }


    public void callBackPressed() {
        mRFIDBase.callBackPressed();
    }

    public void selectItem(int i) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            finish();

        }
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_cowchronicle:
                if(UserStorage.getInstance().isLogin()){
                    finishAffinity();
                    Intent intent = new Intent(this, CowChronicleActivity.class);
                    intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.WEBVIEW.toString());
                    startActivity(intent);
                }
                else{
                    finishAffinity();
                    Intent intent = new Intent(this, UserLoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.menu_readers:
                if(UserStorage.getInstance().isLogin()){
                    finishAffinity();
                    Intent intent = new Intent(this, CowChronicleActivity.class);
                    intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.FARM_SELECT.toString());
                    startActivity(intent);
                }
                else{
                    finishAffinity();
                    Intent intent = new Intent(this, UserLoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_user_info:
                if(UserStorage.getInstance().isLogin()){
                    finishAffinity();
                    Intent intent = new Intent(this, CowChronicleActivity.class);
                    intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.USER_INFO.toString());
                    startActivity(intent);
                }
                else{
                    finishAffinity();
                    Intent intent = new Intent(this, UserLoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_battery_statics:
                if (mConnectedReader != null && mConnectedReader.isConnected()) {
                    showBatteryStats();
                } else {
                    Toast.makeText(this, "연결된 장치가 없습니다. ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_fw_update:
                if (mConnectedReader != null && mConnectedReader.isConnected()) {
                    loadUpdateFirmware(MenuItemCompat.getActionView(item));
                } else {
                    Toast.makeText(this, "연결된 장치가 없습니다. ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_connection_help:
                Intent helpIntent = new Intent(this, NavigationHelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.nav_settings:
                viewPager.setCurrentItem(mAdapter.getSettingsTab());
                loadNextFragment(MAIN_HOME_SETTINGS_TAB);
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        drawer.setSelected(true);
        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public int getCurrentTabPosition() {
        return mAdapter.getCurrentActivePosition();
    }


    @Override
    protected void onStop() {
        super.onStop();
        onSaveInstanceState = true;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("default_tab", "readers");
        onSaveInstanceState = true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (waitingForFWReboot == false) {
            //viewPager.setCurrentItem(READERS_TAB);
            setCurrentTabFocus(READERS_TAB);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
        }
        onSaveInstanceState = false;

    }


    public void setCurrentTabFocus(int pos) {
        if (onSaveInstanceState == false) {
            //loadNextFragment(MAIN_HOME_SETTINGS_TAB);
            if (mAdapter.getCurrentActivePosition() != pos) viewPager.setCurrentItem(pos);
        }

    }

    public void setCurrentTabFocus(int pos, int fragment) {
        if (!onSaveInstanceState) {
            if (mAdapter.getCurrentActivePosition() != pos) {
                viewPager.setCurrentItem(pos);
                loadNextFragment(fragment);
            }
        }

    }

    public void sendNotification(String actionReaderStatusObtained, String info) {
        mRFIDBase.sendNotification(actionReaderStatusObtained, info);
    }

    public void loadWifiReaderSettings() {
        if (mConnectedReader != null && mConnectedReader.isConnected())
            loadNextFragment(READER_WIFI_SETTINGS_TAB);

    }

    public void loadScanSettings(View view) {
        loadNextFragment(SCAN_SETTINGS_TAB);
    }

    public void loadScanAdvancedSettings(View view) {
        loadNextFragment(SCAN_ADVANCED_TAB);
    }

    @Override
    public void OnAdvancedListFragmentInteractionListener(AdvancedOptionsContent.SettingItem item) {
        Fragment fragment = null;
        int settingItemSelected = Integer.parseInt(item.id);
        //Show the selected item
        switch (settingItemSelected) {
            case R.id.antenna:
                loadNextFragment(ANTENNA_SETTINGS_TAB);
                break;
            case R.id.singulation_control:
                loadNextFragment(SINGULATION_CONTROL_TAB);
                break;
            case R.id.start_stop_triggers:
                loadNextFragment(START_STOP_TRIGGER_TAB);
                break;
            case R.id.tag_reporting:
                loadNextFragment(TAG_REPORTING_TAB);
                break;
            case R.id.save_configuration:
                loadNextFragment((SAVE_CONFIG_TAB));
                break;
            case R.id.power_management:
                loadNextFragment(DPO_SETTING_TAB);
                break;
        }
        setTitle(item.content);
    }


    public void timerDelayRemoveDialog(long time, final Dialog d, final String command, final boolean isPressBack) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (d != null && d.isShowing()) {
                    sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, command + " timeout");
                    d.dismiss();
                    if (ActiveDeviceActivity.isActivityVisible() && isPressBack) callBackPressed();
                }
            }
        }, time);
    }

    /**
     * Method called when save config button is clicked
     *
     * @param v - View to be addressed
     */
    public void saveConfigClicked(View v) {
        if (mConnectedReader != null && mConnectedReader.isConnected()) {
            progressDialog = new CustomProgressDialog(this, getString(R.string.save_config_progress_title));
            progressDialog.show();
            timerDelayRemoveDialog(Constants.SAVE_CONFIG_RESPONSE_TIMEOUT, progressDialog, getString(R.string.status_failure_message), false);
            new AsyncTask<Void, Void, Boolean>() {
                private OperationFailureException operationFailureException;

                @Override
                protected Boolean doInBackground(Void... voids) {
                    boolean bResult = false;
                    try {
                        mConnectedReader.Config.saveConfig();
                        bResult = true;
                    } catch (InvalidUsageException e) {
                        if (e != null && e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                    } catch (OperationFailureException e) {
                        if (e != null && e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                        operationFailureException = e;
                    }
                    return bResult;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    progressDialog.dismiss();
                    if (!result) {
                        Toast.makeText(getApplicationContext(), operationFailureException.getVendorMessage(), Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.status_success_message), Toast.LENGTH_SHORT).show();
                }
            }.execute();
        } else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_disconnected), Toast.LENGTH_SHORT).show();

    }

    public void changeAdapter(int tabCount) {
        if (tabCount == DEVICE_PREMIUM_PLUS_MODE)
            mAdapter = new ActiveDevicePremiumAdapter(this, getSupportFragmentManager(), DEVICE_PREMIUM_PLUS_MODE);
        else
            mAdapter = new ActiveDeviceStandardAdapter(this, getSupportFragmentManager(), DEVICE_STD_MODE);

        RFD_DEVICE_MODE = tabCount;
        viewPager.setAdapter(mAdapter);
        //viewPager.getAdapter().notifyDataSetChanged();

    }

    public void onFactoryReset(ReaderDevice readerDevice) {
        mRFIDBase.onFactoryReset(readerDevice);
    }

    @Override
    public void createFile1(Uri uri) {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/csv");
        intent.putExtra(Intent.EXTRA_TITLE, getFilename());
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);

        exportresultLauncher.launch(intent);
    }

    private String getFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS");
        return "RFID" + "_" + sdf.format(new Date()) + ".csv";
    }

    public ActiveDeviceAdapter getDeviceAdapter() {
        return mAdapter;
    }


    /**
     * scale availability check
     */
    private class AsyncTaskScaleAvailable extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        Context context;
        Class targetClass;
        private com.codegear.mariamc_rfid.scanner.helpers.CustomProgressDialog progressDialog;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;

        public AsyncTaskScaleAvailable(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, Context context, Class targetClass) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.context = context;
            this.targetClass = targetClass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new com.codegear.mariamc_rfid.scanner.helpers.CustomProgressDialog(ActiveDeviceActivity.this, "잠시만 기다려주세요...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            boolean result = executeCommand(opcode, strings[0], sb, scannerId);
            if (opcode == DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_READ_WEIGHT) {
                if (result) {
                    return true;
                }
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean scaleAvailability) {
            super.onPostExecute(scaleAvailability);
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

            Intent intent = new Intent(context, targetClass);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, scannerID);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME, getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME));
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCALE_STATUS, scaleAvailability);
            startActivity(intent);
        }


    }

    public void updateBarcodeCount() {
        if (barcodeQueue.size() != iBarcodeCount) {
            barcodeCount = (TextView) findViewById(R.id.barcodesListCount);
            iBarcodeCount = barcodeQueue.size();
            barcodeCount.setText("스캔된 바코드: " + Integer.toString(iBarcodeCount));
            if (iBarcodeCount > 0) {
                Button btnClear = (Button) findViewById(R.id.btnClearList);
                btnClear.setEnabled(true);
            }
        }

    }

    @Override
    public boolean scannerHasAppeared(int scannerID) {
        return false;
    }

    @Override
    public boolean scannerHasDisappeared(int scannerID) {
        if (null != cmdExecTask) {
            cmdExecTask.cancel(true);
        }
        barcodeQueue.clear();
        return true;
    }

    @Override
    public boolean scannerHasConnected(int scannerID) {
        barcodeQueue.clear();

        return false;
    }

    @Override
    public boolean scannerHasDisconnected(int scannerID) {
        barcodeQueue.clear();
        return true;
    }


    public void setPickListMode(int picklistInt) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-xml><attrib_list><attribute><id>" + 402 + "</id><datatype>B</datatype><value>" + picklistInt + "</value></attribute></attrib_list></arg-xml></cmdArgs></inArgs>";
        StringBuilder outXML = new StringBuilder();
        cmdExecTask = new MyAsyncTask(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_SET, outXML);
        cmdExecTask.execute(new String[]{in_xml});
    }

    public void updateFirmware(View view) {

        int setTab = RFD_DEVICE_MODE == DEVICE_STD_MODE ? SCAN_TAB : SETTINGS_TAB;
        Fragment fragment = getCurrentFragment(setTab);
        if (fragment instanceof UpdateFirmware) ((UpdateFirmware) fragment).updateFirmware(view);

    }

    /**
     * select firmware file
     *
     * @param view user interface
     */
    public void selectFirmware(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
        intent.putExtra("DocumentsContract.EXTRA_INITIAL_URI", uri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri documentUri;
                if (data != null) {
                    if (data.getData().toString().contains("content://com.android.providers")) {
                        runOnUiThread(this::ShowPlugInPathChangeDialog);
                    } else {
                        int setTab = RFD_DEVICE_MODE == DEVICE_STD_MODE ? SCAN_TAB : SETTINGS_TAB;
                        Fragment fragment = getCurrentFragment(setTab);
                        if (fragment instanceof UpdateFirmware) {
                            documentUri = data.getData();
                            //((UpdateFirmware) fragment).selectedFile(data.getData());
                            ((UpdateFirmware) fragment).selectedFile(documentUri);
                        }
                    }
                }
            }
        }

        private void ShowPlugInPathChangeDialog() {
            if (!isFinishing()) {
                final Dialog dialog = new Dialog(ActiveDeviceActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_plugin_path_change);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                TextView declineButton = (TextView) dialog.findViewById(R.id.btn_ok);
                declineButton.setOnClickListener(v -> dialog.dismiss());
            }
        }
    });


    ActivityResultLauncher<Intent> exportresultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri uri = data.getData();
                if (data != null) {
                    mRFIDBase.exportData(uri);
                }
            }
        }

    });


    public void loadUpdateFirmware(View view) {
        int tab = RFD_DEVICE_MODE == DEVICE_STD_MODE ? SCAN_TAB : SETTINGS_TAB;
        setCurrentTabFocus(tab);
        loadNextFragment(UPDATE_FIRMWARE_TAB);

    }

    public void loadReaderDetails(ReaderDevice readerDevice) {
        connectedReaderDetails(readerDevice);
        loadNextFragment(READER_DETAILS_TAB);
    }

    private void connectedReaderDetails(ReaderDevice readerDevice) {
        mConnectedReaderDetails = readerDevice;
    }

    public ReaderDevice connectedReaderDetails() {
        return mConnectedReaderDetails;
    }

    public void ImageVideo(View view) {
        if (scannerType != 2) {
            String message = "Video feature not supported in bluetooth scanners.";
            alertShow(message, false);
        } else {
            loadImageVideo();
        }
    }

    private void alertShow(String message, boolean error) {

        if (error) {
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ActiveDeviceActivity.this);
            dialog.setTitle("Video not supported").setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialoginterface, int i) {
                    loadImageVideo();
                }
            }).show();
        }
    }

    private void loadImageVideo() {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, scannerID);
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME, getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME));
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_TYPE, scannerType);
        startActivity(intent);
    }

    public void loadIdc(View view) {
        Intent intent = new Intent(this, IntelligentImageCaptureActivity.class);
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, scannerID);
        intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME, getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME));
        startActivity(intent);
    }

    public void loadBatteryStatistics(View view) {
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
        new AsyncTaskBatteryAvailable(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GETALL, this, BatteryStatistics.class).execute(new String[]{in_xml});

    }

    /**
     * Navigate to Scan Speed Analytics views
     *
     * @param view
     */
    public void loadScanSpeedAnalytics(View view) {
        // Scan speed analytics symbology type has set
        if (SsaSetSymbologyActivity.SSA_SYMBOLOGY_ENABLED_FLAG) {
            // navigate to scan speed analytics view
            Intent intent = new Intent(this, ScanSpeedAnalyticsActivity.class);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, scannerID);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SYMBOLOGY_SSA_ENABLED, SsaSetSymbologyActivity.SSA_ENABLED_SYMBOLOGY_OBJECT);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME, getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            int ssaStatus = 0;
            if (scannerType != 1) {
                ssaStatus = 2;
            }
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SSA_STATUS, ssaStatus);

            getApplicationContext().startActivity(intent);

        } else { // Scan speed analytics symbology type has not set
            // navigate to Scan speed analytics set view
            String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID></inArgs>";
            new AsyncTaskSSASvailable(scannerID, DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GETALL, this, SsaSetSymbologyActivity.class).execute(new String[]{in_xml});
        }
    }

    private class AsyncTaskBatteryAvailable extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        Context context;
        Class targetClass;
        private com.codegear.mariamc_rfid.scanner.helpers.CustomProgressDialog progressDialog;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;

        public AsyncTaskBatteryAvailable(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, Context context, Class targetClass) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.context = context;
            this.targetClass = targetClass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new com.codegear.mariamc_rfid.scanner.helpers.CustomProgressDialog(ActiveDeviceActivity.this, "잠시만 기다려주세요...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            boolean result = executeCommand(opcode, strings[0], sb, scannerId);
            if (opcode == DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GETALL) {
                if (result) {
                    try {
                        int i = 0;
                        XmlPullParser parser = Xml.newPullParser();

                        parser.setInput(new StringReader(sb.toString()));
                        int event = parser.getEventType();
                        String text = null;
                        while (event != XmlPullParser.END_DOCUMENT) {
                            String name = parser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    break;
                                case XmlPullParser.TEXT:
                                    text = parser.getText();
                                    break;

                                case XmlPullParser.END_TAG:
                                    if (name.equals("attribute")) {
                                        if (text != null && text.trim().equals("30018")) {
                                            return true;
                                        }
                                    }
                                    break;
                            }
                            event = parser.next();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();

            Intent intent = new Intent(context, targetClass);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, scannerID);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME, getIntent().getStringExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_NAME));
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.BATTERY_STATUS, b);
            startActivity(intent);
        }


    }

    private class AsyncTaskSSASvailable extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        Context context;
        Class targetClass;
        private com.codegear.mariamc_rfid.scanner.helpers.CustomProgressDialog progressDialog;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;

        public AsyncTaskSSASvailable(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, Context context, Class targetClass) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.context = context;
            this.targetClass = targetClass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new com.codegear.mariamc_rfid.scanner.helpers.CustomProgressDialog(ActiveDeviceActivity.this, "잠시만 기다려주세요...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            boolean result = executeCommand(opcode, strings[0], sb, scannerId);
            if (opcode == DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GETALL) {
                if (result) {
                    try {
                        int i = 0;
                        XmlPullParser parser = Xml.newPullParser();

                        parser.setInput(new StringReader(sb.toString()));
                        int event = parser.getEventType();
                        String text = null;
                        ssaSupportedAttribs = new ArrayList<Integer>();
                        while (event != XmlPullParser.END_DOCUMENT) {
                            String name = parser.getName();
                            switch (event) {
                                case XmlPullParser.START_TAG:
                                    break;
                                case XmlPullParser.TEXT:
                                    text = parser.getText();
                                    break;
                                case XmlPullParser.END_TAG:
                                    if (name.equals("attribute")) {
                                        if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_UPC))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_UPC);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_EAN_JAN))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_EAN_JAN);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_2_OF_5))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_2_OF_5);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODEBAR))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODEBAR);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_11))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_11);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_128))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_128);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_39))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_39);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_CODE_93))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_CODE_93);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_COMPOSITE))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_COMPOSITE);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_DATABAR))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_DATABAR);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_MSI))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_MSI);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_DATAMARIX))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_DATAMARIX);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_PDF))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_PDF);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_POSTAL_CODES))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_POSTAL_CODES);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_QR))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_QR);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_AZTEC))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_AZTEC);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_OCR))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_OCR);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_MAXICODE))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_MAXICODE);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_DATAMATRIX))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_DATAMATRIX);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_GS1_QR_CODE))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_GS1_QR_CODE);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_COUPON))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_COUPON);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_UPC))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_UPC);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_EAN_JAN))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_EAN_JAN);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_DIGIMARC_OTHER))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_DIGIMARC_OTHER);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER_1D))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER_1D);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER_2D))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER_2D);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_OTHER))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_OTHER);
                                            result = true;
                                        } else if (text != null && text.trim().equals(Integer.toString(RMD_ATTR_VALUE_SSA_HISTOGRAM_UNUSED_ID))) {
                                            ssaSupportedAttribs.add(RMD_ATTR_VALUE_SSA_DECODE_COUNT_UNUSED_ID);
                                            result = true;
                                        }
                                    }
                                    break;
                            }
                            event = parser.next();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            SharedPreferences settings = getSharedPreferences(com.codegear.mariamc_rfid.scanner.helpers.Constants.PREFS_NAME, 0);
            int ssaStatus = 0;
            if (ssaSupportedAttribs.size() == 0) {
                ssaStatus = 1;
            } else if (scannerType != 1) {
                ssaStatus = 2;
            }

            Intent intent = new Intent(context, targetClass);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SCANNER_ID, scannerID);
            intent.putIntegerArrayListExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SYMBOLOGY_SSA, (ArrayList<Integer>) ssaSupportedAttribs);
            intent.putExtra(com.codegear.mariamc_rfid.scanner.helpers.Constants.SSA_STATUS, ssaStatus);
            startActivity(intent);
        }
    }

    /**
     * method to send connect command request to reader
     * after connect button clicked on connect password pairTaskDailog
     *
     * @param password     - reader password
     * @param readerDevice
     */
    public void connectClicked(String password, ReaderDevice readerDevice) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
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
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_RFID_FRAGMENT);
        if(fragment == null){
            fragment = getCurrentFragment(viewPager.getCurrentItem());
        }
        if (fragment instanceof RFIDReadersListFragment) {
            ((RFIDReadersListFragment) fragment).readerDisconnected(readerDevice, true);
        }
    }


    public void findScanner(View view) {
        btnFindScanner = (Button) findViewById(R.id.btn_find_scanner);
        if (btnFindScanner != null) {
            btnFindScanner.setEnabled(false);
        }
        new FindScannerTask(scannerID).execute();
    }


    private class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        int scannerId;
        StringBuilder outXML;
        DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode;
        private com.codegear.mariamc_rfid.scanner.helpers.CustomProgressDialog progressDialog;

        public MyAsyncTask(int scannerId, DCSSDKDefs.DCSSDK_COMMAND_OPCODE opcode, StringBuilder outXML) {
            this.scannerId = scannerId;
            this.opcode = opcode;
            this.outXML = outXML;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new com.codegear.mariamc_rfid.scanner.helpers.CustomProgressDialog(ActiveDeviceActivity.this, "명령어 실행중...");
            progressDialog.show();
        }


        @Override
        protected Boolean doInBackground(String... strings) {
            return executeCommand(opcode, strings[0], outXML, scannerId);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            if (!b) {
                Toast.makeText(ActiveDeviceActivity.this, "작업을 수행할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class FindScannerTask extends AsyncTask<String, Integer, Boolean> {
        int scannerId;

        public FindScannerTask(int scannerId) {
            this.scannerId = scannerId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Boolean doInBackground(String... strings) {

            long t0 = System.currentTimeMillis();

            TurnOnLEDPattern();
            BeepScanner();
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            while (System.currentTimeMillis() - t0 < 3000) {
                VibrateScanner();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    if (e != null && e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                }
                VibrateScanner();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    if (e != null && e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                }
                BeepScanner();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    if (e != null && e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                }
                VibrateScanner();
            }
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            TurnOffLEDPattern();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (btnFindScanner != null) {
                btnFindScanner.setEnabled(true);
            }

        }


        private void TurnOnLEDPattern() {
            String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-int>" + 88 + "</arg-int></cmdArgs></inArgs>";
            StringBuilder outXML = new StringBuilder();
            executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_SET_ACTION, inXML, outXML, scannerID);
        }

        private void TurnOffLEDPattern() {
            String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-int>" + 90 + "</arg-int></cmdArgs></inArgs>";
            StringBuilder outXML = new StringBuilder();
            executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_SET_ACTION, inXML, outXML, scannerID);
        }

        private void VibrateScanner() {
            String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs>";
            StringBuilder outXML = new StringBuilder();
            executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_VIBRATION_FEEDBACK, inXML, outXML, scannerID);
        }

        private void BeepScanner() {
            String inXML = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-int>" + RMD_ATTR_VALUE_ACTION_HIGH_HIGH_LOW_LOW_BEEP + "</arg-int></cmdArgs></inArgs>";
            StringBuilder outXML = new StringBuilder();
            executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_SET_ACTION, inXML, outXML, scannerID);
        }

    }


    /////////////////////   RFID  Functions  ///////////////

    private void PrepareMatchModeList() {
        Log.d(TAG, "PrepareMatchModeList");
        if (TAG_LIST_MATCH_MODE && !TAG_LIST_LOADED) {
            //This for loop will reset all the items in the tagsListCSV(making Tag count to zero)
            for (int i = 0; i < tagsListCSV.size(); i++) {
                InventoryListItem inv = null;
                if (tagsListCSV.get(i).getCount() != 0) {
                    inv = tagsListCSV.remove(i);
                    InventoryListItem inventoryListItem = new InventoryListItem(inv.getTagID(), 0, null, null, null, null, null, null);
                    inventoryListItem.setTagDetails(inv.getTagDetails());
                    tagsListCSV.add(i, inventoryListItem);
                } else {
                    if (tagsListCSV.get(i).isVisible()) {
                        tagsListCSV.get(i).setVisible(false);
                    }
                }
            }
            UNIQUE_TAGS_CSV = tagsListCSV.size();
            tagsReadInventory.addAll(tagsListCSV);
            inventoryList.putAll(tagListMap);
            missedTags = tagsListCSV.size();
            matchingTags = 0;
            TAG_LIST_LOADED = true;
            Log.d(TAG, "PrepareMatchModeList done");
        }
    }

    public synchronized void inventoryStartOrStop(View view) {
        mRFIDBase.inventoryStartOrStop();
    }

    public void loadNextFragment(int fragmentType) {
        int settingsTab = mAdapter.getSettingsTab();
        String PageTitle = " ";
        try {

            switch (fragmentType) {
                case RAPID_READ_TAB:
                    PageTitle = "빠른 태그";
                    mAdapter.setRFIDMode(RAPID_READ_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(RFID_TAB)).commit();
                    break;
                case LOCATE_TAG_TAB:
                    PageTitle = "태그 찾기";
                    mAdapter.setRFIDMode(LOCATE_TAG_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(RFID_TAB)).commit();
                    break;
                case INVENTORY_TAB:
                    PageTitle = "태그 인벤토리";
                    mAdapter.setRFIDMode(INVENTORY_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(RFID_TAB)).commit();
                    break;
                case RFID_PREFILTERS_TAB:
                    PageTitle = "사전 필터";
                    mAdapter.setRFIDMode(RFID_PREFILTERS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(RFID_TAB)).commit();
                    break;
                case RFID_ACCESS_TAB:
                    PageTitle = "태그 쓰기";
                    mAdapter.setRFIDMode(RFID_ACCESS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(RFID_TAB)).commit();
                    break;
                case RFID_SETTINGS_TAB:
                    PageTitle = "RFID 설정";
                    mAdapter.setSettingsMode(RFID_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case SCAN_SETTINGS_TAB:
                    PageTitle = "스캔";
                    mAdapter.setSettingsMode(SCAN_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case SCAN_DATAVIEW_TAB:
                    PageTitle = "스캔 데이터";
                    mAdapter.setSCANMode(SCAN_DATAVIEW_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(SCAN_TAB)).commit();
                    break;
                case SCAN_ADVANCED_TAB:
                    PageTitle = "고급 스캔";
                    mAdapter.setReaderListMode(SCAN_ADVANCED_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case SCAN_HOME_SETTINGS_TAB:
                    PageTitle = "설정";
                    mAdapter.setReaderListMode(SCAN_HOME_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;

                case READER_LIST_TAB:
                    PageTitle = "장치 설정";
                    mAdapter.setReaderListMode(READER_LIST_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(READERS_TAB)).commit();
                    break;
                case DEVICE_PAIR_TAB:
                    PageTitle = "스캔"; //"Pair";
                    mAdapter.setReaderListMode(DEVICE_PAIR_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(READERS_TAB)).commit();
                    break;
                case READER_DETAILS_TAB:
                    PageTitle = "장치 상세 정보";
                    mAdapter.setReaderListMode(READER_DETAILS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(READERS_TAB)).commit();
                    break;
                case READER_WIFI_SETTINGS_TAB:
                    PageTitle = "Wi-Fi";
                    mAdapter.setReaderListMode(READER_WIFI_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(READERS_TAB)).commit();
                    break;
                case MAIN_RFID_SETTINGS_TAB:
                    PageTitle = "RFID 설정";
                    mAdapter.setSettingsMode(MAIN_RFID_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case MAIN_HOME_SETTINGS_TAB:
                    PageTitle = "설정";
                    mAdapter.setSettingsMode(MAIN_HOME_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case MAIN_GENERAL_SETTINGS_TAB:
                    PageTitle = "일반 설정";
                    mAdapter.setSettingsMode(MAIN_GENERAL_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case APPLICATION_SETTINGS_TAB:
                    PageTitle = "어플리케이션";
                    mAdapter.setSettingsMode(APPLICATION_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case RFID_PROFILES_TAB:
                    PageTitle = "프로파일";
                    mAdapter.setSettingsMode(RFID_PROFILES_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case RFID_ADVANCED_OPTIONS_TAB:
                    PageTitle = "RFID 고급 설정";
                    mAdapter.setSettingsMode(RFID_ADVANCED_OPTIONS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case RFID_REGULATORY_TAB:
                    PageTitle = "규제";
                    mAdapter.setSettingsMode(RFID_REGULATORY_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case RFID_BEEPER_TAB:
                    PageTitle = "신호음";
                    mAdapter.setSettingsMode(RFID_BEEPER_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case RFID_LED_TAB:
                    PageTitle = "LED";
                    mAdapter.setSettingsMode(RFID_LED_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case RFID_WIFI_TAB:
                    PageTitle = "WiFi";
                    mAdapter.setSettingsMode(RFID_WIFI_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case CHARGE_TERMINAL_TAB:
                    PageTitle = "충전 단자";
                    mAdapter.setSettingsMode(CHARGE_TERMINAL_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case ANTENNA_SETTINGS_TAB:
                    PageTitle = "안테나";
                    mAdapter.setSettingsMode(ANTENNA_SETTINGS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case SINGULATION_CONTROL_TAB:
                    PageTitle = "싱귤레이션 제어";
                    mAdapter.setSettingsMode(SINGULATION_CONTROL_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case START_STOP_TRIGGER_TAB:
                    PageTitle = "시작 / 정지 트리거";
                    mAdapter.setSettingsMode(START_STOP_TRIGGER_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case TAG_REPORTING_TAB:
                    PageTitle = "태그 리포팅";
                    mAdapter.setSettingsMode(TAG_REPORTING_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case SAVE_CONFIG_TAB:
                    PageTitle = "구성 저장";
                    mAdapter.setSettingsMode(SAVE_CONFIG_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case DPO_SETTING_TAB:
                    PageTitle = "전원 관리";
                    mAdapter.setSettingsMode(DPO_SETTING_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case FACTORY_RESET_FRAGMENT_TAB:
                    PageTitle = "공장 초기화";
                    mAdapter.setSettingsMode(FACTORY_RESET_FRAGMENT_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case LOGGER_FRAGMENT_TAB:
                    PageTitle = "로깅 설정";
                    mAdapter.setSettingsMode(LOGGER_FRAGMENT_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case DEVICE_RESET_TAB:
                    PageTitle = "기기 초기화";
                    mAdapter.setSettingsMode(DEVICE_RESET_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case KEYREMAP_TAB:
                    PageTitle = "트리거 맵";
                    mAdapter.setSettingsMode(KEYREMAP_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case UPDATE_FIRMWARE_TAB:
                    PageTitle = "펌웨어 업데이트";
                    mAdapter.setSettingsMode(UPDATE_FIRMWARE_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case ASSERT_DEVICE_INFO_TAB:
                    PageTitle = "장치 정보";
                    mAdapter.setSettingsMode(ASSERT_DEVICE_INFO_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case STATIC_IP_CONFIG:
                    PageTitle = "네트워크 IP 구성";
                    mAdapter.setSettingsMode(STATIC_IP_CONFIG);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case BARCODE_SYMBOLOGIES_TAB:
                    PageTitle = "기호";
                    mAdapter.setSettingsMode(BARCODE_SYMBOLOGIES_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case BEEPER_ACTION_TAB:
                    PageTitle = "신호음";
                    mAdapter.setSettingsMode(BEEPER_ACTION_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case BATTERY_STATISTICS_TAB:
                    PageTitle = "배터리 통계";
                    mAdapter.setSettingsMode(BATTERY_STATISTICS_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;
                case USB_MIFI_TAB:
                    PageTitle = "USB MiFi";
                    mAdapter.setSettingsMode(USB_MIFI_TAB);
                    getSupportFragmentManager().beginTransaction().remove(getCurrentFragment(settingsTab)).commit();
                    break;

            }
            setActionBarTitle(PageTitle);
        } catch (NullPointerException | IllegalStateException ne) {
            return;
        }

        getSupportFragmentManager().beginTransaction().addToBackStack(null);
        getSupportFragmentManager().executePendingTransactions();
        mAdapter.notifyDataSetChanged();
    }


    public Fragment getCurrentFragment(int position) {
        return mAdapter.getRegisteredFragment(position);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    void exportData() {
        if (mConnectedReader != null) {
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
            createFile1(uri);
            // new DataExportTask(getApplicationContext(), tagsReadInventory, mConnectedReader.getHostName(), TOTAL_TAGS, UNIQUE_TAGS, mRRStartedTime).execute();
        }
    }

    public void resetFactoryDefault() throws InvalidUsageException, OperationFailureException {
        try {
            if (mConnectedReader != null && mConnectedReader.getTransport() != null && mConnectedReader.getTransport().equals("BLUETOOTH")) {
                mRFIDBase.resetFactoryDefault();
                Thread.sleep(2000);
                mRFIDBase.onFactoryReset(RFIDController.mConnectedDevice);
            } else if (RFIDController.mConnectedDevice != null) {
                mRFIDBase.resetFactoryDefault();
            }

        } catch (OperationFailureException e) {
            throw e;
        } catch (InterruptedException e) {
        }

    }


    public void onRadioButtonClicked(View view) {

        Fragment fragment = getCurrentFragment(SETTINGS_TAB);
        if (fragment instanceof FactoryResetFragment) {
            ((FactoryResetFragment) fragment).changeResetMode(view);
        }

    }

    public boolean deviceReset(String commandString) throws InvalidUsageException, OperationFailureException {
        return mRFIDBase.deviceReset();
    }

    void checkForExportPermission(final int code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                switch (code) {
                    case REQUEST_CODE_ASK_PERMISSIONS:
                        exportData();
                        break;
                    case REQUEST_CODE_ASK_PERMISSIONS_CSV:
                        MatchModeFileLoader.getInstance(this).LoadMatchModeCSV();
                        break;
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showMessageOKCancel("Write to external storage permission needed to export the inventory.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
                        }
                    });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ActiveDeviceActivity.this).setMessage(message).setPositiveButton("OK", okListener).setNegativeButton("Cancel", null).create().show();
    }

    public void disableScanner() {
        mRFIDBase.disableScanner();
    }

    public void enableScanner() {
        mRFIDBase.enableScanner();
    }

    @Override
    public void LoadTagListCSV() {
        Log.d(TAG, "LoadTagListCSV");
        mRFIDBase.LoadTagListCSV();

    }


    public void startbeepingTimer() {
        mRFIDBase.startbeepingTimer();
    }

    public void performtagmatchClick() {
        if (inventoryBT != null) {
            if (mIsInventoryRunning) {
                inventoryBT.performClick();
                startbeepingTimer();
            }
        }
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
        NdefMessage msg = new NdefMessage(new NdefRecord[]{NdefRecord.createMime("application/com.bluefletch.nfcdemo.mimetype", text.getBytes())
                /**
                 * The Android Application Record (AAR) is commented out. When a device
                 * receives a push with an AAR in it, the application specified in the AAR
                 * is guaranteed to run. The AAR overrides the tag dispatch system.
                 * You can add it back in to guarantee that this
                 * activity starts when receiving a beamed message. For now, this code
                 * uses the tag dispatch system.
                */
                //,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        return msg;
    }



    public void createDWProfile() {
        // MAIN BUNDLE PROPERTIES
        if ((RFIDController.mConnectedReader != null) && RFIDController.mConnectedReader.getHostName() != null && RFIDController.mConnectedReader.getHostName().startsWith("MC33")) {


            Bundle bMain = new Bundle();
            bMain.putString("PROFILE_NAME", "RFIDMobileApp");
            bMain.putString("PROFILE_ENABLED", "true");              // <- that will be enabled
            bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");   // <- or created if necessary.
            // PLUGIN_CONFIG BUNDLE PROPERTIES
            Bundle scanBundle = new Bundle();
            scanBundle.putString("PLUGIN_NAME", "BARCODE"); // barcode plugin
            scanBundle.putString("RESET_CONFIG", "true");
            // PARAM_LIST BUNDLE PROPERTIES
            Bundle scanParams = new Bundle();
            scanParams.putString("scanner_selection", "auto");
            scanParams.putString("scanner_input_enabled", "false"); // Mainly disable scanner plugin
            // NEST THE BUNDLE "bParams" WITHIN THE BUNDLE "bConfig"
            scanBundle.putBundle("PARAM_LIST", scanParams);

            Bundle keystrokeBundle = new Bundle();
            keystrokeBundle.putString("PLUGIN_NAME", "KEYSTROKE");
            Bundle keyStrokeParams = new Bundle();
            keyStrokeParams.putString("keystroke_output_enabled", "false");
            keyStrokeParams.putString("keystroke_action_char", "9"); // 0, 9 , 10, 13
            keyStrokeParams.putString("keystroke_delay_extended_ascii", "500");
            keyStrokeParams.putString("keystroke_delay_control_chars", "800");
            keystrokeBundle.putBundle("PARAM_LIST", keyStrokeParams);

            Bundle rfidConfigParamList = new Bundle();
            rfidConfigParamList.putString("rfid_input_enabled", "false");
            Bundle rfidConfigBundle = new Bundle();
            rfidConfigBundle.putString("PLUGIN_NAME", "RFID");
            rfidConfigBundle.putString("RESET_CONFIG", "true");
            rfidConfigBundle.putBundle("PARAM_LIST", rfidConfigParamList);


            Bundle bConfigIntent = new Bundle();
            Bundle bParamsIntent = new Bundle();
            bParamsIntent.putString("intent_output_enabled", "true");
            bParamsIntent.putString("intent_action", "com.symbol.dwudiusertokens.udi");
            bParamsIntent.putString("intent_category", "zebra.intent.dwudiusertokens.UDI");
            bParamsIntent.putInt("intent_delivery", 2); //Use "0" for Start Activity, "1" for Start Service, "2" for Broadcast, "3" for start foreground service
            bConfigIntent.putString("PLUGIN_NAME", "INTENT");
            bConfigIntent.putString("RESET_CONFIG", "true");
            bConfigIntent.putBundle("PARAM_LIST", bParamsIntent);


            // THEN NEST THE "bConfig" BUNDLE WITHIN THE MAIN BUNDLE "bMain"
            ArrayList<Bundle> bundleArrayList = new ArrayList<>();
            bundleArrayList.add(scanBundle);
            bundleArrayList.add(rfidConfigBundle);
            bundleArrayList.add(keystrokeBundle);
            bundleArrayList.add(bConfigIntent);

            // following requires arrayList
            bMain.putParcelableArrayList("PLUGIN_CONFIG", bundleArrayList);
            // CREATE APP_LIST BUNDLES (apps and/or activities to be associated with the Profile)
            Bundle ActivityList = new Bundle();
            ActivityList.putString("PACKAGE_NAME", getPackageName());      // Associate the profile with this app
            ActivityList.putStringArray("ACTIVITY_LIST", new String[]{"*"});

            // NEXT APP_LIST BUNDLE(S) INTO THE MAIN BUNDLE
            bMain.putParcelableArray("APP_LIST", new Bundle[]{ActivityList});
            Intent i = new Intent();
            i.setAction("com.symbol.datawedge.api.ACTION");
            i.putExtra("com.symbol.datawedge.api.SET_CONFIG", bMain);
            i.putExtra("SEND_RESULT", "true");
            i.putExtra("COMMAND_IDENTIFIER", Application.RFID_DATAWEDGE_PROFILE_CREATION);
            sendBroadcast(i);
        }
    }

    private void deviceDisconnect(){
        RFIDSingleton.deviceDisconnect();
        RFIDController.clearSettings();

        ImageView batteryLevelImage = findViewById(R.id.appbar_batteryLevelImage);
        batteryLevelImage.setImageLevel(0);
    }
}
