package com.codegear.mariamc_rfid.cowchronicle.ui.drawer;

import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.BATTERY_STATISTICS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.UPDATE_FIRMWARE_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.Constants.INTENT_NEXT_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.Constants.INTENT_START_TAB;

import android.content.Intent;
import android.util.Log;
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
import com.codegear.mariamc_rfid.cowchronicle.consts.BottomNavEnum;
import com.codegear.mariamc_rfid.cowchronicle.consts.CowChronicleScreenEnum;
import com.codegear.mariamc_rfid.cowchronicle.ui.screens.CowChronicleActivity;
import com.codegear.mariamc_rfid.cowchronicle.ui.screens.FarmSelectFragment;
import com.codegear.mariamc_rfid.cowchronicle.ui.screens.UserInfoFragment;
import com.codegear.mariamc_rfid.cowchronicle.ui.screens.UserLoginActivity;
import com.codegear.mariamc_rfid.cowchronicle.ui.screens.WebviewHomeFragment;
import com.codegear.mariamc_rfid.cowchronicle.device.RFIDSingleton;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.PixelUtil;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.google.android.material.navigation.NavigationView;
import com.zebra.rfid.api3.BatteryStatistics;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

public class CustomConnectedDrawer {

    private final String TAG = "CustomConnectedDrawer";


    private AppCompatActivity mActivity;
    private DrawerLayout mDrawerLayout;
    private View rlDeviceContainer;
    private TextView tvBatteryPercentage;
    private ImageView ivBatteryLevel;
    private Button btnDisconnect;

    private int batteryPercent = 0;

    public CustomConnectedDrawer(AppCompatActivity activity){
        mActivity = activity;

        rlDeviceContainer = mActivity.findViewById(R.id.rlDeviceContainer);
        tvBatteryPercentage = mActivity.findViewById(R.id.tvBatteryPercentage);
        ivBatteryLevel = mActivity.findViewById(R.id.ivBatteryLevel);
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


            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                Log.d(TAG, "onDrawerStateChanged newState:"+newState+",isOpen:"+mDrawerLayout.isDrawerOpen(GravityCompat.START));

                //열기전.
                if (newState == DrawerLayout.STATE_SETTLING && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // starts opening
                    refreshDrawerLayout();
                }
            }

