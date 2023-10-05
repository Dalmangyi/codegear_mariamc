package com.codegear.mariamc_rfid.cowchronicle.ui.screens;

import static com.codegear.mariamc_rfid.cowchronicle.ui.screens.CowChronicleActivity.FLAG_FRAGMENT_START_PAGE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.codegear.mariamc_rfid.BuildConfig;
import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.consts.BottomNavEnum;
import com.codegear.mariamc_rfid.cowchronicle.consts.CowChronicleScreenEnum;
import com.codegear.mariamc_rfid.cowchronicle.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.services.RetrofitClient;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.drawer.CustomConnectedDrawer;
import com.codegear.mariamc_rfid.cowchronicle.utils.AndroidUtil;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.GPSTracker;
import com.codegear.mariamc_rfid.cowchronicle.utils.MD5Util;
import com.codegear.mariamc_rfid.cowchronicle.utils.PermissionUtil;
import com.codegear.mariamc_rfid.scanner.activities.BaseActivity;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends BaseActivity {

    //DATA
    private static String TAG = "UserLoginActivity";
    private Context mContext;
    private GPSTracker gpsTracker;




    //UI
    private EditText etLoginId, etLoginPassword;
    private Button btnLoginSignIn;
    private CheckBox cbIsAutoLogin;
    private Button btnNavigationBottom1, btnNavigationBottom2;
    private AlertDialog dialogLoading;
    private CustomConnectedDrawer mCustomDrawer;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mContext = this;
        gpsTracker = new GPSTracker(mContext);

        dialogLoading = new SpotsDialog.Builder()
                .setContext(mContext)
                .setTheme(R.style.CustomAlertDialog)
                .build();

        etLoginId = findViewById(R.id.etLoginId);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLoginSignIn = findViewById(R.id.btnLoginSignIn);
        btnLoginSignIn.setOnClickListener(v -> preLogin());
        cbIsAutoLogin = findViewById(R.id.cbIsAutoLogin);



        btnNavigationBottom1 = findViewById(R.id.btnNavigationBottom1);
        btnNavigationBottom2 = findViewById(R.id.btnNavigationBottom2);
        btnNavigationBottom1.setSelected(false);
        btnNavigationBottom2.setSelected(false);
        btnNavigationBottom1.setOnClickListener(v -> {
            if (btnNavigationBottom1.isSelected()) {
                btnNavigationBottom1.setSelected(false);
                btnNavigationBottom2.setSelected(false);
            } else {
                btnNavigationBottom1.setSelected(true);
                btnNavigationBottom2.setSelected(false);
            }
        });
        btnNavigationBottom2.setOnClickListener(v -> {
            if (btnNavigationBottom2.isSelected()) {
                btnNavigationBottom1.setSelected(false);
                btnNavigationBottom2.setSelected(false);
            } else {
                btnNavigationBottom1.setSelected(false);
                btnNavigationBottom2.setSelected(true);
            }
        });
        switch(UserStorage.getInstance().getBottomNavItem()){
            case BN_COW_CHRONICLE_WEBVIEW:
                btnNavigationBottom1.setSelected(true);
                break;
            case BN_COW_TAGS:
                btnNavigationBottom2.setSelected(true);
                break;
        }

        //Drawer, ActionBar 세팅
        mCustomDrawer = new CustomConnectedDrawer(this);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        //GPS 권한 요청
        PermissionUtil.reqPermissions(this, () -> {
            //모두 허가되었다면, 자동로그인 시도하기
            checkAutoLogin();
        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);


        //기기 고유값
        String deviceId = AndroidUtil.getDeviceId(this);
        Log.d(TAG, "DeviceId : "+deviceId);
    }


    void checkAutoLogin() {

        UserStorage userStorage = UserStorage.getInstance();
        boolean isAutoLogin = userStorage.getPrevLoginIsAuto();

        if (isAutoLogin) {
            String strLoginId = userStorage.getPrevLoginId();
            String strLoginPwd = userStorage.getPrevLoginPwd();
            login(strLoginId, strLoginPwd, true, UserStorage.getInstance().getBottomNavItem());
        }
    }


    void preLogin() {
        dialogLoading.show();

        String strId = etLoginId.getText().toString().trim();
        String strPwd = etLoginPassword.getText().toString().trim();
        boolean isAutoLogin = cbIsAutoLogin.isChecked();


        if(BuildConfig.DEBUG){
            strId = "chalet2cha";
            strPwd = "1519";
        }

        if (strId.isEmpty() || strPwd.isEmpty()) {
            dialogLoading.dismiss();
            CustomDialog.showSimple(mContext, "아이디 또는 비밀번호를 입력해 주세요.");
            return;
        }
        if (!btnNavigationBottom1.isSelected() && !btnNavigationBottom2.isSelected()) {
            dialogLoading.dismiss();
            CustomDialog.showSimple(mContext, R.string.login_need_select_page_button);
            return;
        }

        //페이지 번호
        BottomNavEnum bottomNavEnum = BottomNavEnum.BN_COW_TAGS;
        if (btnNavigationBottom1.isSelected()) {
            bottomNavEnum = BottomNavEnum.BN_COW_CHRONICLE_WEBVIEW;
        } else if (btnNavigationBottom2.isSelected()) {
            bottomNavEnum = BottomNavEnum.BN_COW_TAGS;
        }

        //로그인
        login(strId, strPwd, isAutoLogin, bottomNavEnum);
    }

    private void login(String strId, String strPwd, final boolean isAutoLogin, final BottomNavEnum bottomNavEnum) {

        //위치 정보 가져오기
        if(!gpsTracker.getIsGPSTrackingEnabled()){
            gpsTracker.showSettingsAlert();
            return;
        }


        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        //비밀번호 해쉬화
        String strHashedPwd = MD5Util.convert(strPwd);

        //Request Query Map
        Map reqMap = new HashMap();
        reqMap.put("userid",strId);
        reqMap.put("password",strHashedPwd);
        reqMap.put("snkey", AndroidUtil.getDeviceId(mContext));
        reqMap.put("latitude",""+latitude);
        reqMap.put("lontitude",""+longitude);


        //로그인 시도
        Call<ResLogin> call = RetrofitClient.getApiService().login(reqMap);
        call.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResLogin> call, @NonNull Response<ResLogin> response) {
                Log.e(TAG,"UserLoginActivity LoginAPI success. url:"+response.raw().request().url()+"code:"+response.code()+",res:"+response.body());

                dialogLoading.dismiss();

                if (!response.isSuccessful()) {

                    String errMessage = "";
                    try {
                        errMessage = response.errorBody().string();
                    } catch (IOException e) {
//                        throw new RuntimeException(e);
                    }
                    CustomDialog.showSimpleError(mContext, "서버 담당자에게 문의해 주세요. ("+response.code()+")\n"+errMessage);
                    return;
                }

                //데이터 변환
                ResLogin resLogin = response.body();
                resLogin.convertData();
                Log.d(TAG,response.toString());

                //데이터 저장
                UserStorage.getInstance().saveLogin(strId, strPwd, isAutoLogin, resLogin);

                //화면 이동
                switch(bottomNavEnum){
                    case BN_COW_CHRONICLE_WEBVIEW:
                        goIntentCowChronicle();
                        break;
                    case BN_COW_TAGS:
                        goIntentFarmSelect();
                        break;
                    default:
                        CustomDialog.showSimpleError(mContext, "잘못된 접근입니다.");
                        finish();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResLogin> call, @NonNull Throwable t) {
                Log.e(TAG,"UserLoginActivity LoginAPI onFailure. "+t.getMessage());
                dialogLoading.dismiss();

                if (t instanceof UnknownHostException){
                    CustomDialog.showSimpleError(mContext, "네트워크 연결을 확인해 주세요.");
                }
                else{
                    CustomDialog.showSimpleError(mContext, t.getMessage());
                }

            }
        });
    }

    //카우크로니클 웹뷰로 이동
    private void goIntentCowChronicle(){
        Intent intent = new Intent(this, CowChronicleActivity.class);
        intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.WEBVIEW.toString());
        startActivity(intent);
    }


    //장치검색 화면으로 이동
    private void goIntentDeviceDiscover(){
        Intent intent = new Intent(this, DeviceDiscoverActivity.class);
        intent.putExtra(DeviceDiscoverActivity.DESTINATION_SCREEN_IS_COWCHRONICLE, true); //연결후 카우크로니클로 가게 하기
        intent.putExtra(DeviceDiscoverActivity.ENABLE_AUTO_CONNECT_DEVICE, true); //자동연결 켜기
        startActivity(intent);
    }

    //목장 선택 화면으로 이동
    private void goIntentFarmSelect(){
        Intent intent = new Intent(this, CowChronicleActivity.class);
        intent.putExtra(FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.FARM_SELECT.toString());
        startActivity(intent);
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Boolean ret = mCustomDrawer.onOptionsItemSelected(item);
        if (ret != null){
            return ret;
        }

        return super.onOptionsItemSelected(item);
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
