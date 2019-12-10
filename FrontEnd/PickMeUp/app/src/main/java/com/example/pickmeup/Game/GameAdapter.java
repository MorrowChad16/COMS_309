package com.example.pickmeup.Game;

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

import com.example.pickmeup.Home.IndividualGame.InDepthGameScreen;
import com.example.pickmeup.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private Context mCtx;
    private List<Game> games;
    public boolean isClickable = true;

    /**
     * @param mCtx context of activity
     * @param games is the list of Game items for assignment
     */
    public GameAdapter(Context mCtx, List<Game> games) {
        this.mCtx = mCtx;
        this.games = games;
    }

    /**
     * @param parent ??
     * @param viewType ??
     * @return the new GameViewHolder object
     */
    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.game_layout, null);
        return new GameViewHolder(view);
    }

    /**
     * @param holder is the instance of GameViewHolder to assign values to
     * @param position in the GameViewHolder array to assign the values to
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);

        holder.gameId.setText(Integer.toString(game.getGameId()));
        holder.gameCourtName.setText(game.getCourtName());
        holder.courtLocationId.setText(Integer.toString(game.getLocationID()));
        //compresses image to ensure high frame rate
        Picasso.with(mCtx)
                .load(game.getImage())
                .fit()
                .centerInside()
                .into(holder.gameTypeView);
        holder.latitude_invisible.setText(game.getLatitude());
        holder.longitude_invisible.setText(game.getLongitude());
        holder.gameDateTime.setText(game.getDateTime());
    }

    /**
     * @return number of games at the court
     */
    @Override
    public int getItemCount() {
        return games.size();
    }

    class GameViewHolder extends RecyclerView.ViewHolder {

        ImageView gameTypeView;
        TextView gameId;
        TextView gameCourtName;
        TextView courtLocationId;
        TextView latitude_invisible;
        TextView longitude_invisible;
        TextView gameDateTime;

        /**
         * @param itemView is the view of the object we want to grab on screen values from
         */
        GameViewHolder(@NonNull View itemView) {
            super(itemView);

            gameTypeView = itemView.findViewById(R.id.gameTypeView);
            gameCourtName = itemView.findViewById(R.id.gameCourtName);
            courtLocationId = itemView.findViewById(R.id.courtLocationId);
            latitude_invisible = itemView.findViewById(R.id.latitude_invisible);
            longitude_invisible = itemView.findViewById(R.id.longitude_invisible);
            gameId = itemView.findViewById(R.id.game_id);
            gameDateTime = itemView.findViewById(R.id.date_time_location);

            itemView.setOnClickListener(view1 -> {
                if(isClickable) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", gameId.getText().toString());
                    bundle.putString("courtId", courtLocationId.getText().toString());
                    bundle.putString("courtName", gameCourtName.getText().toString());
                    bundle.putString("currentLat", latitude_invisible.getText().toString());
                    bundle.putString("currentLongt", longitude_invisible.getText().toString());

                    mCtx.startActivities(new Intent[]{new Intent(mCtx, InDepthGameScreen.class).putExtras(bundle)});
                }
            });
        }
    }
}
