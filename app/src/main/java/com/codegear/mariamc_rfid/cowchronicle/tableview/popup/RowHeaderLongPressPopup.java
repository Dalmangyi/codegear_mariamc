package com.codegear.mariamc_rfid.cowchronicle.tableview.popup;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codegear.mariamc_rfid.R;
import com.evrencoskun.tableview.TableView;


public class RowHeaderLongPressPopup extends PopupMenu implements PopupMenu.OnMenuItemClickListener {


    @NonNull
    private final TableView mTableView;
    private final int mRowPosition;

    public RowHeaderLongPressPopup(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull TableView tableView) {
        super(viewHolder.itemView.getContext(), viewHolder.itemView);

        this.mTableView = tableView;
        this.mRowPosition = viewHolder.getBindingAdapterPosition();

        initialize();
    }

    private void initialize() {
        createMenuItem();

        this.setOnMenuItemClickListener(this);
    }

    private void createMenuItem() {
        Context context = mTableView.getContext();
        //this.getMenu().add(Menu.NONE, REMOVE_ROW, 2, "Remove " + mRowPosition + " position");
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        // Note: item id is index of menu item..

        switch (menuItem.getItemId()) {
//            case SCROLL_COLUMN:
//                mTableView.scrollToColumnPosition(15);
//                break;
//            case SHOWHIDE_COLUMN:
//                int column = 1;
//                if (mTableView.isColumnVisible(column)) {
//                    mTableView.hideColumn(column);
//                } else {
//                    mTableView.showColumn(column);
//                }
//
//                break;
//            case REMOVE_ROW:
//                mTableView.getAdapter().removeRow(mRowPosition);
//                break;
            default:
                break;
        }
        return true;
    }

}
