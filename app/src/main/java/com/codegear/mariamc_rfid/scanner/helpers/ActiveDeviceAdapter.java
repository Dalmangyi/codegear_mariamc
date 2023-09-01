package com.codegear.mariamc_rfid.scanner.helpers;


import android.util.SparseArray;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.codegear.mariamc_rfid.LoggerFragment;
import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.discover_connect.nfc.PairOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.access_operations.AccessOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.home.nonoperationFragment;
import com.codegear.mariamc_rfid.rfidreader.inventory.RFIDInventoryFragment;
import com.codegear.mariamc_rfid.rfidreader.locate_tag.LocateOperationsFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.DeviceResetFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.FactoryResetFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.ManagerFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.ScanHomeSettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.manager.mainSettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.rapidread.RapidReadFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.RFIDReadersListFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.AdvancedOptionItemFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.AntennaSettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ApplicationSettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.BatteryFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.BatteryStatsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.BeeperFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ChargeTerminalFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.DPOSettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.KeyRemapFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.LedFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.PreFilterFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ProfileFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.RegulatorySettingsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SaveConfigurationsFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingListFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.SingulationControlFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.StartStopTriggersFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.TagReportingFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.UsbMiFiFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.WifiFragment;
import com.codegear.mariamc_rfid.scanner.activities.AssertFragment;
import com.codegear.mariamc_rfid.scanner.activities.BeeperActionsFragment;
import com.codegear.mariamc_rfid.scanner.activities.SymbologiesFragment;
import com.codegear.mariamc_rfid.scanner.activities.UpdateFirmware;
import com.codegear.mariamc_rfid.scanner.fragments.AdvancedFragment;
import com.codegear.mariamc_rfid.scanner.fragments.BarcodeFargment;
import com.codegear.mariamc_rfid.scanner.fragments.ReaderDetailsFragment;
import com.codegear.mariamc_rfid.scanner.fragments.SettingsFragment;
import com.codegear.mariamc_rfid.scanner.fragments.Static_ipconfig;
import com.codegear.mariamc_rfid.wifi.ReaderWifiSettingsFragment;

/**
 * Adapter to give the tabs for Active Scanner
 */
public class ActiveDeviceAdapter extends FragmentStatePagerAdapter {

    public static final int READERS_TAB = 0;
    public static final  int RFID_TAB = 1;
    public static final int SCAN_SETTINGS_TAB = 2;
    public static final int SCAN_DATAVIEW_TAB = 3;
    public static final int SCAN_ADVANCED_TAB = 4;
    public static final int INVENTORY_TAB = 10;
    public static final int RAPID_READ_TAB = 11;
    public static final int LOCATE_TAG_TAB = 12;
    public static final int PROFILES_TAB = 13;
    public static final int RFID_SETTINGS_TAB = 14;
    public static final int RFID_ACCESS_TAB = 15;
    public static final int RFID_PREFILTERS_TAB = 16;
    public static final int RFID_ABOUT_TAB = 17;
    public static final int DEVICE_PAIR_TAB = 18;
    public static final int READER_LIST_TAB = 19;
    public static final int MAIN_RFID_SETTINGS_TAB = 20;
    public static final int MAIN_HOME_SETTINGS_TAB = 21;
    public static final int MAIN_GENERAL_SETTINGS_TAB = 22;
    public static final int SCAN_HOME_SETTINGS_TAB = 23;
    public static final int APPLICATION_SETTINGS_TAB = 24;
    public static final int RFID_PROFILES_TAB = 25;
    public static final int RFID_ADVANCED_OPTIONS_TAB = 26;
    public static final int RFID_REGULATORY_TAB = 27;
    public static final int RFID_BEEPER_TAB = 28;
    public static final int RFID_LED_TAB = 29;
    public static final int ANTENNA_SETTINGS_TAB = 30;
    public static final int SINGULATION_CONTROL_TAB = 31;
    public static final int START_STOP_TRIGGER_TAB = 32;
    public static final int TAG_REPORTING_TAB = 33;
    public static final int SAVE_CONFIG_TAB = 34;
    public static final int DPO_SETTING_TAB = 35;
    public static final int FACTORY_RESET_FRAGMENT_TAB = 36;
    public static final int LOGGER_FRAGMENT_TAB = 37;
    public static final int  DEVICE_RESET_TAB = 38;
    public static final int KEYREMAP_TAB = 39;
    public static final int UPDATE_FIRMWARE_TAB = 40;
    public static final int ASSERT_DEVICE_INFO_TAB = 41;
    public static final int BARCODE_SYMBOLOGIES_TAB = 42;
    public static final int BEEPER_ACTION_TAB = 43;
    public static final int READER_DETAILS_TAB = 44;
    public static final int READER_WIFI_SETTINGS_TAB = 45;
    public static final int STATIC_IP_CONFIG = 46;
    public static final int RFID_WIFI_TAB = 47;
    public static final int NONOPER_TAB = 48;
    public static final int CHARGE_TERMINAL_TAB = 49;
    public static final int BATTERY_STATISTICS_TAB = 50;
    public static final int USB_MIFI_TAB = 51;
    public static final int SETTINGS_TAB = 3;

