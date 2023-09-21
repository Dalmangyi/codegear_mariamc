package com.codegear.mariamc_rfid.cowchronicle.models;

import androidx.annotation.NonNull;

import com.codegear.mariamc_rfid.cowchronicle.consts.MemoryBankIdEnum;
import com.codegear.mariamc_rfid.cowchronicle.ui.cowtags.CowTagCell;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.MEMORY_BANK;
import com.zebra.rfid.api3.TagData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class CowTagsModel {
    private final List<String> resKeyList = Arrays.asList("COW_ID_NUM", "SNM", "SEX+MONTHS", "PRTY", "PRN_STTS", "TAGNO", "COUNT", "RSSI"); //입력 데이터에서 순서대로 추출할 key
    private final ArrayList<Map<String, String>> mInputResList = new ArrayList<Map<String, String>>(); //입력 데이터
    private final ArrayList<CowTagCell> mCowInfoList = new ArrayList<CowTagCell>(); //출력될 테이블 행
    private final ArrayList<TagData> mTagList = new ArrayList<TagData>(); //태그 데이터


    /*
    inputResList
     */
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
    }

    public CowTagCell getPosition(int pos){
        return this.mCowInfoList.get(pos);
    }

    public int getSize(){
        return this.mCowInfoList.size();
    }





    public void resetTagData(){
        this.mTagList.clear();

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
                ArrayList<TagData> copiedTagList = new ArrayList<>(mTagList);

                //태그 데이터 추출
                Map<String, Integer> mapTagCount = new HashMap<>();
                for(TagData tagData:copiedTagList){

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
                    Integer tagCount = mapTagCount.get(tagId);
                    if (tagCount == null){
                        tagCount = 0;
                    }
                    tagCount++;
                    mapTagCount.put(tagId, tagCount);

                    //태그 데이터 반영 (COUNT)
                    List<CowTagCell> filteredCowTagCells = mCowInfoList.stream()
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
            }
        }
        catch (Exception e){}
    }

    public ArrayList<CowTagCell> getCowTagList(){
        return new ArrayList<>(mCowInfoList);
    }
}
