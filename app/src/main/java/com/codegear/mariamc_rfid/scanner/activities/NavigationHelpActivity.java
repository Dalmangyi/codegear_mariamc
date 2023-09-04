package com.codegear.mariamc_rfid.scanner.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codegear.mariamc_rfid.BuildConfig;
import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.scanner.helpers.Foreground;
import com.codegear.mariamc_rfid.R;

public class NavigationHelpActivity extends AppCompatActivity {
    private static final String TAG = "NavigationHelpActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_activity_help);
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


        Toolbar subActionBar = (Toolbar) findViewById(R.id.sub_actionbar);
        setSupportActionBar(subActionBar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Help");
        }
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        }
        String version;
        if (pInfo != null) {
            version = BuildConfig.VERSION_NAME;
            ((TextView) findViewById(R.id.about)).setText(getResources().getString(R.string.app_name) + " Application v" + version + "\n\n" + "SDK version " + com.zebra.rfid.api3.BuildConfig.VERSION_NAME + "\n\n" + "\u00a9 All rights reserved.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.no_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isInBackgroundMode(getApplicationContext())) {
            finish();
        }
    }

    public boolean isInBackgroundMode(final Context context) {
        return Foreground.get().isBackground();
    }
}
