package com.codegear.mariamc_rfid.cowchronicle.tableview;

import androidx.annotation.NonNull;


import com.codegear.mariamc_rfid.cowchronicle.tableview.model.Cell;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.ColumnHeader;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.RowHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TableViewModel {
    private List<String> resKeyList;
    private List<String> columnKeyList;
    private ArrayList<Map<String, String>> mInputResList = new ArrayList<Map<String, String>>();
    private final ArrayList<ColumnHeader> mColumnList = new ArrayList<ColumnHeader>();
    private final ArrayList<List<Cell>> mRowList = new ArrayList<List<Cell>>();

    public TableViewModel(ArrayList<Map<String, String>> inputResList, List resKeyList, List<String> columnKeyList) {
        this.resKeyList = resKeyList;
        this.columnKeyList = columnKeyList;
        this.mInputResList = inputResList;

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
        return mRowList;
    }
}
