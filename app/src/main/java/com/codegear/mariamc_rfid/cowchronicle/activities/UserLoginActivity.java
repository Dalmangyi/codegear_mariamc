package com.codegear.mariamc_rfid.cowchronicle.activities;

import static com.codegear.mariamc_rfid.cowchronicle.utils.CryptedPrefs.IS_AUTO_LOGIN;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.ReqLogin;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.RetrofitClient;
import com.codegear.mariamc_rfid.cowchronicle.utils.CryptedPrefs;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomDialog;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {

    private Context mContext;

    EditText etLoginId, etLoginPassword;
    Button btnLoginSignIn;
    CheckBox cbIsAutoLogin;

    AlertDialog dialogLoading;

    CryptedPrefs prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        mContext = this;

        prefs = new CryptedPrefs(getApplicationContext());

        etLoginId = findViewById(R.id.etLoginId);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLoginSignIn = findViewById(R.id.btnLoginSignIn);
        cbIsAutoLogin = findViewById(R.id.cbIsAutoLogin);

        dialogLoading = new SpotsDialog.Builder().setContext(this).build();


        btnLoginSignIn.setOnClickListener(v -> login());
        cbIsAutoLogin.setChecked(prefs.getValue(IS_AUTO_LOGIN, true));
        cbIsAutoLogin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setValue(IS_AUTO_LOGIN, isChecked);
        });
    }

    void login(){
        dialogLoading.show();

        String strId = etLoginId.getText().toString().trim();
        String strPwd = etLoginPassword.getText().toString().trim();
        Boolean isAutoLogin = cbIsAutoLogin.isChecked();

        if(strId.isEmpty() || strPwd.isEmpty()){
            dialogLoading.dismiss();
            CustomDialog.showSimple(this, "아이디 또는 비밀번호를 입력해주세요.");
            return;
        }



        ReqLogin reqLogin = new ReqLogin(strId, strPwd);
        Call<ResLogin> call = RetrofitClient.getApiService().login(reqLogin);

        Log.d("TTTT","hi");
        call.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResLogin> call, @NonNull Response<ResLogin> response) {
                Log.d("TTTT",""+response.code()+","+response.body());
                dialogLoading.dismiss();

                if (!response.isSuccessful()) {
                    CustomDialog.showSimpleError(mContext, "서버 담당자에게 문의해주세요 ("+response.code()+")\n"+response.message());
                    return;
                }


//
//                MaterialDialog dialogMsg = new MaterialDialog(UserLoginActivity.this);
//                dialogMsg.setTitle("");
//                dialogMsg.message("");


            }

            @Override
            public void onFailure(@NonNull Call<ResLogin> call, @NonNull Throwable t) {
                dialogLoading.dismiss();
                CustomDialog.showSimpleError(mContext, t.getMessage());
            }
        });
    }

}
