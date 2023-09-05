package com.codegear.mariamc_rfid.cowchronicle.tableview.popup;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.tableview.holder.ColumnHeaderViewHolder;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.sort.SortState;

public class ColumnHeaderLongPressPopup extends PopupMenu implements PopupMenu.OnMenuItemClickListener {
    // Menu Item constants
    private static final int ASCENDING = 1;
    private static final int DESCENDING = 2;

    @NonNull
    private final TableView mTableView;
    private final int mXPosition;

    public ColumnHeaderLongPressPopup(@NonNull ColumnHeaderViewHolder viewHolder, @NonNull TableView tableView) {
        super(viewHolder.itemView.getContext(), viewHolder.itemView);
        this.mTableView = tableView;
        this.mXPosition = viewHolder.getBindingAdapterPosition();

        initialize();
    }

    private void initialize() {
        createMenuItem();
        changeMenuItemVisibility();

        this.setOnMenuItemClickListener(this);
    }

    private void createMenuItem() {
        Context context = mTableView.getContext();
        this.getMenu().add(Menu.NONE, ASCENDING, 0, context.getString(R.string.tableview_sort_ascending));
        this.getMenu().add(Menu.NONE, DESCENDING, 1, context.getString(R.string.tableview_sort_descending));
        // add new one ...

    }

    private void changeMenuItemVisibility() {
        // Determine which one shouldn't be visible
        SortState sortState = mTableView.getSortingStatus(mXPosition);
        if (sortState == SortState.UNSORTED) {
            // Show others
        } else if (sortState == SortState.DESCENDING) {
            // Hide DESCENDING menu item
            getMenu().getItem(1).setVisible(false);
        } else if (sortState == SortState.ASCENDING) {
            // Hide ASCENDING menu item
            getMenu().getItem(0).setVisible(false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        // Note: item id is index of menu item..

        switch (menuItem.getItemId()) {
            case ASCENDING:
                mTableView.sortColumn(mXPosition, SortState.ASCENDING);
                break;
            case DESCENDING:
                mTableView.sortColumn(mXPosition, SortState.DESCENDING);
                break;
        }
        return true;
    }

}
