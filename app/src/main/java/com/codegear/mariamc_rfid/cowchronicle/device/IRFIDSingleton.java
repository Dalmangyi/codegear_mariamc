package com.codegear.mariamc_rfid.cowchronicle.device;

import com.zebra.rfid.api3.TagData;

public interface IRFIDSingleton {

    //태그된 데이터
    void tags(TagData[] tagList);

    //트리거 상태 (모드에 따라 다른 반응)
    void trigger(Boolean isPressed, Boolean isReleased);

    //인벤토리 상태 (모드에 따른 다른 반응)
    void inventory(Boolean isStarted, Boolean isStopped, Boolean isFinished);

}
