package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.farms.FarmSearchDialogCompat;
import com.codegear.mariamc_rfid.cowchronicle.activities.models.FarmModel;

import java.util.ArrayList;

import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class FarmSelectActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_farm_select);
    }

    private void clickFarmSearch() {
        new FarmSearchDialogCompat<>(
                FarmSelectActivity.this,
                "Search...",
                "What are you looking for...?",
                null,
                createSampleContacts(),
                new SearchResultListener<FarmModel>() {
            @Override
            public void onSelected(BaseSearchDialogCompat dialog, FarmModel item, int position) {
                Toast.makeText(FarmSelectActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).show();
    }

    private ArrayList<FarmModel> createSampleContacts() {
        ArrayList<FarmModel> items = new ArrayList<>();
        // Thanks to https://randomuser.me for the images
        items.add(new FarmModel("First item", "https://randomuser.me/api/portraits/women/93.jpg"));
        items.add(new FarmModel("Second item", "https://randomuser.me/api/portraits/women/79.jpg"));
        items.add(new FarmModel("Third item", "https://randomuser.me/api/portraits/women/56.jpg"));
        items.add(new FarmModel("The ultimate item", "https://randomuser.me/api/portraits/women/44.jpg"));
        items.add(new FarmModel("Last item", "https://randomuser.me/api/portraits/women/82.jpg"));
        items.add(new FarmModel("Lorem ipsum", "https://randomuser.me/api/portraits/lego/3.jpg"));
        items.add(new FarmModel("Dolor sit", "https://randomuser.me/api/portraits/women/60.jpg"));
        items.add(new FarmModel("Some random word", "https://randomuser.me/api/portraits/women/32.jpg"));
        items.add(new FarmModel("guess who's back", "https://randomuser.me/api/portraits/women/67.jpg"));
        return items;
    }
}
