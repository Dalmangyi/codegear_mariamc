package com.codegear.mariamc_rfid.cowchronicle.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResInsertTagData extends ResCommon {

    /*
    {
        "message": "Data inserted successfully"
    }
     */
    @SerializedName("message")
    public String message;
}
