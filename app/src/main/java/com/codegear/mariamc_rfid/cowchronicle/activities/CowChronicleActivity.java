package com.codegear.mariamc_rfid.cowchronicle.activities;

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

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomConnectedDrawer;

public class CowChronicleActivity extends AppCompatActivity {

    public static final String FLAG_FRAGMENT_START_PAGE = "fragment_start_page";


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
        btnNavigationBottom1.setOnClickListener(v -> replaceFragment(new WebviewFragment(), false));
        btnNavigationBottom2.setOnClickListener(v -> replaceFragment(new FarmSelectFragment(), false));


        initFragment();
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
            CowChronicleFragmentEnum fragmentEnum = CowChronicleFragmentEnum.valueOf(strFragmentStartPage);

            switch(fragmentEnum){
                case WEBVIEW:
                    replaceFragment(new WebviewFragment(), false);
                    break;
                case COW_TAGS:
                    replaceFragment(new CowTagsFragment(), false);
                    break;
                default:
                    replaceFragment(new FarmSelectFragment(), false);
            }
        }
        else {
            replaceFragment(new WebviewFragment(), false);
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
