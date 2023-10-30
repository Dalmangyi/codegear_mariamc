package com.codegear.mariamc_rfid.cowchronicle.ui.cowtags;

import com.zebra.rfid.api3.TagData;

public class ExTagData extends TagData {

    public String newTagId;

    @Override
    public String getTagID(){
        if(newTagId != null){
            return newTagId;
        }

        return super.getTagID();
    }
}
