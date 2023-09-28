package com.codegear.mariamc_rfid.cowchronicle.ui.cowtags;

public class CowTagCell {

    private final String EMPTY_VAL = "null";


    public int rowPosition = -1;
    public String COW_ID_NUM = "";              //이력제번호
    public String SNM = "";                     //목장이표
    public String SEX_AND_MONTHS = "";          //성별+월령
    public String PRTY = "";                    //산차
    public String PRN_STTS = "";                //번식상태
    public String TAGNO = "";                   //전자이표
    public int COUNT = 0;                       //태그 입력 횟수
    public int RSSI = 0;                        //태그 신호 세기
    public int PHASE = 0;                       //태그 PHASE
    public int CHANNEL = 0;                     //태크 CHANNEL

    public String READER_SERIAL_NO = EMPTY_VAL;    //리더기 시리얼 넘버
    public String EPC_VAL = EMPTY_VAL;             //메모리뱅크 EPC 값
    public String EPD_VAL = EMPTY_VAL;             //메모리뱅크 EPD 값
    public String TID_VAL = EMPTY_VAL;             //메모리뱅크 TID 값
    public String OTHER_VAL = EMPTY_VAL;           //메모리뱅크 기타 값



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

    public String toDetailString(){
        return "목장이표 : "+this.SNM
        +"\n성별(월령) : "+this.SEX_AND_MONTHS
        +"\n산차 : "+this.PRTY
        +"\n번식상태 : "+this.PRN_STTS
        +"\n전자이표 : "+this.TAGNO
        +"\n"
        +"\n메모리뱅크"
        +"\n ・ TID : "  +(this.TID_VAL == null || this.TID_VAL.equals(EMPTY_VAL) ? "" : this.TID_VAL)
        +"\n ・ EPC : "  +(this.EPC_VAL == null || this.EPC_VAL.equals(EMPTY_VAL) ? "" : this.EPC_VAL)
        +"\n ・ OTHER : "+(this.OTHER_VAL == null || this.OTHER_VAL.equals(EMPTY_VAL) ? "" : this.OTHER_VAL)
        +"\n"
        +"\n태그"
        +"\n ・ 횟수 : "+this.COUNT
        +"\n ・ 신호세기 : "+this.RSSI
        +"\n ・ PHASE : "+this.PHASE
        +"\n ・ CHANNEL : "+this.CHANNEL
        ;
    }


}
