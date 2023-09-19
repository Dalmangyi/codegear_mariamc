package com.codegear.mariamc_rfid.cowchronicle.ui.cowtags;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codegear.mariamc_rfid.R;

public class CowTagViewHolder extends RecyclerView.ViewHolder {

    LinearLayout view;
    TextView tvCOW_ID_NUM, tvSNM, tvSexAndMonths, tvPRTY, tvPRN_STTS, tvTAGNO, tvCOUNT, tvRSSI;

    public CowTagViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView.findViewById(R.id.llItem);
        tvCOW_ID_NUM = itemView.findViewById(R.id.tvCOW_ID_NUM);
        tvSNM = itemView.findViewById(R.id.tvSNM);
        tvSexAndMonths = itemView.findViewById(R.id.tvSexAndMonths);
        tvPRTY = itemView.findViewById(R.id.tvPRTY);
        tvPRN_STTS = itemView.findViewById(R.id.tvPRN_STTS);
        tvTAGNO = itemView.findViewById(R.id.tvTAGNO);
        tvCOUNT = itemView.findViewById(R.id.tvCOUNT);
        tvRSSI = itemView.findViewById(R.id.tvRSSI);
    }
}
