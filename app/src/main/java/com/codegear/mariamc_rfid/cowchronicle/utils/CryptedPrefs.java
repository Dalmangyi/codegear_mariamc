package com.codegear.mariamc_rfid.cowchronicle.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CryptedPrefs {

    private Context mContext;
    private SharedPreferences prefs;



    public static final String IS_AUTO_LOGIN = "is_auto_login";




    public CryptedPrefs(Context context){
        this.mContext = context;

        try {
            this.initPrefs();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void initPrefs() throws GeneralSecurityException, IOException {
        MasterKey masterKeyAlias = new MasterKey.Builder(mContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        prefs = EncryptedSharedPreferences.create(
                mContext, // Context
                "encrypted_pref_file", // 파일 명
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // AES256_SIV으로 key를 암호화
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // AES256_GCM으로 value를 암호화
        );
    }

    public void setValue(String key, String value){
        prefs.edit().putString("s"+key, value).apply();
    }

    public void setValue(String key, boolean value){
        prefs.edit().putBoolean("b"+key, value).apply();
    }

    public String getValue(String key, String defaultValue){
        return prefs.getString("s"+key, defaultValue);
    }

    public boolean getValue(String key, boolean defaultValue){
        return prefs.getBoolean("b"+key, defaultValue);
    }
}
