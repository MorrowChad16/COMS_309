package com.example.pickmeup.Messages.UserActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.R;

import java.util.List;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.UserActivityViewHolder> {

    private Context mCtx;
    private List<UserActivity> userActivities;

    public UserActivityAdapter(Context mCtx, List<UserActivity> userActivities) {
        this.mCtx = mCtx;
        this.userActivities = userActivities;
    }

    @NonNull
    @Override
    public UserActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.active_user_icon, null);
        return new UserActivityAdapter.UserActivityViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserActivityAdapter.UserActivityViewHolder holder, int position) {
        UserActivity userActivity = userActivities.get(position);

        holder.name.setText(userActivity.getName());
    }

    @Override
    public int getItemCount() {
        return userActivities.size();
    }

    class UserActivityViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        UserActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.active_user_name);
        }
    }
}
