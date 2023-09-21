package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.consts.CowChronicleScreenEnum;
import com.codegear.mariamc_rfid.cowchronicle.device.RFIDSingleton;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.drawer.CustomConnectedDrawer;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;

public class CowChronicleActivity extends AppCompatActivity {

    public static final String FLAG_WEB_URL = "activity_flag_web_url";
    public static final String FLAG_FRAGMENT_START_PAGE = "fragment_start_page";

    private RFIDSingleton rfidSingleton = RFIDSingleton.getInstance();

    private CustomConnectedDrawer mCustomDrawer;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cowchronicle);

        mCustomDrawer = new CustomConnectedDrawer(this);
        mFragmentManager = getSupportFragmentManager();

        Button btnNavigationBottom1 = findViewById(R.id.btnNavigationBottom1);
        Button btnNavigationBottom2 = findViewById(R.id.btnNavigationBottom2);
        btnNavigationBottom1.setOnClickListener(v -> {
            replaceFragment(new WebviewHomeFragment(), false);
        });
        btnNavigationBottom2.setOnClickListener(v -> {
            if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                goDeviceDiscoverActivity();
            } else{
                replaceFragment(new FarmSelectFragment(), false);
            }
        });

        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rfidSingleton.init();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
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

    private void initFragment(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String strFragmentStartPage = bundle.getString(FLAG_FRAGMENT_START_PAGE);
            CowChronicleScreenEnum fragmentEnum = CowChronicleScreenEnum.valueOf(strFragmentStartPage);

            switch(fragmentEnum){
                case WEBVIEW:
                    replaceFragment(new WebviewHomeFragment(), false);
                    break;
                case COW_TAGS:
                    replaceFragment(new CowTagsFragment(), false);
                    break;
                case COW_TAG_DETAIL:
                    replaceFragment(new WebviewCowDetailFragment(), true);
                    break;
                case USER_INFO:
                    replaceFragment(new UserInfoFragment(), false);
                default:
                    replaceFragment(new FarmSelectFragment(), false);
            }
        }
        else {
            replaceFragment(new WebviewHomeFragment(), false);
        }
    }

    public void replaceFragment(Fragment fragment, boolean needBackStack){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.flFragment, fragment);
        if(needBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void goDeviceDiscoverActivity(){
        Intent intent = new Intent(this, DeviceDiscoverActivity.class);

        //로그인 경우만 자동연결,
        if(UserStorage.getInstance().isLogin()){
            intent.putExtra(DeviceDiscoverActivity.ENABLE_AUTO_CONNECT_DEVICE, true); //자동연결 하기.
            intent.putExtra(DeviceDiscoverActivity.DESTINATION_SCREEN_IS_COWCHRONICLE, true); //연결후 카우크로니클로 가게 하기.
            intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.FARM_SELECT.toString());
        }
        else {
            intent.putExtra(DeviceDiscoverActivity.ENABLE_AUTO_CONNECT_DEVICE, true); //자동연결 끄기
            intent.putExtra(DeviceDiscoverActivity.DESTINATION_SCREEN_IS_COWCHRONICLE, false); //연결후 카우크로니클로 가지 않게 하기.
        }
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