    public static final int SCAN_TAB = 2;
    public static final int BARCODE_TAB = SCAN_TAB;

    private static int currentPostion = RFID_TAB;
    private static int mNextRFIDFragmentId = RAPID_READ_TAB;
    private static int mNextSCANFragmentId = SCAN_DATAVIEW_TAB;
    private static int mNextRedaerListFragmentId = READER_LIST_TAB;
    private static int mNextSettingsFragmentId = MAIN_HOME_SETTINGS_TAB;
    private Fragment mRfidFragment;
    private Fragment mScannerFragment;
    private Fragment mReadersFragment;
    private Fragment mMainSettingsFragment;
    private Fragment mNoImagerFragment;
    private final int mFunctionCount;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private String mModelName;


    public ActiveDeviceAdapter(FragmentManager fm, int deviceMode) {
        super(fm);
        mFunctionCount= deviceMode;
        if(deviceMode == Application.DEVICE_STD_MODE)
        {


        }else if(deviceMode == Application.DEVICE_PREMIUM_PLUS_MODE)
        {


        }

    }


    /**
     * Return the Fragment associated with a specified position.
     * @param position - tab selected
     */
    @Override
    public Fragment getItem(int position) {

        currentPostion = position;
        switch (position) {
            case READERS_TAB:
                Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "1st Tab Selected");
                switch(mNextRedaerListFragmentId)
                {
                    case DEVICE_PAIR_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mReadersFragment = PairOperationsFragment.newInstance();
                        return mReadersFragment;
                    case READER_LIST_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mReadersFragment = RFIDReadersListFragment.getInstance();
                        return mReadersFragment;
                    case READER_DETAILS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mReadersFragment = ReaderDetailsFragment.newInstance();
                        return mReadersFragment;
                    case READER_WIFI_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mReadersFragment = ReaderWifiSettingsFragment.newInstance();
                        return mReadersFragment;
                    default:
                        mReadersFragment = nonoperationFragment.newInstance();
                        return mReadersFragment;


                }

            case SETTINGS_TAB:
                switch(mNextSettingsFragmentId)
                {
                    case MAIN_RFID_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = SettingListFragment.newInstance();
                        return mMainSettingsFragment;
                    case MAIN_HOME_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = mainSettingsFragment.newInstance();
                        return mMainSettingsFragment;
                    case MAIN_GENERAL_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = ManagerFragment.newInstance();
                        return mMainSettingsFragment;
                    case SCAN_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mScannerFragment = SettingsFragment.newInstance();
                        return mScannerFragment;
                    case SCAN_ADVANCED_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "3rd Tab Selected");
                        mScannerFragment = AdvancedFragment.newInstance();
                        return mScannerFragment;
                    case SCAN_HOME_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "3rd Tab Selected");
                        mScannerFragment = ScanHomeSettingsFragment.newInstance();
                        return mScannerFragment;
                    case APPLICATION_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = ApplicationSettingsFragment.newInstance();
                        return mMainSettingsFragment;
                    case RFID_PROFILES_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = ProfileFragment.newInstance();
                        return mMainSettingsFragment;
                    case RFID_ADVANCED_OPTIONS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = AdvancedOptionItemFragment.newInstance();
                        return mMainSettingsFragment;
                    case RFID_REGULATORY_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = RegulatorySettingsFragment.newInstance();
                        return mMainSettingsFragment;

