package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.rapidread.MatchModeProgressView;
import com.codegear.mariamc_rfid.rfidreader.rapidread.RapidReadFragment;
import com.codegear.mariamc_rfid.rfidreader.settings.ISettingsUtil;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class UserInfoFragment extends Fragment {


    private static UserInfoFragment mUserInfoFragment = null;
    private static final String TAG = "UserInfoFragment";

    public UserInfoFragment() {}
    public static UserInfoFragment newInstance() {
        mUserInfoFragment = new UserInfoFragment();
        return mUserInfoFragment;
    }



    private View mUserInfoFragmentView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mUserInfoFragmentView = inflater.inflate(R.layout.fragment_user_info, null, false);

        return mUserInfoFragmentView;
    }
}
