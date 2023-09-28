package com.codegear.mariamc_rfid.cowchronicle.ui.drawer;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.CowChronicleActivity;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.FarmSelectFragment;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.UserInfoFragment;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.UserLoginActivity;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.WebviewHomeFragment;
import com.codegear.mariamc_rfid.cowchronicle.device.RFIDSingleton;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.PixelUtil;
import com.codegear.mariamc_rfid.rfidreader.common.Constants;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingsDetailActivity;
import com.google.android.material.navigation.NavigationView;
import com.zebra.rfid.api3.BatteryStatistics;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

public class CustomConnectedDrawer {

    private AppCompatActivity mActivity;
    private DrawerLayout mDrawerLayout;
    private View rlNavigationDeviceContainer;
    private TextView tvBatteryPercentage;
    private ImageView ivBatterylevel;
    private Button btnDisconnect;

    private int batteryPercent = 0;

    public CustomConnectedDrawer(AppCompatActivity activity){
        mActivity = activity;

        rlNavigationDeviceContainer = mActivity.findViewById(R.id.rlNavigationDeviceContainer);
        tvBatteryPercentage = mActivity.findViewById(R.id.tvBatteryPercentage);
        ivBatterylevel = mActivity.findViewById(R.id.ivBatterylevel);
        btnDisconnect = mActivity.findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(v -> {
            RFIDSingleton.deviceDisconnect();
            mDrawerLayout.closeDrawer(GravityCompat.START);
        });

        //배터리 퍼센트 가져오기
        loadBatteryPercent();



        //툴바, 네비게이션바 세팅
        Toolbar toolbar = (Toolbar)mActivity.findViewById(R.id.dis_toolbar);
        mActivity.setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState(); //ActionBarDrawerToggle 누를때 앱 종료되는 현상 막기.

        //drawer 상태 리스너
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                refreshEmptyMenuItemHeight();

                //연결 상태 확인
                if (RFIDController.mConnectedReader != null) {
                    rlNavigationDeviceContainer.setVisibility(View.VISIBLE);

                    //배터리 정보 업데이트
                    loadBatteryPercent();
                    applyBatteryPercent();

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

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void refreshEmptyMenuItemHeight(){
        //사이즈 측정
        View vDisMainMenu = mActivity.getLayoutInflater().inflate(R.layout.main_menu, null, false);
        vDisMainMenu.measure(0, 0);
        int disMainMenuHeight = vDisMainMenu.getMeasuredHeight();
        int screenHeight = PixelUtil.getScreenHeightPx(mActivity);
        int navigationViewMenuHeight = (int) mActivity.getResources().getDimension(R.dimen.drawer_navigationview_item_height);

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
        NavigationView navigationView = (NavigationView) mActivity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(mActivity::onOptionsItemSelected);
        int finalRlNavigationDeviceContainerTotalHeight = rlNavigationDeviceContainerTotalHeight;


        final View v = mActivity.getLayoutInflater().inflate(R.layout.drawer_menu_custom_item, null);
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


    public Boolean onOptionsItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        //네비게이션 기능을 사용하기 전에, 로그인 먼저 해야됨을 안내.
        if(!UserStorage.getInstance().isLogin()){
            CustomDialog.showSimple(mActivity, R.string.login_need_login_process);

            //로그인 페이지가 아니면, 로그인페이지로 이동
            if(!(mActivity instanceof UserLoginActivity)){
                Intent intent = new Intent(mActivity, UserLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
                mActivity.finishAffinity();
            }
            return true;
        }

        //네비게이션 메뉴 아이디에 따른 행동.
        switch (item.getItemId()) {
            case R.id.menu_cowchronicle:
                if(isLoginWithGoPage()){
                    replaceFragment(new WebviewHomeFragment(), true);
                }
                return true;

            case R.id.menu_readers:
                if(isLoginWithGoPage()) {
                    replaceFragment(new FarmSelectFragment(), true);
                }
                return true;

            case R.id.nav_user_info:
                if(isLoginWithGoPage()) {
                    replaceFragment(new UserInfoFragment(), true);
                }
                return true;

            case R.id.nav_battery_statics:
                if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                    CustomDialog.showSimple(mActivity, "연결된 장치가 없어서 실행이 불가합니다.\n전자이표 또는 장치 설정을 눌러서 장치를 연결 후에 다시 시도해주세요.");
                } else {
                    Intent batteryIntent = new Intent(mActivity, SettingsDetailActivity.class);
                    batteryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    batteryIntent.putExtra(Constants.SETTING_ITEM_ID, R.id.battery);
                    mActivity.startActivity(batteryIntent);
                }
                return true;

            case R.id.nav_fw_update:
                if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                    CustomDialog.showSimple(mActivity, "연결된 장치가 없어서 실행이 불가합니다.\n전자이표 또는 장치 설정을 눌러서 장치를 연결 후에 다시 시도해주세요.");
                } else{
                    Intent fwIntent = new Intent(mActivity, SettingsDetailActivity.class);
                    fwIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    fwIntent.putExtra(Constants.SETTING_ITEM_ID, R.id.firmware_update);
                    fwIntent.putExtra(Constants.SETTING_ON_FACTORY, true);
                    mActivity.startActivityForResult(fwIntent, 0);
                }
                return true;

            case R.id.nav_settings:

                if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                    if(!(mActivity instanceof DeviceDiscoverActivity)) {
                        Intent intent = new Intent(mActivity, DeviceDiscoverActivity.class);

                        //자동연결 진행하고, 설정화면에 남아있게 만들기.
                        intent.putExtra(DeviceDiscoverActivity.ENABLE_AUTO_CONNECT_DEVICE, true); //자동연결 하기.
                        intent.putExtra(DeviceDiscoverActivity.DESTINATION_SCREEN_IS_COWCHRONICLE, false); //연결 후, 카우크로니클 이동 여부.

                        mActivity.startActivity(intent);
                    }
                    else{
                        //nothing..
                    }
                }
                else if(!(mActivity instanceof ActiveDeviceActivity)) {
                    Intent intent = new Intent(mActivity, ActiveDeviceActivity.class);
                    mActivity.startActivity(intent);
                }
                return true;

            default:
                return null;
        }
    }

