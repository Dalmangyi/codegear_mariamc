package com.codegear.mariamc_rfid.cowchronicle.ui.cowtags;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.models.CowTagsModel;

public class CowTagRowAdapter extends RecyclerView.Adapter<CowTagViewHolder> {


    private OnItemClickListener onItemClickListener = null;
    private CowTagsModel mCowTagsModel;
    public CowTagRowAdapter(CowTagsModel cowTagsModel) {
        this.mCowTagsModel = cowTagsModel;
    }



    @NonNull
    @Override
    public CowTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_cowtags_row, parent, false);

        return new CowTagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CowTagViewHolder holder, final int position) {

        CowTagCell cowTagCell = mCowTagsModel.getPosition(position);

        holder.view.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(cowTagCell);
            }
        });

        //이력제 번호 끝 5자리만 BOLD 처리 후, 출력.
        String strCowIdNum = cowTagCell.COW_ID_NUM;
        SpannableStringBuilder spanRowHeader = new SpannableStringBuilder(strCowIdNum);
        if (strCowIdNum.length() >= 12){
            String strTemp = strCowIdNum.substring(0,3)
                    + " " + strCowIdNum.substring(3,7)
                    + " " + strCowIdNum.substring(7,11)
                    + " " + strCowIdNum.substring(11);
            spanRowHeader = new SpannableStringBuilder(strTemp);
            spanRowHeader.setSpan(
                    new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 9, strTemp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        holder.tvCOW_ID_NUM.setText(spanRowHeader);

        //데이터 출력.
        holder.tvSNM.setText(cowTagCell.SNM);
        holder.tvSexAndMonths.setText(cowTagCell.SEX_AND_MONTHS);
        holder.tvPRTY.setText(cowTagCell.PRTY);
        holder.tvPRN_STTS.setText(cowTagCell.PRN_STTS);
        holder.tvTAGNO.setText(cowTagCell.TAGNO);
        holder.tvCOUNT.setText(""+cowTagCell.COUNT);
        holder.tvRSSI.setText(""+cowTagCell.RSSI);
    }

    @Override
    public int getItemCount() {
        return mCowTagsModel.getSize();
    }

    public interface OnItemClickListener {
        void onItemClick(CowTagCell cell);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

}