                    case RFID_BEEPER_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = BeeperFragment.newInstance();
                        return mMainSettingsFragment;
                    case RFID_LED_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = LedFragment.newInstance();
                        return mMainSettingsFragment;
                    case RFID_WIFI_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = WifiFragment.newInstance();
                        return mMainSettingsFragment;
                    case CHARGE_TERMINAL_TAB:
                        mMainSettingsFragment = ChargeTerminalFragment.newInstance();
                        return mMainSettingsFragment;
                    case ANTENNA_SETTINGS_TAB:
                        mMainSettingsFragment = AntennaSettingsFragment.newInstance();
                        return mMainSettingsFragment;
                    case SINGULATION_CONTROL_TAB:
                        mMainSettingsFragment = SingulationControlFragment.newInstance();
                        return mMainSettingsFragment;
                    case START_STOP_TRIGGER_TAB:
                        mMainSettingsFragment = StartStopTriggersFragment.newInstance();
                        return mMainSettingsFragment;
                    case TAG_REPORTING_TAB:
                        mMainSettingsFragment = TagReportingFragment.newInstance();
                        return mMainSettingsFragment;
                    case  SAVE_CONFIG_TAB:
                        mMainSettingsFragment = SaveConfigurationsFragment.newInstance();
                        return mMainSettingsFragment;
                    case DPO_SETTING_TAB:
                        mMainSettingsFragment = DPOSettingsFragment.newInstance();
                        return mMainSettingsFragment;
                    case FACTORY_RESET_FRAGMENT_TAB:
                        mMainSettingsFragment = FactoryResetFragment.newInstance();
                        return mMainSettingsFragment;
                    case LOGGER_FRAGMENT_TAB:
                        mMainSettingsFragment = LoggerFragment.newInstance();
                        return mMainSettingsFragment;
                    case DEVICE_RESET_TAB:
                        mMainSettingsFragment = DeviceResetFragment.newInstance();
                        return mMainSettingsFragment;
                    case KEYREMAP_TAB:
                        mMainSettingsFragment = KeyRemapFragment.newInstance();
                        return mMainSettingsFragment;
                    case UPDATE_FIRMWARE_TAB:
                        mMainSettingsFragment = UpdateFirmware.newInstance();
                        return mMainSettingsFragment;
                    case ASSERT_DEVICE_INFO_TAB:
                        mMainSettingsFragment = AssertFragment.newInstance();
                        return mMainSettingsFragment;
                    case STATIC_IP_CONFIG:
                        mMainSettingsFragment = Static_ipconfig.newInstance();
                        return  mMainSettingsFragment;

                    case BARCODE_SYMBOLOGIES_TAB:
                        mMainSettingsFragment = SymbologiesFragment.newInstance();
                        return mMainSettingsFragment;
                    case BEEPER_ACTION_TAB:
                        mMainSettingsFragment = BeeperActionsFragment.newInstance();
                        return mMainSettingsFragment;
                    case RFID_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mMainSettingsFragment = SettingListFragment.newInstance();
                        return mMainSettingsFragment;
                    case BATTERY_STATISTICS_TAB:
                        if(mModelName.startsWith("RFD40") || mModelName.startsWith("RFD90"))
                            mMainSettingsFragment = BatteryStatsFragment.newInstance();
                        else
                            mMainSettingsFragment = BatteryFragment.newInstance();

                        return mMainSettingsFragment;
                    case USB_MIFI_TAB:
                        mMainSettingsFragment = UsbMiFiFragment.newInstance();
                        return mMainSettingsFragment;

