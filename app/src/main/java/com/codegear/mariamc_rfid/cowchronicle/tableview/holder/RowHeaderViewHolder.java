package com.codegear.mariamc_rfid.cowchronicle.tableview.holder;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.tableview.model.RowHeader;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

public class RowHeaderViewHolder extends AbstractViewHolder {

    @NonNull
    public final LinearLayout row_header_container;

    @NonNull
    public final TextView row_header_textview;

    public RowHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        row_header_container = itemView.findViewById(R.id.row_header_container);
        row_header_textview = itemView.findViewById(R.id.row_header_textview);
    }


    public void setRowHeader(@Nullable RowHeader rowHeader) {

        String strRowHeader = String.valueOf(rowHeader.getData());

        //이력제번호 분리되어 보이기
        SpannableStringBuilder spanRowHeader = new SpannableStringBuilder(strRowHeader);
        if (strRowHeader.length() >= 12){
            String strTemp = strRowHeader.substring(0,3)
                    + " " + strRowHeader.substring(3,7)
                    + " " + strRowHeader.substring(7,11)
                    + " " + strRowHeader.substring(11);
            spanRowHeader = new SpannableStringBuilder(strTemp);
            spanRowHeader.setSpan(
                new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 9, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        row_header_textview.setText(spanRowHeader);
        row_header_textview.requestLayout();
    }


    @Override
    public void setSelected(@NonNull SelectionState selectionState) {
        super.setSelected(selectionState);

        switch (selectionState){
            case SELECTED:
            case SHADOWED:
                row_header_container.setBackgroundColor(ContextCompat.getColor(row_header_container.getContext(), R.color.colorPrimary));
                row_header_textview.setTextColor(Color.WHITE);
                break;
            default:
                row_header_container.setBackgroundColor(Color.WHITE);
                row_header_textview.setTextColor(ContextCompat.getColor(row_header_container.getContext(), R.color.tableview_default_text_color));
                break;
        }
    }
}
