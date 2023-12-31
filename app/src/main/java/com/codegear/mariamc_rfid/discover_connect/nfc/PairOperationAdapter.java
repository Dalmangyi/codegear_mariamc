package com.codegear.mariamc_rfid.discover_connect.nfc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.CameraScanFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.ScanAndPairFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.ScanBarcodeAndPairFragment;

import java.util.HashMap;

public class PairOperationAdapter extends FragmentStatePagerAdapter {

    private static final int NO_OF_TABS = 2;
    String[] tabs = {"직접 입력", "스캔 등록"};
    int[] icons = {R.drawable.ic_tap_and_pair, R.drawable.ic_scan_and_pair};
    Context mContext;
    private HashMap<Integer, Fragment> currentlyActiveFragments;



    public PairOperationAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        this.mContext = ctx;
    }

    @Override
    public Fragment getItem(int index) {

        if (currentlyActiveFragments == null)
            currentlyActiveFragments = new HashMap<>();

        Fragment fragment;

        switch (index) {
            case 0:
                Log.d(getClass().getSimpleName(), "2nd Tab Selected");
                fragment = ScanAndPairFragment.newInstance();
                Bundle bt_scan = new Bundle();
                bt_scan.putBoolean("bt_pair", true);
                fragment.setArguments(bt_scan);
                break;
            case 1:
                fragment = CameraScanFragment.newInstance();
                break;
            default:
                fragment = null;
                break;
        }

        currentlyActiveFragments.put(index, fragment);

        return fragment;
    }

    public Fragment getFragment(int key) {
        if (currentlyActiveFragments != null) {
            return currentlyActiveFragments.get(key);
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        //Remove the reference
        currentlyActiveFragments.remove(position);
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return NO_OF_TABS;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = tabs[position];
        int resId = icons[position];
        Drawable titleIcon = ContextCompat.getDrawable(mContext, resId);
        titleIcon.setBounds(0, 0, titleIcon.getIntrinsicWidth(), titleIcon.getIntrinsicHeight());
        SpannableString spannable = new SpannableString("  " + title);
        ImageSpan imageSpan = new ImageSpan(titleIcon, ImageSpan.ALIGN_BOTTOM);
        spannable.setSpan(new RelativeSizeSpan((float) 0.74), 2, title.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(R.color.black_overlay), 0, title.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

}

