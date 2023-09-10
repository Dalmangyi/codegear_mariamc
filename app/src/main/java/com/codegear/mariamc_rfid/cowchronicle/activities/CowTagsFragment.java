package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.codegear.mariamc_rfid.cowchronicle.device.DeviceTaskSettings;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewAdapter;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewListener;
import com.codegear.mariamc_rfid.cowchronicle.tableview.TableViewModel;
import com.codegear.mariamc_rfid.cowchronicle.utils.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.SoundSearcher;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.rfid.RfidListeners;
import com.codegear.mariamc_rfid.rfidreader.settings.ProfileContent;
import com.evrencoskun.tableview.TableView;
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
import java.util.List;
import java.util.Map;

import ir.mirrajabi.searchdialog.core.BaseFilter;
import retrofit2.Call;

public class CowTagsFragment extends Fragment {


    private final String TAG = "CowTagsFragment";

    private final String[] TABLEVIEW_KEYS = new String[] {"COW_ID_NUM", "SNM", "SEX+MONTHS", "PRTY", "PRN_STTS", "TAGNO", "COUNT", "RSSI"};
    private final String[] TABLEVIEW_NAMES = new String[] {"이력제번호", "목장이표", "성별(월령)", "산차", "번식상태", "전자이표", "Count", "RSSI"};


    //UI
    private AppCompatActivity mActivity;
    private View mMainView;

    private TableView mTableView;
    private Button btnCurrentFarm, btnDistancePowerApply, btnScan;
    private BubbleSeekBar bsbDistancePower;




    //Data
    private String mSelectedFarmCode;
    private ArrayList<FarmModel> mFarmList;
    private ArrayList<Map<String, String>> mCowList;
    private int[] antennaPowerLevels = null;
    private DeviceTaskSettings.SaveAntennaConfigurationTask antennaTask = null;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (AppCompatActivity)getActivity();
        mActivity.getSupportActionBar().setTitle("전자이표");


        mMainView = inflater.inflate(R.layout.fragment_cow_tags, null, false);

        btnCurrentFarm = mMainView.findViewById(R.id.btnCurrentFarm);
        btnCurrentFarm.setOnClickListener(v -> {
            showFarmSearchDialog(v);
        });

        bsbDistancePower = mMainView.findViewById(R.id.bsbDistancePower);
        btnDistancePowerApply = mMainView.findViewById(R.id.btnDistancePowerApply);
        btnDistancePowerApply.setOnClickListener(v -> {

            if(antennaPowerLevels != null){
                int powerIndex = antennaPowerLevels[bsbDistancePower.getProgress()];

                antennaTask = new DeviceTaskSettings.SaveAntennaConfigurationTask(powerIndex, mActivity);
                antennaTask.execute();
            }
        });

        btnScan = mMainView.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            boolean isSelected = v.isSelected();
            v.setSelected(!isSelected);

            setScanRunning(!isSelected);
        });
        mTableView = mMainView.findViewById(R.id.tbCowTags);



        initProfiles();
        mFarmList = loadFarmModel();


        return mMainView;
    }


    @Override
    public void onResume() {
        super.onResume();

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

        Call<ResCowList> call = RetrofitClient.getApiService().getCowList(mSelectedFarmCode);
        RetrofitClient.commonCall(ResCowList.class, mActivity, call, null, new RetrofitClient.OnStateListener<ResCowList>() {
            @Override
            public void OnSuccess(ResCowList res) {
                mCowList = res.data;
                resetTableView();
            }
        });
    }

    //테이블뷰 리셋
    private void resetTableView(){

        TableViewModel tableViewModel = new TableViewModel(
                mCowList,
                Arrays.asList(TABLEVIEW_KEYS),
                Arrays.asList(TABLEVIEW_NAMES)
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
        initTagEventListener();
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
                for (TagData tagData : myTags) {
                    String tagId = tagData.getTagID();
                    short rssi = tagData.getPeakRSSI();

                    Log.d(TAG, "tag("+tagId+"):"+rssi);
                }
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


}
