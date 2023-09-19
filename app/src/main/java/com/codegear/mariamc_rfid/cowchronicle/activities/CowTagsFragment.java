package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.activities.farms.FarmSearchDialogCompat;
import com.codegear.mariamc_rfid.cowchronicle.models.FarmModel;
import com.codegear.mariamc_rfid.cowchronicle.services.ResCowList;
import com.codegear.mariamc_rfid.cowchronicle.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.services.RetrofitClient;
import com.codegear.mariamc_rfid.cowchronicle.device.DeviceTaskSettings;
import com.codegear.mariamc_rfid.cowchronicle.device.IRFIDSingletonTag;
import com.codegear.mariamc_rfid.cowchronicle.device.RFIDSingleton;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.tableview.CowTagRowAdapter;
import com.codegear.mariamc_rfid.cowchronicle.models.CowTagsModel;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.SoundSearcher;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.rfid.RfidListeners;
import com.codegear.mariamc_rfid.rfidreader.settings.ProfileContent;
import com.xw.repo.BubbleSeekBar;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.TagData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import ir.mirrajabi.searchdialog.core.BaseFilter;
import retrofit2.Call;

public class CowTagsFragment extends Fragment {


    private final String TAG = "CowTagsFragment";


    //UI
    private AppCompatActivity mActivity;
    private View mMainView;
    private RecyclerView mRecyclerView;
    private CowTagRowAdapter cowTagRowAdapter;
    private Button btnCurrentFarm, btnDistancePowerApply, btnClear, btnScan;
    private BubbleSeekBar bsbDistancePower;




    //Data
    private String mSelectedFarmCode;
    private ArrayList<FarmModel> mFarmList;
    private ArrayList<Map<String, String>> mCowList;
    private int[] antennaPowerLevels = null;
    private DeviceTaskSettings.SaveAntennaConfigurationTask antennaTask = null;
    private CowTagsModel cowTagsModel = new CowTagsModel();
    private ArrayList<TagData> mTagList = new ArrayList<>();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (AppCompatActivity)getActivity();
        mActivity.getSupportActionBar().setTitle("전자이표");


        mMainView = inflater.inflate(R.layout.fragment_cow_tags, null, false);

        btnCurrentFarm = mMainView.findViewById(R.id.btnCurrentFarm);
        btnCurrentFarm.setOnClickListener(v -> {

            if(btnScan.isSelected()){
                CustomDialog.showSimple(mActivity, "스캔을 정지 후, 다시 시도해 주세요.");
                return;
            }

            showFarmSearchDialog(v);
        });

