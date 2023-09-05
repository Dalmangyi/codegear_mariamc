package com.codegear.mariamc_rfid.cowchronicle.tableview.holder;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.Cell;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;


public class CellViewHolder extends AbstractViewHolder {
    @NonNull
    private final TextView cell_textview;
    @NonNull
    private final LinearLayout cell_container;

    public CellViewHolder(@NonNull View itemView) {
        super(itemView);
        cell_textview = itemView.findViewById(R.id.cell_data);
        cell_container = itemView.findViewById(R.id.cell_container);
    }

    public void setCell(@Nullable Cell cell) {
        cell_textview.setText(String.valueOf(cell.getData()));

        cell_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        cell_textview.requestLayout();
    }


    @Override
    public void setSelected(@NonNull SelectionState selectionState) {
        super.setSelected(selectionState);

        switch (selectionState){
            case SELECTED:
            case SHADOWED:
                cell_container.setBackgroundColor(ContextCompat.getColor(cell_container.getContext(), R.color.colorHighlight));
                cell_textview.setTextColor(Color.WHITE);
                break;
            default:
                cell_container.setBackgroundColor(Color.WHITE);
                cell_textview.setTextColor(ContextCompat.getColor(cell_container.getContext(), R.color.tableview_default_text_color));
                break;
        }
    }
}
