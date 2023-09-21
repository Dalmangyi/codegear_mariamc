package com.codegear.mariamc_rfid.cowchronicle.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.consts.MemoryBankIdEnum;
import com.codegear.mariamc_rfid.cowchronicle.services.ReqInsertTagData;
import com.codegear.mariamc_rfid.cowchronicle.services.ResCommon;
import com.codegear.mariamc_rfid.cowchronicle.services.ResInsertTagData;
import com.codegear.mariamc_rfid.cowchronicle.ui.cowtags.CowTagCell;
import com.codegear.mariamc_rfid.cowchronicle.ui.farms.FarmSearchDialogCompat;
import com.codegear.mariamc_rfid.cowchronicle.models.FarmModel;
import com.codegear.mariamc_rfid.cowchronicle.services.ResCowList;
import com.codegear.mariamc_rfid.cowchronicle.services.ResLogin;
import com.codegear.mariamc_rfid.cowchronicle.services.RetrofitClient;
import com.codegear.mariamc_rfid.cowchronicle.device.DeviceTaskSettings;
import com.codegear.mariamc_rfid.cowchronicle.device.RFIDSingleton;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.ui.cowtags.CowTagRowAdapter;
import com.codegear.mariamc_rfid.cowchronicle.models.CowTagsModel;
import com.codegear.mariamc_rfid.cowchronicle.ui.dialog.CustomDialog;
import com.codegear.mariamc_rfid.cowchronicle.utils.PixelUtil;
import com.codegear.mariamc_rfid.cowchronicle.utils.SoundSearcher;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.rfid.RfidListeners;
import com.codegear.mariamc_rfid.rfidreader.settings.ProfileContent;
import com.google.gson.Gson;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.xw.repo.BubbleSeekBar;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;

