package com.codegear.mariamc_rfid.cowchronicle.activities.services;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ResCowList {

//    {
//        "rowcnt ": 115,
//        "success": 1,
//        "data" : [
//            {
//                "COW_ID_NUM":"002173840724",
//                "TAGNO":"541100000000465",
//                "KIND":"한우",
//                "SEX":"거세",
//                "SNM":"4072",
//                "BIRTHDT":"2022-05-04",
//                "MONTHS":"16",
//                "PRTY":" ",
//                "PRN_STTS":" ",
//                "BREDKND":"비육우",
//                "REGNO":" "
//            }
//        ]
//    }

    @SerializedName("rowcnt") private int rowcnt;
    @SerializedName("success") private int success;
    @SerializedName("data") private Map<String, String> data;

}
