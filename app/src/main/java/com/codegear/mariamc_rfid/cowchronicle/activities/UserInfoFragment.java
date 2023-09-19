package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomDialog;

import java.util.Map;

public class UserInfoFragment extends Fragment {


    private static final String TAG = "UserInfoFragment";
    private AppCompatActivity mActivity;



    private View mUserInfoFragmentView;

    private TextView tvFarmNames;
    private TextView tvMemberName;
    private Button btnUserInfoLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mActivity = (AppCompatActivity)getActivity();
        mActivity.getSupportActionBar().setTitle("사용자");

        mUserInfoFragmentView = inflater.inflate(R.layout.fragment_user_info, null, false);

        tvFarmNames = mUserInfoFragmentView.findViewById(R.id.tvFarmNames);
        tvMemberName = mUserInfoFragmentView.findViewById(R.id.tvMemberName);
        btnUserInfoLogout = mUserInfoFragmentView.findViewById(R.id.btnUserInfoLogout);
        btnUserInfoLogout.setOnClickListener(v -> {
            goIntentLogin();
        });

        return mUserInfoFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        initUserInfo();
    }

    private void initUserInfo(){

        ResLogin resLogin = UserStorage.getInstance().getResLogin();

        if(resLogin == null){
            CustomDialog.showSimpleError(mActivity, "사용자 정보를 조회할 수 없습니다.");
            return;
        }


        //목장정보
        String strFarmList = "";
        for(Map<String, String> farmItem : resLogin.mConvertedFarmList){
            String farmName = farmItem.get("farm");
            strFarmList += (farmName + "\n");
        }
        tvFarmNames.setText(strFarmList);

        //가입자 정보
        String membership = resLogin.membership_nm;
        String company = resLogin.cmpy;
        String user_name = resLogin.usr_nm;
        String strMemberName = "";
        if(membership == "Premium"){
            //프리미엄 사용자
            strMemberName += user_name + "\n";
            strMemberName += "(프리미엄 회원)";
        }else{
            strMemberName += user_name + "\n";
            strMemberName += "("+membership+" 회원)";
        }

        tvMemberName.setText(strMemberName);
    }

    private void goIntentLogin(){
        mActivity.finishAffinity();
        UserStorage.getInstance().doLogout();
        Intent intent = new Intent(mActivity, UserLoginActivity.class);
        startActivity(intent);
    }
}