            //Drawer Layout 새로고침 (메뉴 사이즈, 연결상태, 배터리 정보, 기기 이름 등)
            private void refreshDrawerLayout(){
                refreshEmptyMenuItemHeight();

                //연결 상태 확인
                if (RFIDController.mConnectedReader != null) {
                    rlDeviceContainer.setVisibility(View.VISIBLE);

                    //배터리 정보 업데이트
                    loadBatteryPercent();
                    applyBatteryPercent();

                    //기기 이름
                    String strDeviceHostName = RFIDController.mConnectedReader.getHostName();
                    btnDisconnect.setText("연결 해제하기\n"+strDeviceHostName);
                }
                else {
                    rlDeviceContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void refreshEmptyMenuItemHeight(){

        //네비게이션뷰
        NavigationView navigationView = (NavigationView) mActivity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(mActivity::onOptionsItemSelected);

        //메뉴 정보
        int navigationViewMenuHeight = (int) mActivity.getResources().getDimension(R.dimen.drawer_navigationview_item_height);
        int menuCount = navigationView.getMenu().size();

        //기기 화면 높이
        int screenHeight = PixelUtil.getScreenHeightPx(mActivity);

        //헤더 높이 측정
        View headerView = mActivity.getLayoutInflater().inflate(R.layout.nav_header_layout, null, false);
        headerView.measure(0, 0);
        int headerViewHeight = headerView.getMeasuredHeight();


        //하단 기기정보 사이즈 측정
        int deviceContainerHeight = rlDeviceContainer.getMeasuredHeight();
        if(deviceContainerHeight <= 0){
            deviceContainerHeight = PixelUtil.convertDpToPx(mActivity, 140);
        }
        //기기로그인이 안되어 있다면, 기기정보 사이즈를 0으로 설정.
        if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
            deviceContainerHeight = 0;
        }
        final int finalDeviceContainerHeight = deviceContainerHeight;





        //메뉴에 액션뷰 세팅.
        final View emptyView = mActivity.getLayoutInflater().inflate(R.layout.drawer_menu_custom_item, null);
        emptyView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            int originalHeight = 0;

            @Override
            public void onViewAttachedToWindow(View v) {
                View parent = (View) v.getParent();
                if (parent != null) parent = (View) parent.getParent();

                if (parent != null) {
                    ViewGroup.LayoutParams p = parent.getLayoutParams();
                    originalHeight = p.height;

                    int menuHeight = 0;
                    if(RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()){
                        menuHeight = screenHeight - headerViewHeight - finalDeviceContainerHeight - (navigationViewMenuHeight * (menuCount-1));
                    }else{
                        menuHeight = screenHeight - headerViewHeight - (navigationViewMenuHeight * (menuCount-1));
                    }

                    if (menuHeight < 0) {
                        menuHeight = 0;
                    }
                    p.height = menuHeight;
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
        menuItem.setActionView(emptyView);
    }


    public Boolean onOptionsItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);


        //네비게이션 메뉴 아이디에 따른 행동.
        switch (item.getItemId()) {
            case R.id.menu_cowchronicle:
                if(UserStorage.getInstance().isLogin()){
                    goCowScreen(CowChronicleScreenEnum.WEBVIEW, true);
                }
                else{
                    goLoginScreen();
                }
                return true;

            case R.id.menu_readers:
                if(UserStorage.getInstance().isLogin()) {
                    goCowScreen(CowChronicleScreenEnum.FARM_SELECT, true);
                }
                else{
                    goLoginScreen();
                }
                return true;

            case R.id.nav_user_info:
                if(UserStorage.getInstance().isLogin()) {
                    goCowScreen(CowChronicleScreenEnum.USER_INFO, true);
                }
                else{
                    goLoginScreen();
                }
                return true;

            case R.id.nav_battery_statics:
                if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                    CustomDialog.showSimple(mActivity, "연결된 장치가 없어서 실행이 불가합니다.\n전자이표 또는 장치 설정을 눌러서 장치를 연결 후에 다시 시도해 주세요.");
                } else {
                    Intent intent = new Intent(mActivity, ActiveDeviceActivity.class);
                    intent.putExtra(INTENT_START_TAB, SETTINGS_TAB);
                    intent.putExtra(INTENT_NEXT_TAB, BATTERY_STATISTICS_TAB);
                    mActivity.startActivity(intent);
                }
                return true;

            case R.id.nav_fw_update:
                if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                    CustomDialog.showSimple(mActivity, "연결된 장치가 없어서 실행이 불가합니다.\n전자이표 또는 장치 설정을 눌러서 장치를 연결 후에 다시 시도해 주세요.");
                } else{
                    Intent intent = new Intent(mActivity, ActiveDeviceActivity.class);
                    intent.putExtra(INTENT_START_TAB, SETTINGS_TAB);
                    intent.putExtra(INTENT_NEXT_TAB, UPDATE_FIRMWARE_TAB);
                    mActivity.startActivity(intent);
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

    //화면 이동
    protected void goCowScreen(CowChronicleScreenEnum screenEnum, boolean needBackStack){
        if(mActivity instanceof CowChronicleActivity){
            Fragment fragment = null;
            switch (screenEnum){
                case FARM_SELECT:
                    fragment = new FarmSelectFragment();
                    break;
                case WEBVIEW:
                    fragment = new WebviewHomeFragment();
                    break;
                case USER_INFO:
                    fragment = new UserInfoFragment();
                    break;
            }

            ((CowChronicleActivity)mActivity).replaceFragment(fragment, needBackStack);
        }
        else {
            Intent intent = new Intent(mActivity, CowChronicleActivity.class);
            intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, screenEnum.toString());
            mActivity.startActivity(intent);
        }
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



    protected void goLoginScreen(){
        if(!(mActivity instanceof UserLoginActivity)){
            Intent intent = new Intent(mActivity, UserLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mActivity.startActivity(intent);
            mActivity.finishAffinity();
        }else {
            CustomDialog.showSimple(mActivity, "로그인 먼저 해주세요.");
        }
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
        ivBatteryLevel.setImageLevel(batteryLevel);
    }
}
