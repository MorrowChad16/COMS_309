package com.example.pickmeup.Court;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.Home.CourtGames.LocationGamesScreen;
import com.example.pickmeup.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.CourtViewHolder>{

    private Context mCtx;
    private List<Court> courts;
    public boolean isClickable = true;

    public CourtAdapter(Context mCtx, List<Court> courts) {
        this.mCtx = mCtx;
        this.courts = courts;
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.court_layout, null);
        return new CourtViewHolder(view);
    }

    /**
     * @param holder instance of individual CourtViewHolder object to assign values
     * @param position position in CourtViewHolder array to assign values
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Court court = courts.get(position);

        holder.textViewTitle.setText(court.getCourtName());
        holder.textLocationID.setText(Integer.toString(court.getLocationID()));
        holder.textViewMilesAway.setText(Double.toString(court.getMilesAway()));
        //compresses image to ensure high frame rate
        Picasso.with(mCtx)
                .load(court.getImage())
                .fit()
                .centerCrop()
                .into(holder.imageView);
        holder.latitude.setText(Double.toString(court.getLatitude()));
        holder.longitude.setText(Double.toString(court.getLongitude()));
        holder.gameStatus.setText(court.getGameStatus());
    }

    /**
     * @return number of courts generated in the array
     */
    @Override
    public int getItemCount() {
        return courts.size();
    }

    class CourtViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewTitle;
        TextView textViewMilesAway;
        TextView textLocationID;
        TextView latitude;
        TextView longitude;
        TextView gameStatus;

        /**
         * @param itemView grabs instance of activity to assign on screen items
         */
        CourtViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewMilesAway = itemView.findViewById(R.id.textMilesAway);
            textLocationID = itemView.findViewById(R.id.location_id);
            latitude = itemView.findViewById(R.id.latitude_invis);
            longitude = itemView.findViewById(R.id.longitude_invis);
            gameStatus = itemView.findViewById(R.id.games_status);

            itemView.setOnClickListener(view1 -> {
                if(isClickable){
                    Bundle bundle = new Bundle();
                    bundle.putString("id", textLocationID.getText().toString());
                    bundle.putString("courtName", textViewTitle.getText().toString());
                    bundle.putString("currentLat", latitude.getText().toString());
                    bundle.putString("currentLongt", longitude.getText().toString());

                    mCtx.startActivities(new Intent[]{new Intent(mCtx, LocationGamesScreen.class).putExtras(bundle)});
                }
            });
        }
    }
}
