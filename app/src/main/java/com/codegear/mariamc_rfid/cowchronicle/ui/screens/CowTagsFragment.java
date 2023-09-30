package com.codegear.mariamc_rfid.cowchronicle.ui.screens;

import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_ACCESS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.Constants.INTENT_NEXT_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.Constants.INTENT_START_TAB;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.cowchronicle.consts.MemoryBankIdEnum;
import com.codegear.mariamc_rfid.cowchronicle.services.ReqInsertTagData;
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
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.xw.repo.BubbleSeekBar;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ir.mirrajabi.searchdialog.core.BaseFilter;
import retrofit2.Call;

public class CowTagsFragment extends Fragment {


    private final String TAG = "CowTagsFragment";


    //UI
    private AppCompatActivity mActivity;
    private View mMainView;
    private RecyclerView rvCowTags;
    private CowTagRowAdapter adapterCowTagRow;
    private Button btnCurrentFarm, btnAntennaPowerApply, btnClear, btnScan;
    private BubbleSeekBar bsbDistancePower;
    private PowerSpinnerView spMemoryBankIds;
    private CheckBox cbUseFilterCount;

    private Handler mAdapterHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Log.d(TAG,"CowTagsFragment handleMessage.");

            adapterCowTagRow.notifyDataSetChanged();
            mAdapterHandler.removeMessages(0);

        }
    };



    //Data
    private String mSelectedFarmCode;
    private ArrayList<FarmModel> mFarmList;
    private ArrayList<Map<String, String>> mCowList = new ArrayList<>();
    private int[] antennaPowerLevels = null;
    private DeviceTaskSettings.SaveAntennaConfigurationTask antennaTask = null;
    private CowTagsModel cowTagsModel = new CowTagsModel();
    private boolean mScanRunning = false;
    private RFIDSingleton rfidSingleton = RFIDSingleton.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        btnAntennaPowerApply = mMainView.findViewById(R.id.btnAntennaPowerApply);
        btnAntennaPowerApply.setOnClickListener(v -> {

            if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                CustomDialog.showSimple(mActivity, "장치 연결이 끊겨있습니다.\n장치설정 화면으로 이동해서 연결후 다시 시도해 주세요.");
                return;
            }

            if(btnScan.isSelected()){
                CustomDialog.showSimple(mActivity, "스캔을 정지 후, 다시 시도해 주세요.");
                return;
            }

            if(antennaPowerLevels != null){
                int powerIndex = antennaPowerLevels[bsbDistancePower.getProgress()];

                antennaTask = new DeviceTaskSettings.SaveAntennaConfigurationTask(powerIndex, mActivity);
                antennaTask.execute();
            }
            else{
                CustomDialog.showSimple(mActivity, "안테나 정보가 올바르지 않습니다. 다시 시도해 주세요.");

                if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected() && RFIDController.mConnectedReader.isCapabilitiesReceived()) {
                    try {
                        antennaPowerLevels = RFIDController.mConnectedReader.ReaderCapabilities.getTransmitPowerLevelValues();
                    }
                    catch (Exception e){
                        Log.d(TAG,"eeee:"+e.toString());
                    }
                }
            }
        });
        btnClear = mMainView.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> {
            refreshTagList();
        });
        btnScan = mMainView.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            boolean isSelected = v.isSelected();
            if(!isSelected){
                if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                    CustomDialog.showSimple(mActivity, "장치 연결이 끊겨있습니다.\n장치설정 화면으로 이동해서 연결후 다시 시도해 주세요.");
                    return;
                }
            }
            v.setSelected(!isSelected);
            setScanRunning(!isSelected);
        });

        adapterCowTagRow = new CowTagRowAdapter(cowTagsModel);
        rvCowTags = mMainView.findViewById(R.id.rvCowTags);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, layoutManager.getOrientation());
        rvCowTags.addItemDecoration(dividerItemDecoration);
        rvCowTags.setLayoutManager(layoutManager);
        rvCowTags.setAdapter(adapterCowTagRow);
        adapterCowTagRow.setOnCowItemClickListener(cell -> {

            CustomDialog.showSelectDialog(mActivity,
                "이력제번호 : "+cell.COW_ID_NUM, cell.toDetailString(),
                "상세 정보 보기", (MaterialDialog.SingleButtonCallback) (dialog, which) -> goCowDetail(""+cell.COW_ID_NUM),
                "태그 쓰기", (MaterialDialog.SingleButtonCallback) (dialog, which) -> goTagWrite(""+cell.TAGNO)
            );
        });


        cbUseFilterCount = mMainView.findViewById(R.id.cbUseFilterCount);
        cbUseFilterCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                adapterCowTagRow.setUseRSSIFilter(isChecked);
            }
        });
        adapterCowTagRow.setUseRSSIFilter(cbUseFilterCount.isChecked());


        initMemoryBankLayout();
        initProfiles();
        initRFIDListener();


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

        mAdapterHandler.removeCallbacksAndMessages(0);
        stopScanInventory(true);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tag_write, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_tag_write:
                goTagWrite("");
                break;
        }
        return true;
    }

    private void initRFIDListener(){

        Log.i(TAG,"initRFIDListener");

        rfidSingleton.setIRFIDSingletonTag(tagList -> {
            if(mScanRunning){
                if (tagList != null && tagList.length > 0) {
                    cowTagsModel.appendTagData(tagList); //기존 태그 리스트에 신규 태그 리스트 추가하기.
                    mAdapterHandler.sendEmptyMessage(0);
                }
            }
        });
    }

    //메모리뱅크 레이아웃 초기화
    private void initMemoryBankLayout(){

        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();
        for (MemoryBankIdEnum memoryBankIdEnum:MemoryBankIdEnum.values()){
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
                if (RFIDController.mConnectedReader == null || !RFIDController.mConnectedReader.isConnected()) {
                    CustomDialog.showSimple(mActivity, "장치 연결이 끊겨있습니다.\n장치설정 화면으로 이동해서 연결후 다시 시도해 주세요..");
                    return;
                }

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
        mFarmList = loadFarmModel();
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
                mCowList.clear();
                mCowList.addAll(res.data);

                //리스트 새로고침
                cowTagsModel.makeRowList(mCowList);
                mAdapterHandler.sendEmptyMessage(0);
            }
        });
    }


    //목장 검색 다이얼로그 표시
    public void showFarmSearchDialog(View view) {

        FarmSearchDialogCompat farmSearchDialogCompat = new FarmSearchDialogCompat<>(
                mActivity, "목장 리스트",
                "검색어를 입력해 주세요.",
                null,
                mFarmList,
                (dialog, item, position) -> {
                    dialog.dismiss();
                    setSelectFarmCode(item.getFarmCode());
                    initSelectFarm();
                    loadCowList();
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
                        farmSearchDialogCompat.getFilterResultListener().onFilter(filtered);
                    }
                    doAfterFiltering();
                }
            }
        };

        farmSearchDialogCompat.setFilter(apiFilter).show();
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
            initRFIDListener();
            startScanInventory();
        }else{
            btnScan.setText("스캔");
            stopScanInventory(true);
        }
    }


    //스캔 시작
    private void startScanInventory(){

        Application.useCowChronicleTagging = true;

        RFIDController.clearAllInventoryData();
        RfidListeners rfidListeners = new RfidListeners() {
            @Override
            public void onSuccess(Object object) {
                RFIDController.tagListMatchNotice = false;
                RFIDController.mIsInventoryRunning = true;
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


        int memoryBankIdIdx = spMemoryBankIds.getSelectedIndex();
        MemoryBankIdEnum memoryBankIdEnum = MemoryBankIdEnum.values()[memoryBankIdIdx];
        if(memoryBankIdEnum == MemoryBankIdEnum.NONE){
            RFIDController.getInstance().performInventory(rfidListeners);
        }else{
            RFIDController.getInstance().inventoryWithMemoryBank(memoryBankIdEnum.toString(), rfidListeners);
        }
    }

    //스캔 중지
    private void stopScanInventory(boolean sendData){

        Application.useCowChronicleTagging = false;

        //데이터 전송
        if(sendData) {
            sendReadingData();
        }

        //중지
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
            if (RFIDController.mIsInventoryRunning){
                RFIDController.getInstance().stopInventory(new RfidListeners() {
                    @Override
                    public void onSuccess(Object object) {
                        if (RFIDController.mIsInventoryRunning) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                if (e != null && e.getStackTrace().length > 0) {
                                    Log.e(TAG, e.getStackTrace()[0].toString());
                                }
                            }
                        }
                        Application.bBrandCheckStarted = false;
                        RFIDController.mIsInventoryRunning = false;
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
    }


    private void refreshTagList(){

        cowTagsModel.resetTagData();
        mAdapterHandler.sendEmptyMessage(0);
    }



    //안테나 설정값 불러오기
    private void loadCurrentAntennaConfig(){

        boolean isRunning = RFIDController.mIsInventoryRunning;
        Log.d(TAG, "isRunning:"+isRunning);

        if (RFIDController.mConnectedReader != null
                && RFIDController.mConnectedReader.isConnected()
                && RFIDController.mConnectedReader.isCapabilitiesReceived())
        {
            if (!RFIDController.mIsInventoryRunning) {
                try {
                    RFIDController.antennaRfConfig = RFIDController.mConnectedReader.Config.Antennas.getAntennaRfConfig(1);
                } catch (InvalidUsageException e) {
                    throw new RuntimeException(e);
                } catch (OperationFailureException e) {
                    throw new RuntimeException(e);
                }
            }
            if(RFIDController.antennaRfConfig != null){
                int powerIndex = RFIDController.antennaRfConfig.getTransmitPowerIndex();
                antennaPowerLevels = RFIDController.mConnectedReader.ReaderCapabilities.getTransmitPowerLevelValues();

                bsbDistancePower.getConfigBuilder()
                        .max(antennaPowerLevels[antennaPowerLevels.length - 1])
                        .min(antennaPowerLevels[0])
                        .progress(antennaPowerLevels[powerIndex])
                        .build();
            }


        }
    }

    //RFID 리딩 값 전송.
    private void sendReadingData(){

        ArrayList<CowTagCell> cowTagList = cowTagsModel.getCowTagList();
        if (cowTagList.size() == 0){
            return;
        }

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

    //태그쓰기 화면으로 이동
    private void goTagWrite(String tagNum){
        RFIDController.accessControlTag = tagNum;

        //Activity (Fragment 형태로는 많은 함수가 연결되어 있어서 동작이 안되기 때문에, Activity형태로 열어야 정상적으로 동작함........)
        Intent intent = new Intent(mActivity, ActiveDeviceActivity.class);
        intent.putExtra(INTENT_START_TAB, RFID_TAB);
        intent.putExtra(INTENT_NEXT_TAB, RFID_ACCESS_TAB);
        startActivity(intent);
    }

}