import org.json.JSONArray;
import org.llrp.ltk.generated.parameters.Custom;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ir.mirrajabi.searchdialog.core.BaseFilter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CowTagsFragment extends Fragment {


    private final String TAG = "CowTagsFragment";


    //UI
    private AppCompatActivity mActivity;
    private View mMainView;
    private RecyclerView mRecyclerView;
    private CowTagRowAdapter cowTagRowAdapter;
    private Button btnCurrentFarm, btnDistancePowerApply, btnClear, btnScan;
    private BubbleSeekBar bsbDistancePower;
    private PowerSpinnerView spMemoryBankIds;




    //Data
    private String mSelectedFarmCode;
    private ArrayList<FarmModel> mFarmList;
    private ArrayList<Map<String, String>> mCowList;
    private int[] antennaPowerLevels = null;
    private DeviceTaskSettings.SaveAntennaConfigurationTask antennaTask = null;
    private CowTagsModel cowTagsModel = new CowTagsModel();

    private boolean mScanRunning = false;



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
        cowTagRowAdapter.setOnItemClickListener(cell -> {
            goCowDetail(""+cell.COW_ID_NUM);
        });

        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
        for (Enum memoryBankIdEnum:MemoryBankIdEnum.values()){
            CharSequence charSequence = memoryBankIdEnum.toString();
            iconSpinnerItems.add(new IconSpinnerItem(charSequence, ContextCompat.getDrawable(mActivity, R.drawable.ic_transparent)));
        }


        spMemoryBankIds = mMainView.findViewById(R.id.spMemoryBankIds);
        spMemoryBankIds.setSpinnerAdapter(new IconSpinnerAdapter(spMemoryBankIds));
        spMemoryBankIds.setItems(iconSpinnerItems);
        spMemoryBankIds.setLifecycleOwner(mActivity);

        spMemoryBankIds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mScanRunning){
                    CustomDialog.showSimple(mActivity, "스캔을 정지하고 변경할 수 있습니다.");
                }
                else{
                    spMemoryBankIds.showOrDismiss(0, PixelUtil.ConvertDpToPx(mActivity, 20));
                }

            }
        });
        spMemoryBankIds.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int oldPos, @Nullable Object o, int pos, Object t1) {

                MemoryBankIdEnum memoryBankIdEnum = MemoryBankIdEnum.values()[pos];

                //Title
                spMemoryBankIds.setText("MemoryBank\n"+memoryBankIdEnum.toString());
            }
        });
        spMemoryBankIds.selectItemByIndex(0);


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

                //TODO - TEST
                for(int i=0; i<mCowList.size(); i++){
                    if(i%2==0){
                        Map<String, String> cowItem = mCowList.get(i);
                        cowItem.put("TAGNO","0541100000001666");
                    }
                }

                //리스트 새로고침
                refreshRecyclerView();
            }
        });
    }

    //테이블뷰 새로고침
    private void refreshRecyclerView(){

        cowTagsModel.makeRowList(mCowList);
        cowTagsModel.resetTagData();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                cowTagRowAdapter.notifyDataSetChanged();
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
        mScanRunning = running;
        btnScan.setSelected(running);

        if(running){
            btnScan.setText("정지");
            startScanInventory();
        }else{
            btnScan.setText("스캔");
            stopScanInventory();
        }
    }


    //스캔 시작
    private void startScanInventory(){

        RFIDController.clearAllInventoryData();
        RFIDSingleton.getInstance().setIrfidSingleton(tagList -> {

            if(mScanRunning){

                //신규 태그 정보
                if (tagList != null && tagList.length > 0) {
                    cowTagsModel.appendTagData(tagList); //기존 태그 리스트에 신규 태그 리스트 추가하기.
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            cowTagRowAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

        });

        int memoryBankIdIdx = spMemoryBankIds.getSelectedIndex();
        MemoryBankIdEnum memoryBankIdEnum = MemoryBankIdEnum.values()[memoryBankIdIdx];

        RfidListeners rfidListeners = new RfidListeners() {
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
        };

        if(memoryBankIdEnum == MemoryBankIdEnum.NONE){
            RFIDController.getInstance().performInventory(rfidListeners);
        }else{
            RFIDController.getInstance().inventoryWithMemoryBank(memoryBankIdEnum.toString(), rfidListeners);
        }
    }

    //스캔 중지
    private void stopScanInventory(){

        //데이터 전송
        sendReadingData();

        //중지
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

        cowTagsModel.resetTagData();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                cowTagRowAdapter.notifyDataSetChanged();
            }
        });
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
    private void sendReadingData(){

        ArrayList<CowTagCell> cowTagList = cowTagsModel.getCowTagList();
        if (cowTagList.size() == 0){
            return;
        }

        //데이터 준비 (리더기 정보)
        String strDeviceHostName = RFIDController.mConnectedReader.getHostName();

        //데이터 준비 (태그 데이터)
        ArrayList<String> hasTagIds = new ArrayList<>(); //TAGNO 중복 방지
        ArrayList<ReqInsertTagData> reqInsertTagList = new ArrayList<>();
        for (CowTagCell cell : cowTagList){

            if(cell.COUNT > 0 && !hasTagIds.contains(cell.TAGNO)){
                ReqInsertTagData reqInsertTagData = new ReqInsertTagData(cell);
                reqInsertTagList.add(reqInsertTagData);
                hasTagIds.add(cell.TAGNO);
            }
        }
        if(reqInsertTagList.size() == 0){
            return;
        }


        //데이터 전송
        Call<ResInsertTagData> call = RetrofitClient.getApiService().insertTagData(reqInsertTagList);
        RetrofitClient.commonCall(ResInsertTagData.class, mActivity, call, null, new RetrofitClient.OnStateListener<ResInsertTagData>() {
            @Override
            public void OnSuccess(ResInsertTagData res) {

            }
        });

    }


    //소 상세정보 보기
    private void goCowDetail(String cowIdNum){
        WebviewCowDetailFragment cowDetailFragment = new WebviewCowDetailFragment();
        cowDetailFragment.cowIdNum = cowIdNum;
        ((CowChronicleActivity)mActivity).replaceFragment(cowDetailFragment, true);
    }

}
