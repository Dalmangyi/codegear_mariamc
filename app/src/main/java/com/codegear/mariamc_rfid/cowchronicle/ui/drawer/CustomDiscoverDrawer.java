package com.codegear.mariamc_rfid.cowchronicle.ui.drawer;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.CowChronicleActivity;
import com.codegear.mariamc_rfid.cowchronicle.ui.activities.UserLoginActivity;
import com.codegear.mariamc_rfid.cowchronicle.consts.CowChronicleScreenEnum;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.PixelUtil;
import com.google.android.material.navigation.NavigationView;

public class CustomDiscoverDrawer {

    private AppCompatActivity mActivity;
    private DrawerLayout mDrawerLayout;

    public CustomDiscoverDrawer(AppCompatActivity activity){
        mActivity = activity;

        Toolbar toolbar = (Toolbar)mActivity.findViewById(R.id.dis_toolbar);
        mActivity.setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout)activity.findViewById(R.id.discover_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState(); //ActionBarDrawerToggle 누를때 앱 종료되는 현상 막기.

        //사이즈 측정
        View vDisMainMenu = mActivity.getLayoutInflater().inflate(R.layout.main_menu, null, false);
        vDisMainMenu.measure(0, 0);
        int disMainMenuHeight = vDisMainMenu.getMeasuredHeight();
        int screenHeight = PixelUtil.getScreenHeightPx(mActivity);
        int navigationViewMenuHeight = (int) mActivity.getResources().getDimension(R.dimen.drawer_navigationview_item_height);

        //NavigationView의 특정 Menu에 사이즈 적용
        NavigationView navigationView = (NavigationView) mActivity.findViewById(R.id.disnav_view);
        navigationView.setNavigationItemSelectedListener(mActivity::onOptionsItemSelected);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.menu_empty);
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
                    CustomDialog.showSimple(mActivity, "장치를 연결 후 진행 하실 수 있습니다.");
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
                    CustomDialog.showSimple(mActivity, "장치를 연결을 진행해주세요.");
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
        mActivity.finishAffinity();
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
