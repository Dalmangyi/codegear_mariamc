package com.codegear.mariamc_rfid.cowchronicle.tableview;

import androidx.annotation.NonNull;


import com.codegear.mariamc_rfid.cowchronicle.tableview.model.Cell;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.ColumnHeader;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.RowHeader;
import com.zebra.rfid.api3.TagData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TableViewModel {
    private List<String> resKeyList;
    private List<String> columnKeyList;
    private ArrayList<Map<String, String>> mInputResList = new ArrayList<Map<String, String>>();
    private final ArrayList<ColumnHeader> mColumnList = new ArrayList<ColumnHeader>();
    private final ArrayList<List<Cell>> mRowList = new ArrayList<List<Cell>>();
    private final ArrayList<TagData> mTagList;

    public TableViewModel(ArrayList<Map<String, String>> inputResList, List resKeyList, List<String> columnKeyList, ArrayList<TagData> tagList) {
        this.resKeyList = resKeyList;
        this.columnKeyList = columnKeyList;
        this.mInputResList = inputResList;
        this.mTagList = tagList;

        makeColumnList();
        makeRowList();
    }

    private void makeColumnList(){

        mColumnList.clear();

        int i = 0;
        for (String strColumn:columnKeyList){
            mColumnList.add(new ColumnHeader(String.valueOf(i++), strColumn));
        }
    }

    private void makeRowList(){

        mRowList.clear();


        for (int i = 0; i < mInputResList.size(); i++) {

            Map<String, String> mapInputResItem = mInputResList.get(i);

            List<Cell> cellList = new ArrayList<>();

            int j = 0;
            for (String resKey : resKeyList) {

                final String id = resKey + "-" + (j++);
                String strValue = "";

                if (resKey.contains("+")){
                    String[] subKeys = resKey.split("\\+");
                    int subKeyIdx = 0;
                    for (String subKey:subKeys){

                        String tempValue = mapInputResItem.get(subKey);
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
                    String tempValue = mapInputResItem.get(resKey);
                    if(tempValue == null){
                        tempValue = "";
                    }
                    strValue = tempValue;
                }

                Cell cell = new Cell(id, strValue);
                cellList.add(cell);
            }
            mRowList.add(cellList);
        }
    }




    @NonNull
    public List<ColumnHeader> getColumnHeaderList() {
        return mColumnList;
    }



    @NonNull
    public List<RowHeader> getRowHeaderList() {
        List<RowHeader> list = new ArrayList<>();
        for (int i = 0; i < mRowList.size(); i++) {

            String strRowHeader = "";
            try{
                String temp = (String)mRowList.get(i).get(0).getData();
                strRowHeader = temp;
            }
            catch (Exception e){
                strRowHeader = "e";
            }

            RowHeader header = new RowHeader(String.valueOf(i), strRowHeader);
            list.add(header);
        }

        return list;
    }


    @NonNull
    public List<List<Cell>> getCellList() {

        if(mTagList != null){
            //태그 정보 복사
            ArrayList<TagData> copiedTagList = new ArrayList<>(mTagList);
            mTagList.clear();

            //태그 데이터 추출
            Map<String, Integer> mapTagCount = new HashMap<>();
            Map<String, String> mapTagRSSI = new HashMap<>();
            for(TagData tagData:copiedTagList){
                String tagId = tagData.getTagID();
                int tagRssi = tagData.getPeakRSSI();
                tagData.getChannelIndex();
                tagData.getPhase();

                //태그 개수 세기
                Integer tagCount = mapTagCount.get(tagId);
                if (tagCount == null){
                    tagCount = 0;
                }
                tagCount++;
                mapTagCount.put(tagId, tagCount);

                //태그 최근 RSSI
                mapTagRSSI.put(tagId, ""+tagRssi);
            }

            //태그 반영 index 찾기
            int tagNoIndex = resKeyList.indexOf("TAGNO");
            int countIndex = resKeyList.indexOf("COUNT");
            int rssiIndex = resKeyList.indexOf("RSSI");

            //태그 데이터 반영
            for (int i = 0; i < mRowList.size(); i++) {
                List<Cell> cellList = mRowList.get(i);

                ArrayList<Cell> newCellList = new ArrayList<>();
                for(Cell cell:cellList){
                    Cell newCell = new Cell(cell.getId(), cell.getData());
                    newCellList.add(newCell);
                }

                String tagId = (String) newCellList.get(tagNoIndex).getData();
                if(mapTagCount.containsKey(tagId) && mapTagRSSI.containsKey(tagId)){

                    //count
                    int tagCount = (int)mapTagCount.get(tagId);
                    Cell countCell = newCellList.get(countIndex);
                    if(countCell != null){
                        countCell.setData("" + tagCount);
                    }


                    //rssi
                    String tagRssi = mapTagRSSI.get(tagId);
                    Cell rssiCell = newCellList.get(rssiIndex);
                    if(rssiCell != null){
                        rssiCell.setData("" + tagRssi);
                    }
                }else {
                    newCellList.get(countIndex).setData("");
                    newCellList.get(rssiIndex).setData("");
                }

                mRowList.remove(i);
                mRowList.add(i, newCellList);
            }
        }

        return mRowList;
    }
}
