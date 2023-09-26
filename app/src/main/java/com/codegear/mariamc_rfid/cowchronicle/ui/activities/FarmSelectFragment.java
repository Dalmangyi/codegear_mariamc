package com.codegear.mariamc_rfid.cowchronicle.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.consts.CowChronicleScreenEnum;
import com.codegear.mariamc_rfid.cowchronicle.ui.farms.FarmSearchDialogCompat;
import com.codegear.mariamc_rfid.cowchronicle.models.FarmModel;
import com.codegear.mariamc_rfid.cowchronicle.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.utils.SoundSearcher;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;

import java.util.ArrayList;
import java.util.Map;

import ir.mirrajabi.searchdialog.core.BaseFilter;


public class FarmSelectFragment extends Fragment {

    private AppCompatActivity mActivity;
    private View mFarmSelectFragmentView;
    private TextView tvMemberName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (AppCompatActivity) getActivity();
        mActivity.getSupportActionBar().setTitle("목장 선택");


        mFarmSelectFragmentView = inflater.inflate(R.layout.fragment_farm_select, null, false);


        tvMemberName = mFarmSelectFragmentView.findViewById(R.id.tvMemberName);

        mFarmSelectFragmentView.findViewById(R.id.btnCurrentFarm).setOnClickListener(this::showFarmSearchDialog);

        initUserInfo();

        return mFarmSelectFragmentView;
    }


    //가입자 정보
    private void initUserInfo(){

        ResLogin resLogin = UserStorage.getInstance().getResLogin();

        if(resLogin!=null){

            String membership = resLogin.membership_nm;
            String company = resLogin.cmpy;
            String user_name = resLogin.usr_nm;
            String strMemberName = "";
            strMemberName += user_name + "\n";
            strMemberName += "("+membership+" 회원)";

            tvMemberName.setText(strMemberName);
        }
    }


    private void showFarmSearchDialog(View v) {

        //기기연결 먼저하기.
        if(RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()){
            Intent intent = new Intent(mActivity, DeviceDiscoverActivity.class);
            intent.putExtra(DeviceDiscoverActivity.ENABLE_AUTO_CONNECT_DEVICE, true); //자동연결 하기.
            intent.putExtra(DeviceDiscoverActivity.DESTINATION_SCREEN_IS_COWCHRONICLE, true); //연결후 카우크로니클로 가게 하기.
            intent.putExtra(CowChronicleActivity.FLAG_FRAGMENT_START_PAGE, CowChronicleScreenEnum.FARM_SELECT.toString());
            mActivity.startActivity(intent);
            return;
        }


        ArrayList<FarmModel> mFarmList = createSampleContacts();


        FarmSearchDialogCompat searchDialogCompat = new FarmSearchDialogCompat<FarmModel>(
            mActivity,
            "목장 리스트", "검색어를 입력해주세요.",
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
                    String listName = farmModel.getName() + " " + farmModel.getOwnerName();
                    boolean isMatch = SoundSearcher.matchString(listName, charSequence.toString());
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
