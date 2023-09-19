package com.codegear.mariamc_rfid.cowchronicle.ui.cowtags;

public class CowTagCell {

    public String COW_ID_NUM = "";
    public String SNM = "";
    public String SEX_AND_MONTHS = "";
    public String PRTY = "";
    public String PRN_STTS = "";
    public String TAGNO = "";
    public int COUNT = 0;
    public int RSSI = 0;



    public void setValue(String key, String value){
        switch (key){
            case "COW_ID_NUM":
                this.COW_ID_NUM = value;
                break;
            case "SNM":
                this.SNM = value;
                break;
            case "SEX+MONTHS":
                this.SEX_AND_MONTHS = value;
                break;
            case "PRTY":
                this.PRTY = value;
                break;
            case "PRN_STTS":
                this.PRN_STTS = value;
                break;
            case "TAGNO":
                this.TAGNO = value;
                break;
        }
    }

    public void setValue(String key, int value){
        switch(key){
            case "COUNT":
                this.COUNT = value;
                break;
            case "RSSI":
                this.RSSI = value;
                break;
        }
    }


}
