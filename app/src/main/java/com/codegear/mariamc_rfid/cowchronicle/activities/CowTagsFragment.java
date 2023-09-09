package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.farms.FarmSearchDialogCompat;
import com.codegear.mariamc_rfid.cowchronicle.activities.models.FarmModel;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.ResCowList;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.activities.services.RetrofitClient;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewAdapter;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewListener;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewModel;
import com.codegear.mariamc_rfid.cowchronicle.utils.SoundSearcher;
import com.evrencoskun.tableview.TableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ir.mirrajabi.searchdialog.core.BaseFilter;
import retrofit2.Call;

public class CowTagsFragment extends Fragment {

    //UI
    private AppCompatActivity mActivity;
    private View mMainView;

    private TableView mTableView;
    private Button btnCurrentFarm;


    //Data
    private String mSelectedFarmCode;
    private ArrayList<FarmModel> mFarmList;
    private ArrayList<Map<String, String>> mCowList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (AppCompatActivity)getActivity();
        mMainView = inflater.inflate(R.layout.fragment_cow_tags, null, false);

        mActivity.getSupportActionBar().setTitle("전자이표");

        mFarmList = createSampleContacts();

        btnCurrentFarm = mMainView.findViewById(R.id.btnCurrentFarm);
        btnCurrentFarm.setOnClickListener(v -> {
            showFarmSearchDialog(v);
        });


        mTableView = mMainView.findViewById(R.id.tbCowTags);

        return mMainView;
    }


    @Override
    public void onResume() {
        super.onResume();

        initSelectFarm();
        loadCowList();
    }

    public void setSelectFarmCode(String farmCode){
        mSelectedFarmCode = farmCode;
    }


    //목장 정보 초기화
    private void initSelectFarm(){

        //목장 정보 불러오기
        FarmModel selectedFarmModel = null;
        for(FarmModel farmModel:mFarmList){
            if(farmModel.getFarmCode().equals(mSelectedFarmCode)){
                selectedFarmModel = farmModel;
                break;
            }
        }
        if(selectedFarmModel != null){
            btnCurrentFarm.setText(selectedFarmModel.getName()+" - "+selectedFarmModel.getOwnerName());
        }else{
            btnCurrentFarm.setText(" - ");
        }
    }

    //소 리스트 불러오기
    private void loadCowList(){

        Call<ResCowList> call = RetrofitClient.getApiService().getCowList(mSelectedFarmCode);
        RetrofitClient.commonCall(ResCowList.class, mActivity, call, null, new RetrofitClient.OnStateListener<ResCowList>() {
            @Override
            public void OnSuccess(ResCowList res) {
                mCowList = res.data;
                resetTableView();
            }
        });
    }


    private void resetTableView(){

        TableViewModel tableViewModel = new TableViewModel(
                mCowList,
                Arrays.asList(
                        "COW_ID_NUM",
                        "SNM",
                        "SEX+MONTHS",
                        "PRTY",
                        "PRN_STTS",
                        "TAGNO",
                        "COUNT",
                        "RSSI"
                ),
                Arrays.asList(
                        "이력제번호",
                        "목장이표",
                        "성별(월령)",
                        "산차",
                        "번식상태",
                        "전자이표",
                        "Count",
                        "RSSI"
                )
        );

        TableViewAdapter tableViewAdapter = new TableViewAdapter();
        mTableView.setAdapter(tableViewAdapter);
        mTableView.setTableViewListener(new TableViewListener(mTableView));
        tableViewAdapter.setAllItems(
                tableViewModel.getColumnHeaderList(),
                tableViewModel.getRowHeaderList(),
                tableViewModel.getCellList()
        );

    }


    public void showFarmSearchDialog(View view) {

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
                        getFilterResultListener().onFilter(filtered);
                    }
                    doAfterFiltering();
                }
            }
        };


        new FarmSearchDialogCompat<>(
                mActivity,
                "목장 리스트",
                "검색어를 입력해주세요.",
                null,
                mFarmList,
                (dialog, item, position) -> {
                    dialog.dismiss();

                    setSelectFarmCode(item.getFarmCode());
                    initSelectFarm();
                    loadCowList();
                }
        ).setFilter(apiFilter).show();
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
}
