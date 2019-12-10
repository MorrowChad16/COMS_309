package com.example.pickmeup.Account.Address;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.example.pickmeup.httpServices.VolleyCallback;

import java.util.List;

import static com.example.pickmeup.httpServices.CustomVolley.customStringRequest;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context mCtx;
    private List<Address> addresses;

    private HomeViewModel homeViewModel;
    private LoggedInUser loggedInUser;

    AddressAdapter(Context mCtx, List<Address> addresses) {
        this.mCtx = mCtx;
        this.addresses = addresses;
        homeViewModel = ViewModelProviders.of((FragmentActivity) mCtx, new HomeViewModelFactory()).get(HomeViewModel.class);
        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login
    }

    /**
     * @param parent assigns layout to the recycler view item
     * @param viewType ??
     * @return Address object
     */
    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.address_layout, null);
        return new AddressViewHolder(view);
    }

    /**
     * @param holder is the instance of AddressViewHolder to assign values to
     * @param position in the AddressViewHolder array to assign the values to
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addresses.get(position);

        holder.addressName.setText(address.getAddressName());
        holder.addressLocation.setText(address.getAddressLocation());
        holder.deleteAddress.setOnClickListener(view -> {
            removeAddress(loggedInUser.getUserLocations().get(position).getUserLocationId());
            addresses.remove(position);
            loggedInUser.removeUserLocation(position);
            homeViewModel.updateUser(mCtx, loggedInUser);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, addresses.size());
        });
    }

    /**
     * @return number of addresses saved under the logged in user
     */
    @Override
    public int getItemCount() {
        return addresses.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView addressName;
        TextView addressLocation;
        ImageView deleteAddress;

        AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            addressName = itemView.findViewById(R.id.address_name);
            addressLocation = itemView.findViewById(R.id.address_location);
            deleteAddress = itemView.findViewById(R.id.delete_address);
        }
    }


    /**
     * @param jsonObject object to remove from the users profile
     * pushes the new court information to the backend database
     */
    private void removeAddress(final Integer jsonObject) {
        try {
            String url = mCtx.getString(R.string.remove_address);

            VolleyCallback callback = new VolleyCallback(){ //implement volleyCallback interface

                @Override
                public void onSuccessResponse(int intResponse, String jsonString) {
                Toast errorToast;
                switch(intResponse){
                    case 0:
                        errorToast = Toast.makeText(mCtx, "", Toast.LENGTH_SHORT);
                        errorToast.show();
                        break;
                    case 1:
                        errorToast = Toast.makeText(mCtx, "Removed", Toast.LENGTH_SHORT);
                        errorToast.show();
                        break;
                    default:
                        errorToast = Toast.makeText(mCtx, "An error occurred", Toast.LENGTH_SHORT);
                        errorToast.show();
                }
                }

                @Override
                public void onErrorResponse(VolleyError error, String message) {
                   
                }
            };

            customStringRequest(mCtx, Request.Method.POST, url, jsonObject + "", callback); //string Request
        } catch(Exception a){
            Log.d("json", "Error" + a.toString());
        }
    }//end createCourt
}
