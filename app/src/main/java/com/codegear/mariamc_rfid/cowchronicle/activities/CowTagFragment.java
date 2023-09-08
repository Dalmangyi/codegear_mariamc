package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.farms.FarmSearchDialogCompat;
import com.codegear.mariamc_rfid.cowchronicle.activities.models.FarmModel;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewAdapter;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewListener;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewModel;
import com.codegear.mariamc_rfid.cowchronicle.utils.SoundSearcher;
import com.evrencoskun.tableview.TableView;

import java.util.ArrayList;

import ir.mirrajabi.searchdialog.core.BaseFilter;

public class CowTagFragment extends Fragment {

//    private UsersService mUsersService;

    private ArrayList<FarmModel> mFarmList;
    private FarmModel mSelectedFarmModel = null;

    private Activity mActivity;
    private View mMainView;

    private TableView mTableView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mMainView = inflater.inflate(R.layout.fragment_cow_tags, null, false);


        mFarmList = createSampleContacts();
        if(mFarmList.size() > 0){
            mSelectedFarmModel = mFarmList.get(0);
        }

        mTableView = mMainView.findViewById(R.id.tbCowTags);

        initializeTableView();

        return mMainView;
    }


    private void initializeTableView(){

        TableViewModel tableViewModel = new TableViewModel();
        TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);
        mTableView.setAdapter(tableViewAdapter);
        mTableView.setTableViewListener(new TableViewListener(mTableView));
        tableViewAdapter.setAllItems(
                tableViewModel.getColumnHeaderList(),
                tableViewModel.getRowHeaderList(),
                tableViewModel.getCellList()
        );

    }


    public void clickFarmSearch(View view) {
        final FarmSearchDialogCompat<FarmModel> searchDialog = new FarmSearchDialogCompat<>(
            mActivity,
            "목장 리스트",
            "검색어를 입력해주세요.",
            null,
                mFarmList,
            (dialog, item, position) -> {
                Toast.makeText(mActivity, item.getTitle(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();

//                mSelectedFarmModel = item;
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
                        searchDialog.getFilterResultListener().onFilter(filtered);
                    }
                    doAfterFiltering();
                }
            }
        };
        searchDialog.setFilter(apiFilter);
        searchDialog.show();
    }

    private ArrayList<FarmModel> createSampleContacts() {
        ArrayList<FarmModel> items = new ArrayList<>();
        // Thanks to https://randomuser.me for the images
        items.add(new FarmModel("제2청우 농장 - 김성희", "https://randomuser.me/api/portraits/women/93.jpg"));
        items.add(new FarmModel("청우농장 - 안치오", "https://randomuser.me/api/portraits/women/79.jpg"));
        items.add(new FarmModel("365농장 - 김효진", "https://randomuser.me/api/portraits/women/56.jpg"));
        items.add(new FarmModel("가나안목장 - 채석현", "https://randomuser.me/api/portraits/women/44.jpg"));
        items.add(new FarmModel("가람목장 - 김민관", "https://randomuser.me/api/portraits/women/82.jpg"));
        items.add(new FarmModel("가율축산 - 우근제", "https://randomuser.me/api/portraits/lego/3.jpg"));
        items.add(new FarmModel("가천농장 - 김기호", "https://randomuser.me/api/portraits/women/60.jpg"));
        items.add(new FarmModel("가온목장 - 구원모", "https://randomuser.me/api/portraits/women/32.jpg"));
        items.add(new FarmModel("강인목장 - 서경범", "https://randomuser.me/api/portraits/women/67.jpg"));
        items.add(new FarmModel("강인목장1 - 서경범", "https://randomuser.me/api/portraits/women/67.jpg"));
        items.add(new FarmModel("강인목장2 - 서경범", "https://randomuser.me/api/portraits/women/67.jpg"));
        items.add(new FarmModel("강인목장3 - 서경범", "https://randomuser.me/api/portraits/women/67.jpg"));
        items.add(new FarmModel("강인목장4 - 서경범", "https://randomuser.me/api/portraits/women/67.jpg"));
        items.add(new FarmModel("강인목장5 - 서경범", "https://randomuser.me/api/portraits/women/67.jpg"));
        items.add(new FarmModel("강인목장6 - 서경범", "https://randomuser.me/api/portraits/women/67.jpg"));
        return items;
    }
}
