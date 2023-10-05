package com.codegear.mariamc_rfid.cowchronicle.ui.drawer;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.consts.BottomNavEnum;
import com.codegear.mariamc_rfid.cowchronicle.ui.screens.CowChronicleActivity;
import com.codegear.mariamc_rfid.cowchronicle.ui.screens.UserLoginActivity;
import com.codegear.mariamc_rfid.cowchronicle.consts.CowChronicleScreenEnum;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.PixelUtil;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.google.android.material.navigation.NavigationView;

public class CustomDiscoverDrawer {

    private final String TAG = "CustomDiscoverDrawer";

    private AppCompatActivity mActivity;
    private DrawerLayout mDrawerLayout;

    public CustomDiscoverDrawer(AppCompatActivity activity) {
        mActivity = activity;

        //툴바, 네비게이션바 세팅
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.dis_toolbar);
        mActivity.setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
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
                Log.d(TAG, "onDrawerStateChanged newState:" + newState);

                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        // starts opening
                        refreshDrawerLayout();
                    }
                }
            }

            //Drawer Layout 새로고침 (메뉴 사이즈, 연결상태, 배터리 정보, 기기 이름 등)
            private void refreshDrawerLayout() {
                refreshEmptyMenuItemHeight();
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

                    int menuHeight = screenHeight - headerViewHeight - (navigationViewMenuHeight * (menuCount-1));

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

        //네비게이션 메뉴 아이디에 따른 행동.
        switch (item.getItemId()) {
            case R.id.menu_cowchronicle:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(UserStorage.getInstance().isLogin()){
                    goCowChronicleWebview();
                }else{
                    goUserLoginActivity();
                }
                return true;
            case R.id.menu_readers:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(UserStorage.getInstance().isLogin()){
                    goCowChronicleFarmSelect();
                }else{
                    goUserLoginActivity();
                }
                return true;
            case R.id.nav_user_info:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(UserStorage.getInstance().isLogin()){
                    goCowChronicleUserInfo();
                }else{
                    goUserLoginActivity();
                }
                return true;

            case R.id.nav_battery_statics:
            case R.id.nav_fw_update:
                CustomDialog.showSimple(mActivity, "장치 설정에서 장치를 연결 후 진행 하실 수 있습니다.");
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_settings:
                if(mActivity instanceof UserLoginActivity){
                    Intent intent = new Intent(mActivity, DeviceDiscoverActivity.class);
                    intent.putExtra(DeviceDiscoverActivity.ENABLE_AUTO_CONNECT_DEVICE, true); //자동연결 끄기
                    intent.putExtra(DeviceDiscoverActivity.DESTINATION_SCREEN_IS_COWCHRONICLE, false); //연결후 카우크로니클로 가지 않게 하기.
                    mActivity.startActivity(intent);
                }
                else if(mActivity instanceof DeviceDiscoverActivity){
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                else {
                    CustomDialog.showSimple(mActivity, "장치를 연결을 진행해 주세요.");
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;

            default:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return null;
        }
    }


    //네비게이션 기능을 사용하기 전에, 로그인 먼저 해야됨을 안내.
    private void goUserLoginActivity(){
        Intent intent = new Intent(mActivity, UserLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(intent);
    }

    //카우크로니클 웹뷰로 이동하기
    private void goCowChronicleWebview(){
        Intent intent = new Intent(mActivity, CowChronicleActivity.class);
        intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.WEBVIEW.toString());
        mActivity.startActivity(intent);
    }

    //카우크로니클 농장선택으로 이동하기
    private void goCowChronicleFarmSelect(){
        Intent intent = new Intent(mActivity, CowChronicleActivity.class);
        intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.FARM_SELECT.toString());
        mActivity.startActivity(intent);
    }

    //카우크로니클 사용자 정보로 이동하기
    private void goCowChronicleUserInfo(){
        Intent intent = new Intent(mActivity, CowChronicleActivity.class);
        intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.USER_INFO.toString());
        mActivity.startActivity(intent);
    }
}
