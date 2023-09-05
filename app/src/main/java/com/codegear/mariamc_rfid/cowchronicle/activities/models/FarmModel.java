package com.codegear.mariamc_rfid.cowchronicle.activities.models;

import ir.mirrajabi.searchdialog.core.Searchable;

public class FarmModel implements Searchable {
    private String mName;
    private String mImageUrl;

    public FarmModel(String name) {
        mName = name;
        mImageUrl = null;
    }

    public FarmModel(String name, String imageUrl) {
        mName = name;
        mImageUrl = imageUrl;
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

    public String getImageUrl() {
        return mImageUrl;
    }

    public FarmModel setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        return this;
    }
}