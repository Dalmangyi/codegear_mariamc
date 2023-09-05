package com.codegear.mariamc_rfid.cowchronicle.tableview;

import android.text.Html;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;


import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.Cell;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.ColumnHeader;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.RowHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TableViewModel {

    private final ArrayList<ColumnHeader> mColumnList = new ArrayList<ColumnHeader>();
    private final ArrayList<String[]> mRowList = new ArrayList<String[]>();

    public TableViewModel() {
        initColumnList();
        initRowList();
    }

    private void initColumnList(){
        int i = 0;
        mColumnList.add(new ColumnHeader(String.valueOf(i++), "목장이표"));
        mColumnList.add(new ColumnHeader(String.valueOf(i++), "성별(월령)"));
        mColumnList.add(new ColumnHeader(String.valueOf(i++), "산차"));
        mColumnList.add(new ColumnHeader(String.valueOf(i++), "번식상태"));
        mColumnList.add(new ColumnHeader(String.valueOf(i++), "전자이표"));
        mColumnList.add(new ColumnHeader(String.valueOf(i++), "Count"));
        mColumnList.add(new ColumnHeader(String.valueOf(i++), "RSSI"));
    }

    private void initRowList(){
        mRowList.add(new String[]{"002123456789", "165", "암(40)", "2","임신","5574651132","15","-50"});
        mRowList.add(new String[]{"002123456788", "160", "암(23)", "5","공태","65456504657","5","-39"});
        mRowList.add(new String[]{"002123456787", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456781", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456782", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456783", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456784", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456785", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456786", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456787", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456788", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456789", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456700", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456701", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456702", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456703", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456704", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456705", "161", "수(12)", "2","임신","5574651132","115","-11"});
        mRowList.add(new String[]{"002123456706", "161", "수(12)", "2","임신","5574651132","115","-11"});
    }


    @NonNull
    private List<RowHeader> getSimpleRowHeaderList() {

        List<RowHeader> list = new ArrayList<>();
        for (int i = 0; i < mRowList.size(); i++) {

            String strRowHeader = "";
            try{
                String temp = mRowList.get(i)[0];

//                strRowHeader = Html.fromHtml(temp);
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
    private List<List<Cell>> getCellListForSortingTest() {
        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < mRowList.size(); i++) {
            String[] rowItem = mRowList.get(i);
            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < mColumnList.size(); j++) {

                String id = j + "-" + i;
                Object text = rowItem[j+1];

                Cell cell = new Cell(id, text);
                cellList.add(cell);
            }
            list.add(cellList);
        }

        return list;
    }


    @NonNull
    public List<List<Cell>> getCellList() {
        return getCellListForSortingTest();
    }

    @NonNull
    public List<RowHeader> getRowHeaderList() {
        return getSimpleRowHeaderList();
    }

    @NonNull
    public List<ColumnHeader> getColumnHeaderList() {
        return mColumnList;
    }
}