    private void replaceFragment(Fragment fragment, boolean needBackStack){
        ((CowChronicleActivity)mActivity).replaceFragment(fragment, needBackStack);
    }

    //로그인 확인 및 이동.
    private boolean isLoginWithGoPage(){
        if(!UserStorage.getInstance().isLogin()){
            //로그인 페이지가 아니면, 로그인페이지로 이동
            if(!(mActivity instanceof UserLoginActivity)){
                Intent intent = new Intent(mActivity, UserLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
                mActivity.finishAffinity();
                
                return false;
            }
        }
        
        return true;
    }

    //배터리 퍼센트 가져오기
    private void loadBatteryPercent(){

        if (RFIDController.mConnectedReader == null) {
            //Toast.makeText(getActivity(), "연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!RFIDController.mIsInventoryRunning) {
            try {
                if (RFIDController.mConnectedReader.Config != null) {
                    BatteryStatistics batteryStats = RFIDController.mConnectedReader.Config.getBatteryStats();
                    batteryPercent = batteryStats.getPercentage();
                }
                else return;
            } catch (InvalidUsageException | NullPointerException e) {
                //Log.e(TAG, e.getStackTrace()[0].toString());
            } catch (OperationFailureException e) {
                if (e.getResults() == RFIDResults.RFID_OPERATION_IN_PROGRESS) {
                    //Toast.makeText(getActivity(), "작업이 진행 중입니다. 배터리 통계를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            //Toast.makeText(getActivity(), "인벤토리가 진행 중입니다. 배터리 통계를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //배터리 퍼센트 적용하기
    private void applyBatteryPercent(){
        int batteryLevel = batteryPercent;
        tvBatteryPercentage.setText(String.valueOf(batteryLevel) + "%");
        ivBatterylevel.setImageLevel(batteryLevel);
    }
}
