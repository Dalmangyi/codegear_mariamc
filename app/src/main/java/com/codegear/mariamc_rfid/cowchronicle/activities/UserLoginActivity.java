package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.RetrofitClient;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomDisconnectedDrawer;
import com.codegear.mariamc_rfid.cowchronicle.utils.MD5Util;
import com.codegear.mariamc_rfid.scanner.activities.BaseActivity;
import com.permissionx.guolindev.PermissionX;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends BaseActivity {

    private Context mContext;

    private EditText etLoginId, etLoginPassword;
    private Button btnLoginSignIn;
    private CheckBox cbIsAutoLogin;

    private Button btnNavigationBottom1, btnNavigationBottom2;
    final int PAGE_NUM_1 = 1, PAGE_NUM_2 = 2;

    private AlertDialog dialogLoading;
    private CustomDisconnectedDrawer mCustomDrawer;


    private static String TAG = "UserLoginActivity";

    public static UserLoginActivity newInstance() {
        return new UserLoginActivity();
    }

    public UserLoginActivity() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mContext = this;

        dialogLoading = new SpotsDialog.Builder().setContext(mContext).build();

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
            if (btnNavigationBottom1.isSelected()){
                btnNavigationBottom1.setSelected(false);
                btnNavigationBottom2.setSelected(false);
            }
            else{
                btnNavigationBottom1.setSelected(true);
                btnNavigationBottom2.setSelected(false);
            }
        });
        btnNavigationBottom2.setOnClickListener(v -> {
            if (btnNavigationBottom2.isSelected()){
                btnNavigationBottom1.setSelected(false);
                btnNavigationBottom2.setSelected(false);
            }
            else {
                btnNavigationBottom1.setSelected(false);
                btnNavigationBottom2.setSelected(true);
            }
        });

        mCustomDrawer = new CustomDisconnectedDrawer(this);

        reqPermission();
    }

    void reqPermission() {

        PermissionX.init(this)
            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .onExplainRequestReason((scope, deniedList, beforeRequest) -> {
                scope.showRequestReasonDialog(deniedList, "앱을 사용하기 위해 아래 권한 허용이 필요합니다.", "허용");
            })
            .onForwardToSettings((scope, deniedList) -> {
                scope.showForwardToSettingsDialog(deniedList, "설정에서 다음 권한을 허용해주세요", "허용");
            })
            .request((allGranted, grantedList, deniedList) -> {
                if (!allGranted) {
                    Toast.makeText(mContext, "다음 권한들을 허용해주세요.：" + deniedList, Toast.LENGTH_SHORT).show();
                }
                else{
                    //모두 허가되었다면, 자동로그인 시도하기
                    checkAutoLogin();
                }
            });
    }

    void checkAutoLogin(){

        UserStorage userStorage = UserStorage.getInstance();
        boolean isAutoLogin = userStorage.getPrevLoginIsAuto();

        if (isAutoLogin){
            String strLoginId = userStorage.getPrevLoginId();
            String strLoginPwd = userStorage.getPrevLoginPwd();
            login(strLoginId, strLoginPwd, true, PAGE_NUM_1);
        }
    }


    void preLogin() {
        dialogLoading.show();

        String strId = etLoginId.getText().toString().trim();
        String strPwd = etLoginPassword.getText().toString().trim();
        Boolean isAutoLogin = cbIsAutoLogin.isChecked();

        //TODO - DebugCode
        strId = "farmtest2";
        strPwd = "111";

        if(!btnNavigationBottom1.isSelected() && !btnNavigationBottom2.isSelected()){
            dialogLoading.dismiss();
            CustomDialog.showSimple(mContext, R.string.login_need_select_page_button);
            return;
        }
        else if (strId.isEmpty() || strPwd.isEmpty()) {
            dialogLoading.dismiss();
            CustomDialog.showSimple(mContext, "아이디 또는 비밀번호를 입력해주세요.");
            return;
        }

        //페이지 번호
        int pageNum = PAGE_NUM_1;
        if(btnNavigationBottom1.isSelected()){
            pageNum = PAGE_NUM_1;
        }else if(btnNavigationBottom2.isSelected()){
            pageNum = PAGE_NUM_2;
        }

        //로그인
        login(strId, strPwd, isAutoLogin, pageNum);
    }

    private void login(final String strId, final String strPwd, final boolean isAutoLogin, final int movePageNumber) {

        //위치 정보 가져오기
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            reqPermission();
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        //비밀번호 해쉬화
        String strHashedPwd = MD5Util.convert(strPwd);
        //TODO - DebugCode
        strHashedPwd = "408bad4df16b6df0997573f3d65f8f13";

        //Request Query Map
        Map reqMap = new HashMap();
        reqMap.put("userid",strId);
        reqMap.put("password",strHashedPwd);
        reqMap.put("snkey","KKEF-33FKE-KLMN");
        reqMap.put("latitude",""+latitude);
        reqMap.put("lontitude",""+longitude);


        //로그인 시도
        Call<ResLogin> call = RetrofitClient.getApiService().login(reqMap);
        call.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResLogin> call, @NonNull Response<ResLogin> response) {
                Log.e(TAG,"UserLoginActivity LoginAPI success. code:"+response.code()+",res:"+response.body());
                dialogLoading.dismiss();

                if (!response.isSuccessful()) {
                    CustomDialog.showSimpleError(mContext, "서버 담당자에게 문의해주세요. ("+response.code()+")\n"+response.message());
                    return;
                }

                //데이터 변환
                ResLogin resLogin = response.body();
                resLogin.convertData();
                Log.d(TAG,response.toString());

                //데이터 저장
                UserStorage.getInstance().saveLogin(strId, strPwd, isAutoLogin, resLogin);

                //화면 이동
                switch(movePageNumber){
                    case PAGE_NUM_1:
                        goIntentCowChronicle();
                        break;
                    case PAGE_NUM_2:
                        goIntentDeviceDiscover();
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
                    CustomDialog.showSimpleError(mContext, "네트워크 연결을 확인해주세요.");
                }
                else{
                    CustomDialog.showSimpleError(mContext, t.getMessage());
                }

            }
        });
    }

    private void goIntentCowChronicle(){
        finish();
        Intent intent = new Intent(UserLoginActivity.this, WebviewActivity.class);
        startActivity(intent);
    }

    private void goIntentDeviceDiscover(){
        finish();
        Intent intent = new Intent(UserLoginActivity.this, DeviceDiscoverActivity.class);
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
}
