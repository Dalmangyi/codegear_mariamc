package com.codegear.mariamc_rfid.cowchronicle.tableview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.tableview.holder.CellViewHolder;
import com.codegear.mariamc_rfid.cowchronicle.tableview.holder.ColumnHeaderViewHolder;
import com.codegear.mariamc_rfid.cowchronicle.tableview.holder.RowHeaderViewHolder;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.Cell;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.ColumnHeader;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.RowHeader;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.evrencoskun.tableview.sort.SortState;

public class TableViewAdapter extends AbstractTableAdapter<ColumnHeader, RowHeader, Cell> {


    private static final String LOG_TAG = TableViewAdapter.class.getSimpleName();

    @NonNull
    private final TableViewModel mTableViewModel;

    public TableViewAdapter(@NonNull TableViewModel tableViewModel) {
        super();
        this.mTableViewModel = tableViewModel;
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO check
        Log.e(LOG_TAG, " onCreateCellViewHolder has been called");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout;

        switch (viewType) {
            default:
                // For cells that display a text
                layout = inflater.inflate(R.layout.tableview_cell_layout, parent, false);

                // Create a Cell ViewHolder
                return new CellViewHolder(layout);
        }
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable Cell cellItemModel, int columnPosition, int rowPosition) {

        switch (holder.getItemViewType()) {
            default:
                // Get the holder to update cell item text
                CellViewHolder viewHolder = (CellViewHolder) holder;
                viewHolder.setCell(cellItemModel);
                break;
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: check
        //Log.e(LOG_TAG, " onCreateColumnHeaderViewHolder has been called");
        // Get Column Header xml Layout
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableview_column_header_layout, parent, false);

        // Create a ColumnHeader ViewHolder
        return new ColumnHeaderViewHolder(layout, getTableView());
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable ColumnHeader columnHeaderItemModel, int columnPosition) {

        // Get the holder to update cell item text
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;
        columnHeaderViewHolder.setColumnHeader(columnHeaderItemModel);
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get Row Header xml Layout
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableview_row_header_layout, parent, false);

        // Create a Row Header ViewHolder
        return new RowHeaderViewHolder(layout);
    }


    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable RowHeader rowHeaderItemModel, int rowPosition) {

        // Get the holder to update row header item text
        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        rowHeaderViewHolder.setRowHeader(rowHeaderItemModel);
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        // Get Corner xml layout
        View corner = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableview_corner_layout, null, false);

//        corner.setOnClickListener(view -> {
//            SortState sortState = TableViewAdapter.this.getTableView().getRowHeaderSortingStatus();
//            if (sortState != SortState.ASCENDING) {
//                Log.d("TableViewAdapter", "Order Ascending");
//                TableViewAdapter.this.getTableView().sortRowHeader(SortState.ASCENDING);
//            } else {
//                Log.d("TableViewAdapter", "Order Descending");
//                TableViewAdapter.this.getTableView().sortRowHeader(SortState.DESCENDING);
//            }
//        });
        return corner;
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int column) {
        switch (column) {
            default:
                // Default view type
                return 0;
        }
    }
}
