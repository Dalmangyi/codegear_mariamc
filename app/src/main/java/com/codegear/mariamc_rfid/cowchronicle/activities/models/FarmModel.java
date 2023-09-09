package com.codegear.mariamc_rfid.cowchronicle.activities.models;

import ir.mirrajabi.searchdialog.core.Searchable;

public class FarmModel implements Searchable {
    private String mFarmName;
    private String mFarmCode;
    private String mOwnerName;

    public FarmModel(String farmName, String farmCode, String ownerName) {
        mFarmName = farmName;
        mFarmCode = farmCode;
        mOwnerName = ownerName;
    }

    @Override
    public String getTitle() {
        return mFarmName+" - "+mOwnerName;
    }

    public String getName() {
        return mFarmName;
    }

    public FarmModel setName(String name) {
        mFarmName = name;
        return this;
    }

    public String getFarmCode() {
        return mFarmCode;
    }

    public FarmModel setFarmCode(String farmCode) {
        mFarmCode = farmCode;
        return this;
    }

    public String getOwnerName(){
        return mOwnerName;
    }
}