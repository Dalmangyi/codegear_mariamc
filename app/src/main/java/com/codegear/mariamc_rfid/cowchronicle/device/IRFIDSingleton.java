package com.codegear.mariamc_rfid.cowchronicle.device;

import com.zebra.rfid.api3.TagData;

public interface IRFIDSingleton {

    //태그된 데이터
    void tags(TagData[] tagList);

    //트리거 상태 (isPress:눌렸을때, isRelease:풀렸을때)
    void trigger(Boolean isPress, Boolean isRelease);
}
