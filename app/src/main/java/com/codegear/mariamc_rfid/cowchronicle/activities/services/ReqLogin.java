package com.codegear.mariamc_rfid.cowchronicle.activities.services;

import com.google.gson.annotations.SerializedName;

public class ReqLogin {
    @SerializedName("userid") private String userid;
    @SerializedName("password") private String password;
    @SerializedName("snkey") private String snkey;
    @SerializedName("latitude") private String latitude;
    @SerializedName("lontitude") private String lontitude;

    public ReqLogin(String strId, String strPwd){
        this.userid = strId;
        this.password = strPwd;
        this.snkey = "KKEF-33FKE-KLMN";
        this.latitude = "34.1123";
        this.lontitude = "125.3341";
    }

}
