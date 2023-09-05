package com.codegear.mariamc_rfid.cowchronicle.tableview.holder;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.ColumnHeader;
import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.sort.SortState;


public class ColumnHeaderViewHolder extends AbstractSorterViewHolder {

    private static final String LOG_TAG = ColumnHeaderViewHolder.class.getSimpleName();

    @NonNull
    private final LinearLayout column_header_container;
    @NonNull
    private final TextView column_header_textview;
    @NonNull
    private final ImageButton column_header_sortButton;
    @Nullable
    private final ITableView tableView;

    public ColumnHeaderViewHolder(@NonNull View itemView, @Nullable ITableView tableView) {
        super(itemView);
        this.tableView = tableView;
        column_header_textview = itemView.findViewById(R.id.column_header_textView);
        column_header_container = itemView.findViewById(R.id.column_header_container);
        column_header_sortButton = itemView.findViewById(R.id.column_header_sortButton);

        column_header_sortButton.setOnClickListener(mSortButtonClickListener);
    }

    public void setColumnHeader(@Nullable ColumnHeader columnHeader) {
        column_header_textview.setText(String.valueOf(columnHeader.getData()));

        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        column_header_textview.requestLayout();
    }

    @NonNull
    private final View.OnClickListener mSortButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            if (getSortState() == SortState.ASCENDING) {
//                tableView.sortColumn(getBindingAdapterPosition(), SortState.DESCENDING);
//            } else if (getSortState() == SortState.DESCENDING) {
//                tableView.sortColumn(getBindingAdapterPosition(), SortState.ASCENDING);
//            } else {
//                // Default one
//                tableView.sortColumn(getBindingAdapterPosition(), SortState.DESCENDING);
//            }

        }
    };

    @Override
    public void onSortingStatusChanged(@NonNull SortState sortState) {
        Log.e(LOG_TAG, " + onSortingStatusChanged: x:  " + getBindingAdapterPosition() + ", " + "old state: " + getSortState() + ", current state: " + sortState + ", " + "visibility: " + column_header_sortButton.getVisibility());

        super.onSortingStatusChanged(sortState);

        // It is necessary to remeasure itself.
        column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;

        controlSortState(sortState);

        Log.e(LOG_TAG, " - onSortingStatusChanged: x:  " + getBindingAdapterPosition() + ", " + "old state: " + getSortState() + ", current state: " + sortState + ", " + "visibility: " + column_header_sortButton.getVisibility());

        column_header_textview.requestLayout();
        column_header_sortButton.requestLayout();
        column_header_container.requestLayout();
        itemView.requestLayout();
    }

    private void controlSortState(@NonNull SortState sortState) {
        if (sortState == SortState.ASCENDING) {
            column_header_sortButton.setVisibility(View.VISIBLE);
            column_header_sortButton.setImageResource(R.drawable.ic_down);

        } else if (sortState == SortState.DESCENDING) {
            column_header_sortButton.setVisibility(View.VISIBLE);
            column_header_sortButton.setImageResource(R.drawable.ic_up);
        } else {
            column_header_sortButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setSelected(@NonNull SelectionState selectionState) {
        super.setSelected(selectionState);

        switch (selectionState){
            case SELECTED:
            case SHADOWED:
                column_header_container.setBackgroundColor(ContextCompat.getColor(column_header_container.getContext(), R.color.colorPrimary));
                column_header_textview.setTextColor(Color.WHITE);
                break;
            default:
                column_header_container.setBackgroundColor(Color.parseColor("#EAEAEA"));
                column_header_textview.setTextColor(ContextCompat.getColor(column_header_container.getContext(), R.color.tableview_default_text_color));
                break;
        }
    }
}
