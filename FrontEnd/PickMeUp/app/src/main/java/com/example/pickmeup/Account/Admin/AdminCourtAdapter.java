package com.example.pickmeup.Account.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdminCourtAdapter extends RecyclerView.Adapter<AdminCourtAdapter.AdminCourtViewHolder>{

    private Context mCtx;
    private List<AdminCourt> adminCourts;

    AdminCourtAdapter(Context mCtx, List<AdminCourt> adminCourts) {
        this.mCtx = mCtx;
        this.adminCourts = adminCourts;
    }

    /**
     * @param parent ??
     * @param viewType ??
     * @return new AdminCourt object
     */
    @NonNull
    @Override
    public AdminCourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.admin_court_layout, null);
        return new AdminCourtViewHolder(view);
    }

    /**
     * @param holder instance of AdminCourtViewHolder to assign values to
     * @param position of AdminCourtViewHolder array to assign values to
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdminCourtViewHolder holder, int position) {
        AdminCourt adminCourt = adminCourts.get(position);

        holder.courtName.setText(adminCourt.getCourtName());
        holder.courtAddress.setText(adminCourt.getCourtAddress());
        holder.id = adminCourt.getId();
        holder.info = adminCourt.getInfo();
        holder.longt = adminCourt.getLongt();
        holder.lat = adminCourt.getLat();
    }

    /**
     * @return number of courts for the admin to check
     */
    @Override
    public int getItemCount() {
        return adminCourts.size();
    }

    /**
     * @param position of item we want to delete
     * Deletes the item in the position provided
     */
    public void deleteItem(int position, int direction) {
        AdminCourtDataSource temp = AdminCourtDataSource.getInstance();
        //court is verified, so it will show up on the courts nearby
        JSONObject updatedCourt = null;
        try {
            updatedCourt = new JSONObject();//must follow table format
            updatedCourt.put("id", adminCourts.get(position).getId());
            updatedCourt.put("info", adminCourts.get(position).getInfo());
            updatedCourt.put("verified", true);
            updatedCourt.put("longt", adminCourts.get(position).getLongt());
            updatedCourt.put("lat", adminCourts.get(position).getLat());
            updatedCourt.put("name", adminCourts.get(position).getCourtName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(direction == 8){ //swipe right
            temp.send("updateCourt() " + updatedCourt.toString());
        } else if(direction == 4){ //swipe left
            temp.send("deleteCourt() " + updatedCourt.toString());
        }
//        mRecentlyDeletedItem = adminCourts.get(position);
//        mRecentlyDeletedItemPosition = position;
        adminCourts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, adminCourts.size());
//        showUndoSnackbar();
    }

//    private void showUndoSnackbar() {
//        View view = this.findViewById(R.id.coordinator_layout);
//        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
//                Snackbar.LENGTH_LONG);
//        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
//        snackbar.show();
//    }
//
//    private void undoDelete() {
//        mListItems.add(mRecentlyDeletedItemPosition,
//                mRecentlyDeletedItem);
//        notifyItemInserted(mRecentlyDeletedItemPosition);
//    }

    class AdminCourtViewHolder extends RecyclerView.ViewHolder {

        TextView courtName;
        TextView courtAddress;
        String id;
        String info;
        String longt;
        String lat;

        /**
         * @param itemView assigns values from the object reference
         */
        AdminCourtViewHolder(@NonNull View itemView) {
            super(itemView);

            courtName = itemView.findViewById(R.id.old_court_name);
            courtAddress = itemView.findViewById(R.id.old_court_address);
        }
    }
}
