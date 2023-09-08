package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomConnectedDrawer;

public class CowChronicleActivity extends AppCompatActivity {

    private CustomConnectedDrawer mCustomDrawer;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cowchronicle);

        mCustomDrawer = new CustomConnectedDrawer(this);
        mFragmentManager = getSupportFragmentManager();

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
        replaceFragment(new FarmSelectFragment(), false);
    }

    public void replaceFragment(Fragment fragment, boolean needBackStack){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.flFragment, fragment);
        if(needBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }


}
