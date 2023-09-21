package com.codegear.mariamc_rfid.cowchronicle.services;

import com.codegear.mariamc_rfid.cowchronicle.ui.cowtags.CowTagCell;
import com.google.gson.annotations.SerializedName;

public class ReqInsertTagData {

        @SerializedName("TAGNO") public String TAGNO = "";
        @SerializedName("READER_SERIAL_NO") public String READER_SERIAL_NO = "";
        @SerializedName("EPD_VAL") public String EPD_VAL = "";
        @SerializedName("EPC_VAL") public String EPC_VAL = "";
        @SerializedName("TID_VAL") public String TID_VAL = "";
        @SerializedName("OTHER_VAL") public String OTHER_VAL = "";

        @SerializedName("RSSI") public int RSSI = -1;
        @SerializedName("PHASE") public int PHASE = -1;
        @SerializedName("CHANNEL") public int CHANNEL = -1;
        @SerializedName("CNT") public int CNT = -1;

        public ReqInsertTagData(){
        }

        public ReqInsertTagData(CowTagCell cell){
                this.TAGNO = cell.TAGNO;
                this.RSSI = cell.RSSI;
                this.CNT = cell.COUNT;
                this.CHANNEL = cell.CHANNEL;
                this.PHASE = cell.PHASE;
                this.EPC_VAL = cell.EPC_VAL;
                this.EPD_VAL = cell.EPD_VAL;
                this.TID_VAL = cell.TID_VAL;
                this.OTHER_VAL = cell.OTHER_VAL;
                this.READER_SERIAL_NO = cell.READER_SERIAL_NO;
        }
}
