package com.codegear.mariamc_rfid.cowchronicle.models;

import androidx.annotation.NonNull;

import com.codegear.mariamc_rfid.cowchronicle.ui.cowtags.CowTagCell;
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
                    String tagId = tagData.getTagID();
                    int tagRssi = tagData.getPeakRSSI();
                    int tagChannel = tagData.getChannelIndex();
                    int tagPhase = tagData.getPhase();

                    String tid = MEMORY_BANK.GetMemoryBankValue("tid").toString();
                    String epc = MEMORY_BANK.GetMemoryBankValue("epc").toString();
                    String memorybank = tagData.getMemoryBankData();

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
