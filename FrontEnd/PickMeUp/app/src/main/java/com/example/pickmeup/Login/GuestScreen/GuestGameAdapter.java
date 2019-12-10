package com.example.pickmeup.Login.GuestScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GuestGameAdapter extends RecyclerView.Adapter<GuestGameAdapter.GuestGameViewHolder>{
    private Context mCtx;
    private List<GuestGame> guestGames;

    /**
     * @param mCtx context of the activity
     * @param guestGames list of games displayed
     */
    GuestGameAdapter(Context mCtx, List<GuestGame> guestGames) {
        this.mCtx = mCtx;
        this.guestGames = guestGames;
    }

    /**
     * @param parent ??
     * @param viewType ??
     * @return new Guest Game
     */
    @NonNull
    @Override
    public GuestGameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.guest_game_layout, null);
        return new GuestGameAdapter.GuestGameViewHolder(view);
    }

    /**
     * @param holder of the saved Guest Game
     * @param position of the Guest Game in the array
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GuestGameAdapter.GuestGameViewHolder holder, int position) {
        GuestGame guestGame = guestGames.get(position);

        holder.courtName.setText(guestGame.getCourtName());
        holder.date.setText(guestGame.getDate());
        holder.team1score.setText(guestGame.getTeam1score());
        holder.team2score.setText(guestGame.getTeam2score());
        //compresses image to ensure high frame rate
        Picasso.with(mCtx)
                .load(guestGame.getImage())
                .fit()
                .centerInside()
                .into(holder.imageView);
    }

    /**
     * @return the number of games displayed
     */
    @Override
    public int getItemCount() {
        return guestGames.size();
    }

    class GuestGameViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView courtName;
        TextView date;
        TextView team1score;
        TextView team2score;

        /**
         * @param itemView instance of guest game to assign on screen values
         */
        GuestGameViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.guestGameTypeView);
            courtName = itemView.findViewById(R.id.guest_gameCourtName);
            date = itemView.findViewById(R.id.guest_time_date);
            team1score = itemView.findViewById(R.id.guest_team_1_score);
            team2score = itemView.findViewById(R.id.guest_team_2_score);
        }
    }
}
