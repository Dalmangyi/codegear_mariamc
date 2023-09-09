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
import com.codegear.mariamc_rfid.cowchronicle.activities.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.utils.SoundSearcher;

import java.util.ArrayList;
import java.util.Map;

import ir.mirrajabi.searchdialog.core.BaseFilter;


public class FarmSelectFragment extends Fragment {

    private AppCompatActivity mActivity;
    private View mFarmSelectFragmentView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (AppCompatActivity) getActivity();
        mFarmSelectFragmentView = inflater.inflate(R.layout.fragment_farm_select, null, false);

        mActivity.getSupportActionBar().setTitle("목장선택");


        mFarmSelectFragmentView.findViewById(R.id.btnCurrentFarm).setOnClickListener(this::showFarmSearchDialog);

        return mFarmSelectFragmentView;
    }


    private void showFarmSearchDialog(View v) {

        ArrayList<FarmModel> mFarmList = createSampleContacts();


        FarmSearchDialogCompat searchDialogCompat = new FarmSearchDialogCompat<FarmModel>(
            mActivity,
            "목장 리스트", "목장 이름을 적어 검색해 보세요.",
            null,
            mFarmList,
            (dialog, item, position) -> {
                goNextIntent(item.getFarmCode());
                dialog.dismiss();
            }
        );

        BaseFilter apiFilter = new BaseFilter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                doBeforeFiltering();
                FilterResults results = new FilterResults();
                results.values = new ArrayList<FarmModel>();
                results.count = 0;

                ArrayList<FarmModel> filteredList = new ArrayList<FarmModel>();
                for(FarmModel farmModel: mFarmList){
                    boolean isMatch = SoundSearcher.matchString(farmModel.getName(), charSequence.toString());
                    if (isMatch){
                        filteredList.add(farmModel);
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null) {
                    ArrayList<FarmModel> filtered = (ArrayList<FarmModel>) filterResults.values;
                    if (filtered != null) {
                        searchDialogCompat.getFilterResultListener().onFilter(filtered);
                    }
                    doAfterFiltering();
                }
            }
        };
        searchDialogCompat.setFilter(apiFilter).show();
    }

    private ArrayList<FarmModel> createSampleContacts() {

        ArrayList<FarmModel> items = new ArrayList<>();

        //로그인 정보를 통해서, 목장정보 가져오기
        ResLogin resLogin = UserStorage.getInstance().getResLogin();
        if (resLogin != null && resLogin.success == 1){
            for(Map<String, String> mapFarmModel : resLogin.mConvertedFarmList){
                String farm = ""+mapFarmModel.get("farm");
                String code = ""+String.valueOf(mapFarmModel.get("code"));
                String name = ""+mapFarmModel.get("name");
                items.add(new FarmModel(farm, code, name));
            }
        }
        else{
            items.add(new FarmModel("샘플 목장1", "26","김ㅇㅇ"));
            items.add(new FarmModel("샘플 목장2", "219","정ㅇㅇ"));
        }

        return items;
    }

    private void goNextIntent(String farmCode){
        CowTagsFragment cowTagsFragment = new CowTagsFragment();
        cowTagsFragment.setSelectFarmCode(farmCode);
        ((CowChronicleActivity)mActivity).replaceFragment(cowTagsFragment, true);
    }
}
