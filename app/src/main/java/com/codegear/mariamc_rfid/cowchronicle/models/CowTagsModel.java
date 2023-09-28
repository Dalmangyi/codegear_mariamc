package com.codegear.mariamc_rfid.cowchronicle.models;

import androidx.annotation.NonNull;

import com.codegear.mariamc_rfid.cowchronicle.consts.CowFilterKeyEnum;
import com.codegear.mariamc_rfid.cowchronicle.consts.MemoryBankIdEnum;
import com.codegear.mariamc_rfid.cowchronicle.ui.cowtags.CowTagCell;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.TagData;

import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CowTagsModel {
    private final List<String> resKeyList = Arrays.asList("COW_ID_NUM", "SNM", "SEX+MONTHS", "PRTY", "PRN_STTS", "TAGNO", "COUNT", "RSSI"); //입력 데이터에서 순서대로 추출할 key
    private final ArrayList<Map<String, String>> mInputResList = new ArrayList<Map<String, String>>(); //입력 데이터
    private final ArrayList<CowTagCell> mCowInfoList = new ArrayList<CowTagCell>(); //출력될 테이블 행
    private final ArrayList<TagData> mTagList = new ArrayList<TagData>(); //태그 데이터
    private Map<String, Integer> mTagCountMap = new HashMap<>();

    //Filter
    private Map<CowFilterKeyEnum, Object> mTagFilterMap = new HashMap<>();
    private ArrayList<CowTagCell> mFilteredCowInfoList = new ArrayList<>();





    public CowTagsModel() {
    }

    //테이블 행 생성하기
    public void makeRowList(ArrayList<Map<String, String>> inputResList){

        //입력 데이터 초기화
        this.mInputResList.clear();
        this.mInputResList.addAll(inputResList);

        //테이블 행 초기화 및 생성
        mCowInfoList.clear();
        for (int i = 0; i < mInputResList.size(); i++) {
            Map<String, String> inputResItem = mInputResList.get(i);

            CowTagCell cell = new CowTagCell();
            cell.rowPosition = i;
            for (String resKey : resKeyList) {

                //key에 따른 데이터 만들기.
                String strValue = "";
                if (resKey.contains("+")){
                    String[] subKeys = resKey.split("\\+");
                    int subKeyIdx = 0;
                    for (String subKey:subKeys){

                        String tempValue = inputResItem.get(subKey);
                        if(tempValue == null){
                            tempValue = "";
                        }

                        if(subKeyIdx == 0){
                            strValue += tempValue;
                        }
                        else{
                            strValue += "("+tempValue+")";
                        }

                        subKeyIdx++;
                    }
                }
                else{
                    String tempValue = inputResItem.get(resKey);
                    if(tempValue == null){
                        tempValue = "";
                    }
                    strValue = tempValue;
                }

                //key에 해당하는 값 저장하기.
                cell.setValue(resKey, strValue);
            }

            mCowInfoList.add(cell);
        }

        //태그 데이터 초기화
        applyTagList(this.mTagList.toArray(new TagData[mTagList.size()]));
    }




    //데이터 초기화
    public void resetTagData(){
        this.mTagList.clear();
        this.mTagCountMap.clear();

        for(CowTagCell cell : mCowInfoList){
            cell.COUNT = 0;
            cell.RSSI = 0;
        }

    }

    @NonNull
    public void appendTagData(TagData[] tagList) {

        try{
            //태그 데이터 반영
            if(tagList.length > 0){

                //태그 정보 복사
                this.mTagList.addAll(Arrays.asList(tagList));

                //태그 데이터 적용
                applyTagList(tagList);
            }
        }
        catch (Exception e){}
    }

    //태그 데이터 적용
    private void applyTagList(TagData[] tagList){

        //태그 데이터 추출
        for(TagData tagData:tagList){

            //기본 데이터 조회
            String tagId = tagData.getTagID();
            int tagRssi = tagData.getPeakRSSI();
            int tagChannel = tagData.getChannelIndex();
            int tagPhase = tagData.getPhase();


            //메모리 뱅크 데이터 조회
            MemoryBankIdEnum memoryBankIdEnum = MemoryBankIdEnum.NONE;
            String memoryBankData = "";
            ACCESS_OPERATION_CODE opCode = tagData.getOpCode();
            ACCESS_OPERATION_STATUS opStatus = tagData.getOpStatus();
            if (opCode != null && opCode.toString().equalsIgnoreCase(ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ.toString())) {
                if (opStatus != null && opStatus.toString().equalsIgnoreCase(ACCESS_OPERATION_STATUS.ACCESS_SUCCESS.toString())) {

                    //저장된 타입 찾기
                    String memoryBankName = tagData.getMemoryBank().toString();
                    if(memoryBankName.contains(MemoryBankIdEnum.RESERVED.toString())){
                        memoryBankIdEnum = MemoryBankIdEnum.RESERVED;
                    }
                    else if(memoryBankName.contains(MemoryBankIdEnum.EPC.toString())){
                        memoryBankIdEnum = MemoryBankIdEnum.EPC;
                    }
                    else if(memoryBankName.contains(MemoryBankIdEnum.TID.toString())){
                        memoryBankIdEnum = MemoryBankIdEnum.TID;
                    }
                    else if(memoryBankName.contains(MemoryBankIdEnum.USER.toString())){
                        memoryBankIdEnum = MemoryBankIdEnum.USER;
                    }

                    //저장된 데이터 가져오기
                    memoryBankData = tagData.getMemoryBankData();
                }
            }


            //태그 개수 세기
            Integer tagCount = mTagCountMap.get(tagId);
            if (tagCount == null){
                tagCount = 0;
            }
            tagCount++;
            mTagCountMap.put(tagId, tagCount);

            //태그 데이터 반영 (COUNT)
            List<CowTagCell> filteredCowTagCells = mCowInfoList.stream()
                    .limit(1) //소 1마리당 유니크한 1개의 전자이표를 가진다는 가정. 로직상 N개 필터링 해도되지만 속도를 위해서 1개로 고정함.
                    .filter(item -> item.TAGNO.equals(tagId))
                    .collect(Collectors.toList());

            for(CowTagCell cell : filteredCowTagCells){
                cell.COUNT = tagCount;
                cell.RSSI = tagRssi;
                cell.PHASE = tagPhase;
                cell.CHANNEL = tagChannel;

                try {
                    if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected()) {
                        cell.READER_SERIAL_NO = RFIDController.mConnectedReader.getHostName();
                    }
                }catch (Exception e){}

                switch(memoryBankIdEnum){
                    case NONE:
                        break;
                    case RESERVED:
                        cell.OTHER_VAL = memoryBankData;
                        break;
                    case TID:
                        cell.TID_VAL = memoryBankData;
                        break;
                    case EPC:
                        cell.EPC_VAL = memoryBankData;
                        cell.EPD_VAL = memoryBankData;
                        break;
                    case USER:
                        cell.OTHER_VAL = memoryBankData;
                        break;
                }
            }
        }

        //필터링 해놓기.
        refreshFilteredCowInfoList();
    }

    public ArrayList<CowTagCell> getCowTagList(){
        if(mTagFilterMap.keySet().size() > 0){
            return new ArrayList<>(mFilteredCowInfoList);
        }

        return new ArrayList<>(mCowInfoList);
    }


    public CowTagCell getPosition(int pos){
        if(mTagFilterMap.keySet().size() > 0){
            return mFilteredCowInfoList.get(pos);
        }

        return this.mCowInfoList.get(pos);
    }

    public int getSize(){
        if(mTagFilterMap.keySet().size() > 0){
            return mFilteredCowInfoList.size();
        }

        return this.mCowInfoList.size();
    }

    //필터 정보 넣기
    public void putFilterInfo(CowFilterKeyEnum key, @NonNull Object obj){
        if(EnumUtils.isValidEnum(CowFilterKeyEnum.class, key.name())){
            mTagFilterMap.put(key, obj);

            refreshFilteredCowInfoList();
        }
    }

    //필터 정보 빼기
    public void delFilterInfo(CowFilterKeyEnum key){
        if(EnumUtils.isValidEnum(CowFilterKeyEnum.class, key.name())){
            mTagFilterMap.remove(key);

            refreshFilteredCowInfoList();
        }
    }

    //필터 배열 갱신하기
    public void refreshFilteredCowInfoList(){
        if(mTagFilterMap.keySet().size() > 0){
            ArrayList<CowTagCell> cowTagCells = new ArrayList<>(this.mCowInfoList);
            Stream<CowTagCell> stream = cowTagCells.stream();

            for(CowFilterKeyEnum filterKey : mTagFilterMap.keySet()){
                Object filterVal = mTagFilterMap.get(filterKey);

                switch(filterKey){
                    case COUNT:
                        stream = stream.filter(item -> item.COUNT > (int)filterVal);
                        break;
                }
            }

            List<CowTagCell> filteredCowTagCells = stream.collect(Collectors.toList());
            this.mFilteredCowInfoList.clear();
            this.mFilteredCowInfoList.addAll(filteredCowTagCells);
        }


    }
}
