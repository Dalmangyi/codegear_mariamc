package com.codegear.mariamc_rfid.cowchronicle.storage;

import android.content.Context;

import com.codegear.mariamc_rfid.cowchronicle.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.utils.CryptedKeys;
import com.codegear.mariamc_rfid.cowchronicle.utils.CryptedPrefs;

public class UserStorage {
    private static final UserStorage mInstance = new UserStorage();
    public static UserStorage getInstance() {
        return mInstance;
    }


    private Context mContext;
    private CryptedPrefs mPrefs;




    //저장용 데이터
    private ResLogin mResLogin;

    //임시 데이터
    public String tempSelectTagNum;


    private UserStorage() {
    }

    public void init(Context context){
        mContext = context;
        mPrefs = new CryptedPrefs(mContext);
    }


    public void saveLogin(String userId, String userPwd, boolean isAutoLogin, ResLogin resLogin){
        if(userId != null && userPwd != null){
            mPrefs.setValue(CryptedKeys.LOGIN_USER_ID, userId);
            mPrefs.setValue(CryptedKeys.LOGIN_USER_PWD, userPwd);
            mPrefs.setValue(CryptedKeys.LOGIN_IS_AUTO, isAutoLogin);
        }

        this.mResLogin = resLogin;
    }

    public void doLogout(){
        this.mResLogin = null;
        mPrefs.setValue(CryptedKeys.LOGIN_IS_AUTO, false);
    }

    public String getPrevLoginId(){
        return mPrefs.getValue(CryptedKeys.LOGIN_USER_ID, null);
    }

    public String getPrevLoginPwd(){
        return mPrefs.getValue(CryptedKeys.LOGIN_USER_PWD, null);
    }

    public boolean getPrevLoginIsAuto(){
        return mPrefs.getValue(CryptedKeys.LOGIN_IS_AUTO, false);
    }

    public boolean isLogin(){
        return (this.mResLogin != null);
    }

    public ResLogin getResLogin(){
        return this.mResLogin;
    }



}
