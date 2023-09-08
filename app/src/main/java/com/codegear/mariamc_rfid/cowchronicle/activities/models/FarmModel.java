package com.codegear.mariamc_rfid.cowchronicle.activities.models;

import ir.mirrajabi.searchdialog.core.Searchable;

public class FarmModel implements Searchable {
    private String mName;
    private String mFarmCode;

    public FarmModel(String name, String farmCode) {
        mName = name;
        mFarmCode = farmCode;
    }

    @Override
    public String getTitle() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public FarmModel setName(String name) {
        mName = name;
        return this;
    }

    public String getFarmCode() {
        return mFarmCode;
    }

    public FarmModel setFarmCode(String farmCode) {
        mFarmCode = farmCode;
        return this;
    }
}