                    default:
                       // mNoImagerFragment= NoImagerFragment.newInstance();
                        mNoImagerFragment= nonoperationFragment.newInstance();
                        return mNoImagerFragment;

                }

            case SCAN_TAB:
                if(Application.RFD_DEVICE_MODE == Application.DEVICE_STD_MODE){
                    switch(mNextSettingsFragmentId)
                    {
                        case MAIN_RFID_SETTINGS_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = SettingListFragment.newInstance();
                            return mMainSettingsFragment;
                        case MAIN_HOME_SETTINGS_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = mainSettingsFragment.newInstance();
                            return mMainSettingsFragment;
                        case MAIN_GENERAL_SETTINGS_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = ManagerFragment.newInstance();
                            return mMainSettingsFragment;
                        case APPLICATION_SETTINGS_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = ApplicationSettingsFragment.newInstance();
                            return mMainSettingsFragment;
                        case RFID_PROFILES_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = ProfileFragment.newInstance();
                            return mMainSettingsFragment;
                        case RFID_ADVANCED_OPTIONS_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = AdvancedOptionItemFragment.newInstance();
                            return mMainSettingsFragment;
                        case RFID_REGULATORY_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = RegulatorySettingsFragment.newInstance();
                            return mMainSettingsFragment;

                        case RFID_BEEPER_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = BeeperFragment.newInstance();
                            return mMainSettingsFragment;
                        case RFID_LED_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = LedFragment.newInstance();
                            return mMainSettingsFragment;
                        case RFID_WIFI_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = WifiFragment.newInstance();
                            return mMainSettingsFragment;
                        case CHARGE_TERMINAL_TAB:
                            mMainSettingsFragment = ChargeTerminalFragment.newInstance();
                            return mMainSettingsFragment;
                        case ANTENNA_SETTINGS_TAB:
                            mMainSettingsFragment = AntennaSettingsFragment.newInstance();
                            return mMainSettingsFragment;
                        case SINGULATION_CONTROL_TAB:
                            mMainSettingsFragment = SingulationControlFragment.newInstance();
                            return mMainSettingsFragment;
                        case START_STOP_TRIGGER_TAB:
                            mMainSettingsFragment = StartStopTriggersFragment.newInstance();
                            return mMainSettingsFragment;
                        case TAG_REPORTING_TAB:
                            mMainSettingsFragment = TagReportingFragment.newInstance();
                            return mMainSettingsFragment;
                        case  SAVE_CONFIG_TAB:
                            mMainSettingsFragment = SaveConfigurationsFragment.newInstance();
                            return mMainSettingsFragment;
                        case DPO_SETTING_TAB:
                            mMainSettingsFragment = DPOSettingsFragment.newInstance();
                            return mMainSettingsFragment;
                        case FACTORY_RESET_FRAGMENT_TAB:
                            mMainSettingsFragment = FactoryResetFragment.newInstance();
                            return mMainSettingsFragment;
                        case LOGGER_FRAGMENT_TAB:
                            mMainSettingsFragment = LoggerFragment.newInstance();
                            return mMainSettingsFragment;
                        case DEVICE_RESET_TAB:
                            mMainSettingsFragment = DeviceResetFragment.newInstance();
                            return mMainSettingsFragment;
                        case KEYREMAP_TAB:
                            mMainSettingsFragment = KeyRemapFragment.newInstance();
                            return mMainSettingsFragment;
                        case UPDATE_FIRMWARE_TAB:
                            mMainSettingsFragment = UpdateFirmware.newInstance();
                            return mMainSettingsFragment;
                        case ASSERT_DEVICE_INFO_TAB:
                            mMainSettingsFragment = AssertFragment.newInstance();
                            return mMainSettingsFragment;
                        case STATIC_IP_CONFIG:
                            mMainSettingsFragment = Static_ipconfig.newInstance();
                            return  mMainSettingsFragment;

                        case BARCODE_SYMBOLOGIES_TAB:
                            mMainSettingsFragment = SymbologiesFragment.newInstance();
                            return mMainSettingsFragment;
                        case SCAN_SETTINGS_TAB:
                            mMainSettingsFragment = SettingsFragment.newInstance();
                            return mMainSettingsFragment;
                        case RFID_SETTINGS_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mMainSettingsFragment = SettingListFragment.newInstance();
                            return mMainSettingsFragment;
                        case BATTERY_STATISTICS_TAB:
                            if(mModelName.startsWith("RFD40") || mModelName.startsWith("RFD90"))
                                mMainSettingsFragment = BatteryStatsFragment.newInstance();
                            else
                                mMainSettingsFragment = BatteryFragment.newInstance();
                            return mMainSettingsFragment;
                        case USB_MIFI_TAB:
                            mMainSettingsFragment = UsbMiFiFragment.newInstance();
                            return mMainSettingsFragment;

                        default:
                            //mNoImagerFragment= NoImagerFragment.newInstance();
                            mNoImagerFragment= nonoperationFragment.newInstance();
                            return mNoImagerFragment;
                    }

                }else{

                    switch(mNextSCANFragmentId)
                    {
                        case SCAN_DATAVIEW_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mScannerFragment = BarcodeFargment.newInstance();
                            return mScannerFragment;
                        case SCAN_SETTINGS_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                            mScannerFragment = SettingsFragment.newInstance();
                            return mScannerFragment;
                        case SCAN_ADVANCED_TAB:
                            Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "3rd Tab Selected");
                            mScannerFragment = AdvancedFragment.newInstance();
                            return mScannerFragment;
                        default:
                            //mNoImagerFragment= NoImagerFragment.newInstance();
                            mNoImagerFragment= nonoperationFragment.newInstance();
                            return mNoImagerFragment;


                    }
                }

            case RFID_TAB:
                switch(mNextRFIDFragmentId)
                {
                    case INVENTORY_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mRfidFragment = RFIDInventoryFragment.newInstance();
                        return mRfidFragment;
                    case RAPID_READ_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mRfidFragment =  RapidReadFragment.newInstance();
                        return mRfidFragment;
                    case LOCATE_TAG_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mRfidFragment =  LocateOperationsFragment.newInstance();
                        return mRfidFragment;
                    case RFID_PREFILTERS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mRfidFragment = PreFilterFragment.newInstance();
                        return mRfidFragment;
                    case RFID_ACCESS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mRfidFragment = AccessOperationsFragment.newInstance();
                        return mRfidFragment;
                    case RFID_SETTINGS_TAB:
                        Constants.logAsMessage(Constants.DEBUG_TYPE.TYPE_DEBUG, getClass().getSimpleName(), "2nd Tab Selected");
                        mRfidFragment = SettingListFragment.newInstance();
                        return mRfidFragment;

                }
                mRfidFragment = nonoperationFragment.newInstance();
                return mRfidFragment;
           // case 6:
           //     Constants.logAsMessage(TYPE_DEBUG, getClass().getSimpleName(), "3rd Tab Selected");
           //     return AdvancedFragment.newInstance();

            default:
                return null;
        }
    }

    public void setCurrentActivePosition(int pos) {
        currentPostion = pos;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mFunctionCount;
    }

    public void setRFIDMOde(int fragmentType) {

        mNextRFIDFragmentId = fragmentType;
        mNextSCANFragmentId = NONOPER_TAB;
        mNextRedaerListFragmentId = NONOPER_TAB;
        mNextSettingsFragmentId = NONOPER_TAB;
    }

    public int getRFIDMOde() {

        return mNextRFIDFragmentId;
    }




    public void setSettingsMode(int settingsTab) {
        mNextSettingsFragmentId = settingsTab;
        mNextRedaerListFragmentId = NONOPER_TAB;
        mNextSCANFragmentId = NONOPER_TAB;
        mNextRFIDFragmentId = NONOPER_TAB;


    }

    public int getSettingsMode() {
        return mNextSettingsFragmentId;
    }

    public void setSCANMOde(int scanSettingsTab) {
        mNextSCANFragmentId = scanSettingsTab;
        mNextRFIDFragmentId = NONOPER_TAB;
        mNextRedaerListFragmentId = NONOPER_TAB;
        mNextSettingsFragmentId = NONOPER_TAB;
    }

    public void setReaderListMOde(int devicePairTab) {
        mNextRedaerListFragmentId = devicePairTab;
        mNextSCANFragmentId = NONOPER_TAB;
        mNextRFIDFragmentId = NONOPER_TAB;
        mNextSettingsFragmentId = NONOPER_TAB;
    }

    public int  getReaderListMOde() {  return mNextRedaerListFragmentId ;  }

    public int getCurrentActivePosition() {
        return currentPostion;
    }

    public Fragment getReadersFragment()
    {
        return mReadersFragment;
    }

    public Fragment getRFIDFragment() {
        return mRfidFragment;
    }

    public Fragment getScannerFragment() {
        return mScannerFragment;
    }

    public Fragment getmMainSettingsFragment() {
        return mMainSettingsFragment;
    }


    public Fragment getRegisteredFragment(int position) {
        if((Application.RFD_DEVICE_MODE == Application.DEVICE_STD_MODE)  && ( position > 2 )){
            position = 2;
        }
        return registeredFragments.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        super.destroyItem(container, position, object);
        registeredFragments.remove(position);

    }

    public int getSettingsTab() {

        if(Application.RFD_DEVICE_MODE == Application.DEVICE_PREMIUM_PLUS_MODE)
            return 3;

        else return 2;

    }

    public void setDeviceModelName(String modelName) {
        mModelName = modelName;
    }
}
