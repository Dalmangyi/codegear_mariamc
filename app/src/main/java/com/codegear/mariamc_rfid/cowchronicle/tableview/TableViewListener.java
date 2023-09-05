package com.codegear.mariamc_rfid.cowchronicle.tableview;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codegear.mariamc_rfid.cowchronicle.tableview.holder.ColumnHeaderViewHolder;
import com.codegear.mariamc_rfid.cowchronicle.tableview.popup.ColumnHeaderLongPressPopup;
import com.codegear.mariamc_rfid.cowchronicle.tableview.popup.RowHeaderLongPressPopup;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.listener.ITableViewListener;

public class TableViewListener implements ITableViewListener {
    @NonNull
    private final Context mContext;
    @NonNull
    private final TableView mTableView;

    public TableViewListener(@NonNull TableView tableView) {
        this.mContext = tableView.getContext();
        this.mTableView = tableView;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        // Do what you want.
//        showToast("Cell " + column + " " + row + " has been clicked.");
    }

    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        // Do what you want.
//        showToast("Cell " + column + " " + row + " has been double clicked.");
    }

    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, final int column, int row) {
        // Do What you want
//        showToast("Cell " + column + " " + row + " has been long pressed.");
    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
        // Do what you want.
//        showToast("Column header  " + column + " has been clicked.");
    }

    @Override
    public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
        // Do what you want.
//        showToast("Column header  " + column + " has been double clicked.");
    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

//        if (columnHeaderView instanceof ColumnHeaderViewHolder) {
//            // Create Long Press Popup
//            ColumnHeaderLongPressPopup popup = new ColumnHeaderLongPressPopup((ColumnHeaderViewHolder) columnHeaderView, mTableView);
//            // Show
//            popup.show();
//        }
    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
        // Do whatever you want.
//        showToast("Row header " + row + " has been clicked.");
    }

    @Override
    public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
        // Do whatever you want.
//        showToast("Row header " + row + " has been double clicked.");
    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
//
//        // Create Long Press Popup
//        RowHeaderLongPressPopup popup = new RowHeaderLongPressPopup(rowHeaderView, mTableView);
//        // Show
//        popup.show();
    }


    private void showToast(String p_strMessage) {
        Toast.makeText(mContext, p_strMessage, Toast.LENGTH_SHORT).show();
    }
}
