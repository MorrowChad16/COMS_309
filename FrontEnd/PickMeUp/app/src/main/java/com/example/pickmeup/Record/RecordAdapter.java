package com.example.pickmeup.Record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.R;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder>{

    private Context mCtx;
    private List<Record> records;

    /**
     * @param mCtx context of where record fragment is
     * @param records list of all records tied to the user
     */
    RecordAdapter(Context mCtx, List<Record> records) {
        this.mCtx = mCtx;
        this.records = records;
    }

    /**
     * @param parent ??
     * @param viewType ??
     * @return new RecordViewHolder of the new object
     */
    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.record_layout, null);
        return new RecordViewHolder(view);
    }

    /**
     * @param holder contains the new object
     * @param position of the new object
     */
    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = records.get(position);

        if(record.getCourtName().equals("Upcoming Games") || record.getCourtName().equals("Completed Games") || record.getCourtName().equals("No Games")){
            CardView temp = holder.itemView.findViewById(R.id.record_cardview);
            temp.setCardElevation(0f);
            holder.courtName.setText(record.getCourtName());
            holder.courtName.setTextSize(23f);
            holder.score.setVisibility(View.GONE);
            holder.address.setVisibility(View.GONE);
            holder.date.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.winLoss.setVisibility(View.GONE);
        } else {
            holder.courtName.setText(record.getCourtName());
            holder.score.setText(record.getScore());
            holder.address.setText(record.getAddress());
            holder.date.setText(record.getDate());
            holder.time.setText(record.getTime());
            holder.winLoss.setText(record.getWinLoss());

            switch (record.getWinLoss()) {
                case "W":  //win? turn it green
                    holder.winLoss.setTextColor(mCtx.getResources().getColor(R.color.quantum_googgreen));
                    break;
                case "L":  //loss? turn it red
                    holder.winLoss.setTextColor(mCtx.getResources().getColor(R.color.quantum_googred));
                    break;
                case "D":  //no decision? turn it grey
                    holder.winLoss.setTextColor(mCtx.getResources().getColor(R.color.quantum_grey700));
                    break;
            }
        }
    }

    /**
     * @return number of records
     */
    @Override
    public int getItemCount() {
        return records.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {

        TextView courtName;
        TextView score;
        TextView time;
        TextView winLoss;
        TextView address;
        TextView date;

        /**
         * @param itemView allows the adapter to access the on-screen info
         */
        RecordViewHolder(@NonNull View itemView) {
            super(itemView);

            courtName = itemView.findViewById(R.id.court_name_record);
            score = itemView.findViewById(R.id.score);
            time = itemView.findViewById(R.id.time_record_tab);
            winLoss = itemView.findViewById(R.id.win_loss_status);
            address = itemView.findViewById(R.id.address_record_tab);
            date = itemView.findViewById(R.id.date_record_tab);
        }
    }
}
