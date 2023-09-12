package com.codegear.mariamc_rfid.cowchronicle.device;

import com.zebra.rfid.api3.TagData;

public interface IRFIDSingletonTag {
    void tags(TagData[] tagList);
}
