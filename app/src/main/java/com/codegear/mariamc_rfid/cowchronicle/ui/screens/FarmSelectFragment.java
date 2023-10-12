package com.codegear.mariamc_rfid.cowchronicle.ui.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.consts.BottomNavEnum;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.ui.farms.FarmSearchDialogCompat;
import com.codegear.mariamc_rfid.cowchronicle.models.FarmModel;
import com.codegear.mariamc_rfid.cowchronicle.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.utils.SoundSearcher;

import java.util.ArrayList;
import java.util.Map;

import ir.mirrajabi.searchdialog.core.BaseFilter;


public class FarmSelectFragment extends Fragment {

    private final String TAG = "FarmSelectFragment";

    //Views
    private AppCompatActivity mActivity;
    private View mRootView;
    private TextView tvMemberName;
    private Button btnFarmSelect;


    //Data
    private ArrayList<FarmModel> mFarmList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mActivity = (AppCompatActivity) getActivity();
        mActivity.getSupportActionBar().setTitle("목장 선택");


        mRootView = inflater.inflate(R.layout.fragment_farm_select, null, false);


        tvMemberName = mRootView.findViewById(R.id.tvMemberName);
        btnFarmSelect = mRootView.findViewById(R.id.btnCurrentFarm);
        btnFarmSelect.setOnClickListener(this::showFarmSearchDialog);

        initUserInfo();
        initFarmList();

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        UserStorage.getInstance().setBottomNavItem(BottomNavEnum.BN_COW_TAGS);

        initFarmList();
    }

    private void initFarmList(){
        mFarmList = createSampleContacts();
        if(mFarmList == null || mFarmList.size() == 0){
            btnFarmSelect.setText("농장이 없습니다.");
            btnFarmSelect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        else if(mFarmList.size() == 1){
            FarmModel farmModel = mFarmList.get(0);
            String strBtnTitle = farmModel.getName() + " - " + farmModel.getOwnerName();
            btnFarmSelect.setText(strBtnTitle);
            btnFarmSelect.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }else{
            btnFarmSelect.setText("목장을 선택해 주세요.");
            btnFarmSelect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_arrow_drop_down_24, 0, 0, 0);
        }
    }

    //가입자 정보
    private void initUserInfo(){

        ResLogin resLogin = UserStorage.getInstance().getResLogin();

        if(resLogin!=null){

            String membership = resLogin.membership_nm;
            if(membership == null){
                membership = "";
            }
            String user_name = resLogin.usr_nm;
            if(user_name == null){
                user_name = "";
            }
            String strMemberName = "";
            strMemberName += user_name + "\n";
            strMemberName += "("+membership+" 회원)";

            tvMemberName.setText(strMemberName);
        }
    }


    private void showFarmSearchDialog(View v) {

        if(mFarmList == null || mFarmList.size() == 0){
            CustomDialog.showSimple(mActivity, "선택할 농장이 없습니다.\n관리자에게 문의해주세요.");
            return;
        }
        else if(mFarmList.size() == 1){
            FarmModel farmModel = mFarmList.get(0);
            goNextIntent(farmModel.getFarmCode());
            return;
        }


//        ArrayList<FarmModel> mFarmList = createSampleContacts();

        FarmSearchDialogCompat searchDialogCompat = new FarmSearchDialogCompat<FarmModel>(
            mActivity,
            "목장 리스트", "검색어를 입력해 주세요.",
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
