package com.codegear.mariamc_rfid.cowchronicle.utils;

import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mConnectedReader;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.UserLoginActivity;
import com.codegear.mariamc_rfid.cowchronicle.activities.WebviewFragment;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingsContent;
import com.codegear.mariamc_rfid.rfidreader.settings.SettingsDetailActivity;
import com.google.android.material.navigation.NavigationView;

public class CustomDisconnectedDrawer {

    private AppCompatActivity mActivity;
    private DrawerLayout mDrawerLayout;

    public CustomDisconnectedDrawer(AppCompatActivity activity){
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
                Intent webviewIntent = new Intent(mActivity, WebviewFragment.class);
                webviewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(webviewIntent);
                return true;

            case R.id.menu_readers:
                return true;

            case R.id.nav_user_info:
                return true;

            case R.id.nav_battery_statics:
                Intent detailsIntent = new Intent(mActivity, SettingsDetailActivity.class);
                detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                detailsIntent.putExtra(com.codegear.mariamc_rfid.rfidreader.common.Constants.SETTING_ITEM_ID, Integer.parseInt(SettingsContent.ITEMS.get(3).id));
                mActivity.startActivity(detailsIntent);
                return true;

            case R.id.nav_fw_update:
                if (mConnectedReader != null || !mConnectedReader.isConnected()) {
                    CustomDialog.showSimple(mActivity, "연결된 장치가 없습니다.");
                }
                return true;

            case R.id.nav_settings:
                return true;

            default:
                return null;
        }
    }
}
