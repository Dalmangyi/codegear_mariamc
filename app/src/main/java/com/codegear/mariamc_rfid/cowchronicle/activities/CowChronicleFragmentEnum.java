package com.codegear.mariamc_rfid.cowchronicle.activities;

public enum CowChronicleFragmentEnum {

    FARM_SELECT("FARM_SELECT"),
    WEBVIEW("WEBVIEW"),
    COW_TAGS("COW_TAGS");



    private final String name;
    private CowChronicleFragmentEnum(String str){
        name = str;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString(){
        return this.name;
    }
}
