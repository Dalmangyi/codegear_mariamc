package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.farms.FarmSearchDialogCompat;
import com.codegear.mariamc_rfid.cowchronicle.activities.models.FarmModel;

import java.util.ArrayList;


public class FarmSelectFragment extends Fragment {

    private AppCompatActivity mActivity;
    private View mFarmSelectFragmentView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (AppCompatActivity) getActivity();
        mFarmSelectFragmentView = inflater.inflate(R.layout.fragment_farm_select, null, false);

        mActivity.getSupportActionBar().setTitle("목장선택");


        mFarmSelectFragmentView.findViewById(R.id.btnCurrentFarm).setOnClickListener(this::clickFarmSearch);

        return mFarmSelectFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }




    private void clickFarmSearch(View v) {
        new FarmSearchDialogCompat<>(mActivity, "목장 리스트", "목장 이름을 적어 검색해 보세요.", null, createSampleContacts(), (dialog, item, position) -> {
            Toast.makeText(mActivity, item.getTitle(), Toast.LENGTH_SHORT).show();
            goNextIntent();
            dialog.dismiss();
        }).show();
    }

    private ArrayList<FarmModel> createSampleContacts() {
        ArrayList<FarmModel> items = new ArrayList<>();
        // Thanks to https://randomuser.me for the images
        items.add(new FarmModel("First item", "https://randomuser.me/api/portraits/women/93.jpg"));
        items.add(new FarmModel("Second item", "https://randomuser.me/api/portraits/women/79.jpg"));
//        items.add(new FarmModel("Third item", "https://randomuser.me/api/portraits/women/56.jpg"));
//        items.add(new FarmModel("The ultimate item", "https://randomuser.me/api/portraits/women/44.jpg"));
//        items.add(new FarmModel("Last item", "https://randomuser.me/api/portraits/women/82.jpg"));
//        items.add(new FarmModel("Lorem ipsum", "https://randomuser.me/api/portraits/lego/3.jpg"));
//        items.add(new FarmModel("Dolor sit", "https://randomuser.me/api/portraits/women/60.jpg"));
//        items.add(new FarmModel("Some random word", "https://randomuser.me/api/portraits/women/32.jpg"));
//        items.add(new FarmModel("guess who's back", "https://randomuser.me/api/portraits/women/67.jpg"));
        return items;
    }

    private void goNextIntent(){
        ((CowChronicleActivity)mActivity).replaceFragment(new CowTagFragment(), true);
    }
}