        bsbDistancePower = mMainView.findViewById(R.id.bsbDistancePower);
        btnDistancePowerApply = mMainView.findViewById(R.id.btnDistancePowerApply);
        btnDistancePowerApply.setOnClickListener(v -> {

            if(btnScan.isSelected()){
                CustomDialog.showSimple(mActivity, "스캔을 정지 후, 다시 시도해 주세요.");
                return;
            }

            if(antennaPowerLevels != null){
                int powerIndex = antennaPowerLevels[bsbDistancePower.getProgress()];

                antennaTask = new DeviceTaskSettings.SaveAntennaConfigurationTask(powerIndex, mActivity);
                antennaTask.execute();
            }
        });
        btnClear = mMainView.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> {
            refreshTagList();
        });
        btnScan = mMainView.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            boolean isSelected = v.isSelected();
            v.setSelected(!isSelected);

            setScanRunning(!isSelected);
        });

        cowTagRowAdapter = new CowTagRowAdapter(cowTagsModel);
        mRecyclerView = mMainView.findViewById(R.id.tbCowTags);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(cowTagRowAdapter);
        cowTagRowAdapter.setOnItemClickListener(pos -> {
//            pos;
            goCowDetail("1212");
        });

        initProfiles();
        mFarmList = loadFarmModel();


        return mMainView;
    }


    @Override
    public void onResume() {
        super.onResume();

        stopScanInventory();
        initSelectFarm();
        loadCowList();
        loadCurrentAntennaConfig();
    }

    @Override
    public void onStop() {
        super.onStop();

        stopScanInventory();
        if(antennaTask != null && antennaTask.getStatus() == AsyncTask.Status.RUNNING){
            antennaTask.cancel(true);
            antennaTask = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopScanInventory();
    }

    //프로필 초기화
    private void initProfiles(){
        //작업 미 수행시, 안테나 파워와 같은 설정값 저장 불가함.
        ProfileContent content = new ProfileContent(mActivity);
        content.LoadDefaultProfiles();
    }

    //목장 선택 설정
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

//        Call<ResCowList> call = RetrofitClient.getApiService().getCowList(mSelectedFarmCode);
        Call<ResCowList> call = RetrofitClient.getApiService().getCowList("219");
        RetrofitClient.commonCall(ResCowList.class, mActivity, call, null, new RetrofitClient.OnStateListener<ResCowList>() {
            @Override
            public void OnSuccess(ResCowList res) {
                mCowList = res.data;
                refreshRecyclerView();
            }
        });
    }

    //테이블뷰 새로고침
    private void refreshRecyclerView(){
        cowTagsModel.makeRowList(mCowList);


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                CowTagRowAdapter adapter = (CowTagRowAdapter)mRecyclerView.getAdapter();
                adapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(adapter);
            }
        });
    }

    //목장 검색 다이얼로그 표시
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
            mActivity, "목장 리스트","검색어를 입력해주세요.",null, mFarmList,
            (dialog, item, position) -> {
                dialog.dismiss();

                setSelectFarmCode(item.getFarmCode());
                initSelectFarm();
                loadCowList();
            }
        ).setFilter(apiFilter).show();
    }


    //목장 정보 가져오기
    private ArrayList<FarmModel> loadFarmModel() {

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


    //스캔 버튼 상태 갱신
    private void setScanRunning(boolean running){
        btnScan.setSelected(running);

        if(running){
            btnScan.setText("정지");
            startScanInventory();
        }else{
            btnScan.setText("스캔");
            stopScanInventory();
        }
    }

    //태그 이벤트 리스너
    private void initTagEventListener(){
        EventHandler eventHandler = new EventHandler();

        try {
            if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.Events != null) {
                RFIDController.mConnectedReader.Events.removeEventsListener(eventHandler);
                RFIDController.mConnectedReader.Events.addEventsListener(eventHandler);
                RFIDController.mConnectedReader.Events.setHandheldEvent(true);
                RFIDController.mConnectedReader.reinitTransport();
            }
        } catch (InvalidUsageException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        } catch (OperationFailureException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        } catch (ClassCastException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        }
    }

    //스캔 시작
    private void startScanInventory(){
        RFIDController.clearAllInventoryData();
//        initTagEventListener();

        RFIDSingleton.getInstance().setIrfidSingleton(new IRFIDSingletonTag() {
            @Override
            public void tags(TagData[] tagList) {
                if (tagList != null) {
                    mTagList.addAll(Arrays.asList(tagList)); 

                    //case1
//                    tableViewAdapter.setCellItems(cowTagsModel.getCellList());
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        public void run() {
//                            cowTagRowAdapter.notifyDataSetChanged();
//                        }
//                    });
 

                }
            }
        });
        RFIDController.getInstance().performInventory(new RfidListeners() {
            @Override
            public void onSuccess(Object object) {
            }

            @Override
            public void onFailure(Exception exception) {
                setScanRunning(false);
                CustomDialog.showSimpleError(mActivity, "스캔 시작 에러 : "+exception.getMessage());
            }

            @Override
            public void onFailure(String message) {
                setScanRunning(false);
                CustomDialog.showSimpleError(mActivity, "스캔 시작 에러 : "+message);
            }
        });
    }

    //스캔 중지
    private void stopScanInventory(){

        if (RFIDController.mIsInventoryRunning){
            RFIDController.getInstance().stopInventory(new RfidListeners() {
                @Override
                public void onSuccess(Object object) {
                }

                @Override
                public void onFailure(Exception exception) {
                    CustomDialog.showSimpleError(mActivity, "스캔 정지 에러 : "+exception.getMessage());
                }

                @Override
                public void onFailure(String message) {
                    CustomDialog.showSimpleError(mActivity, "스캔 정지 에러 : "+message);
                }
            });
        }
    }


    private void refreshTagList(){
        mTagList.clear();
//        tableViewAdapter.setCellItems(cowTagsModel.getCellList());
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            public void run() {
//                cowTagRowAdapter.notifyDataSetChanged();
//            }
//        });
    }

    private void testTagList(){
        TagData tagData = new TagData();
    }

    public class EventHandler implements RfidEventsListener {

        public EventHandler( ) {
        }

        @Override
        public void eventReadNotify(RfidReadEvents e) {

            final int READ_COUNT = 100;
            TagData[] myTags = null;
            if (RFIDController.mConnectedReader != null) {
                if (!RFIDController.mConnectedReader.Actions.MultiTagLocate.isMultiTagLocatePerforming()) {
                    myTags = RFIDController.mConnectedReader.Actions.getReadTags(READ_COUNT);
                } else {
                    myTags = RFIDController.mConnectedReader.Actions.getMultiTagLocateTagInfo(READ_COUNT);
                }
            }

            if (myTags != null) {
                mTagList.addAll(Arrays.asList(myTags));

                //case1
//                tableViewAdapter.setCellItems(cowTagsModel.getCellList());
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    public void run() {
//                        cowTagRowAdapter.notifyDataSetChanged();
//                    }
//                });



                //case2
//                List<List<Cell>> newCellList = cowTagsModel.getCellList();
//
//                //태그 반영 index 찾기
//                int tagNoIndex = TABLEVIEW_KEYS.indexOf("TAGNO");
//                int countIndex = TABLEVIEW_KEYS.indexOf("COUNT");
//                int rssiIndex = TABLEVIEW_KEYS.indexOf("RSSI");
//
//                //태그 데이터 반영
//                for (int i = 0; i < newCellList.size(); i++) {
//
//                    List<Cell> subCellList = newCellList.get(i);
//
//                    Cell countCell = subCellList.get(countIndex);
//                    Cell newCountCell = new Cell(""+i, countCell.getData());
//                    tableViewAdapter.changeCellItem(countIndex, i, newCountCell);
//
//                    Cell rssiCell = subCellList.get(rssiIndex);
//                    Cell newRssiCell = new Cell(""+i, rssiCell.getData());
//                    tableViewAdapter.changeCellItem(rssiIndex, i, newRssiCell);
//                }
//
//                tableViewAdapter.setAllItems(
//                        cowTagsModel.getColumnHeaderList(),
//                        cowTagsModel.getRowHeaderList(),
//                        cowTagsModel.getCellList()
//                );
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    public void run() {
//                        tableViewAdapter.notifyDataSetChanged();
//                    }
//                });

            }
        }

        @Override
        public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
            Log.d(TAG, "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
        }
    }

    //안테나 설정값 불러오기
    private void loadCurrentAntennaConfig(){

        Antennas.AntennaRfConfig antennaRfConfig;
        try {
            antennaRfConfig = RFIDController.mConnectedReader.Config.Antennas.getAntennaRfConfig(1);

            antennaPowerLevels = RFIDController.mConnectedReader.ReaderCapabilities.getTransmitPowerLevelValues();
            int powerIndex = antennaRfConfig.getTransmitPowerIndex();

            bsbDistancePower.getConfigBuilder()
                    .max(antennaPowerLevels[antennaPowerLevels.length-1])
                    .min(antennaPowerLevels[0])
                    .progress(antennaPowerLevels[powerIndex])
                    .build();

        } catch (InvalidUsageException e) {
            throw new RuntimeException(e);
        } catch (OperationFailureException e) {
            throw new RuntimeException(e);
        }
    }

    //RFID 리딩 값 전송.
    private void callInsertData(){


    }




    private void goCowDetail(String cowNumber){
        WebviewCowDetailFragment cowDetailFragment = new WebviewCowDetailFragment();
        cowDetailFragment.cowNumber = cowNumber;
        ((CowChronicleActivity)mActivity).replaceFragment(cowDetailFragment, true);
    }

}
