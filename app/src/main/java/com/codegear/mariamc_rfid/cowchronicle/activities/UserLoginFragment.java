package com.codegear.mariamc_rfid.cowchronicle.activities;

import static com.codegear.mariamc_rfid.cowchronicle.utils.CryptedPrefs.IS_AUTO_LOGIN;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.RetrofitClient;
import com.codegear.mariamc_rfid.cowchronicle.utils.CryptedPrefs;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.MD5Util;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.scanner.fragments.SettingsFragment;
import com.permissionx.guolindev.PermissionX;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginFragment extends Fragment {

    private Context mContext;

    EditText etLoginId, etLoginPassword;
    Button btnLoginSignIn;
    CheckBox cbIsAutoLogin;

    AlertDialog dialogLoading;

    CryptedPrefs prefs;


    private View userLoginFragmentView;
    private static String TAG = "UserLoginFragment";

    public static UserLoginFragment newInstance() {
        return new UserLoginFragment();
    }

    public UserLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userLoginFragmentView = inflater.inflate(R.layout.activity_user_login, container, false);
        mContext = userLoginFragmentView.getContext();

        prefs = new CryptedPrefs(mContext);
        dialogLoading = new SpotsDialog.Builder().setContext(mContext).build();

        etLoginId = userLoginFragmentView.findViewById(R.id.etLoginId);
        etLoginPassword = userLoginFragmentView.findViewById(R.id.etLoginPassword);
        btnLoginSignIn = userLoginFragmentView.findViewById(R.id.btnLoginSignIn);
        cbIsAutoLogin = userLoginFragmentView.findViewById(R.id.cbIsAutoLogin);


        btnLoginSignIn.setOnClickListener(v -> preLogin());
        cbIsAutoLogin.setChecked(prefs.getValue(IS_AUTO_LOGIN, true));
        cbIsAutoLogin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setValue(IS_AUTO_LOGIN, isChecked);
        });


        reqPermission();

        return userLoginFragmentView;
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
            });
    }

    void preLogin() {
        dialogLoading.show();

        String strId = etLoginId.getText().toString().trim();
        String strPwd = etLoginPassword.getText().toString().trim();
        Boolean isAutoLogin = cbIsAutoLogin.isChecked();

        //TODO - DebugCode
        strId = "farmtest2";
        strPwd = "111";

        if (strId.isEmpty() || strPwd.isEmpty()) {
            dialogLoading.dismiss();
            CustomDialog.showSimple(mContext, "아이디 또는 비밀번호를 입력해주세요.");
            return;
        }

        //비밀번호 해쉬화
        String strHashedPwd = MD5Util.convert(strPwd);

        //TODO - DebugCode
        strHashedPwd = "408bad4df16b6df0997573f3d65f8f13";

        //로그인
        login(strId, strHashedPwd);
    }

    private void login(String strId, String password) {

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

        //Request Query Map
        Map reqMap = new HashMap();
        reqMap.put("userid",strId);
        reqMap.put("password",password);
        reqMap.put("snkey","KKEF-33FKE-KLMN");
        reqMap.put("latitude",""+latitude);
        reqMap.put("lontitude",""+longitude);


        //로그인 시도
        Call<ResLogin> call = RetrofitClient.getApiService().login(reqMap);
        call.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResLogin> call, @NonNull Response<ResLogin> response) {
                Log.d("TTTT",""+response.code()+","+response.body());
                dialogLoading.dismiss();

                if (!response.isSuccessful()) {
                    CustomDialog.showSimpleError(mContext, "서버 담당자에게 문의해주세요 ("+response.code()+")\n"+response.message());
                    return;
                }

                response.body().convertData();
                Log.d("TTTT",response.toString());
//
//                MaterialDialog dialogMsg = new MaterialDialog(UserLoginActivity.this);
//                dialogMsg.setTitle("");
//                dialogMsg.message("");


            }

            @Override
            public void onFailure(@NonNull Call<ResLogin> call, @NonNull Throwable t) {
                Log.e("TTTT","UserLoginActivity LoginAPI onFailure. "+t.getMessage());
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

}
