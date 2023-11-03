package com.codegear.mariamc_rfid.cowchronicle.ui.cowtags;

import com.codegear.mariamc_rfid.cowchronicle.consts.MemoryBankIdEnum;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.MEMORY_BANK;
import com.zebra.rfid.api3.TagData;

public class ExTagData extends TagData {

    public String newTagId;
    private TagData tagData;


    public void setTagData(TagData tagData) {
        this.tagData = tagData;
    }

    @Override
    public String getTagID(){
        if(newTagId != null){
            return newTagId;
        }

        return tagData.getTagID();
    }

    @Override
    public short getPeakRSSI() {
        return tagData.getPeakRSSI();
    }

    @Override
    public short getChannelIndex() {
        return tagData.getChannelIndex();
    }

    @Override
    public short getPhase() {
        return tagData.getPhase();
    }

    @Override
    public ACCESS_OPERATION_CODE getOpCode() {
        return tagData.getOpCode();
    }

    public ACCESS_OPERATION_STATUS getOpStatus() {
        return tagData.getOpStatus();
    }

    @Override
    public MEMORY_BANK getMemoryBank() {
        return tagData.getMemoryBank();
    }


    @Override
    public String getMemoryBankData() {
        return tagData.getMemoryBankData();
    }

}
