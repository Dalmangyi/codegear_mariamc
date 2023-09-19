package com.codegear.mariamc_rfid.cowchronicle.services;

import com.google.gson.annotations.SerializedName;

public class ReqInsertTagData {

        @SerializedName("TAGNO") private String TAGNO;
        @SerializedName("READER_SERIAL_NO") private String READER_SERIAL_NO;
        @SerializedName("EPD_VAL") private String EPD_VAL;
        @SerializedName("TID_VAL") private String TID_VAL;
        @SerializedName("OTHER_VAL") private String OTHER_VAL;

        @SerializedName("RSSI") private int RSSI;
        @SerializedName("PHASE") private int PHASE;
        @SerializedName("CHANNEL") private int CHANNEL;
        @SerializedName("CNT") private int CNT;
}
