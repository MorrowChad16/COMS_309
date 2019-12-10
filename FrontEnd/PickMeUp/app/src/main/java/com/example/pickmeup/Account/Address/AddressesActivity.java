package com.example.pickmeup.Account.Address;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressesActivity extends AppCompatActivity {

    private List<Address> addresses;
    private AddressAdapter adapter;

    private LoggedInUser loggedInUser;

    private Handler handler;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        handler = new Handler();

        //call from any fragment in bottomHomeScreen
        //HomeViewModel instantiated in bottomHomeScreen
        HomeViewModel homeViewModel = ViewModelProviders.of(this, new HomeViewModelFactory())
                .get(HomeViewModel.class);
        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login

        title = findViewById(R.id.text_addresses);

        addresses = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.address_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(this, addresses);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addAddressButton = findViewById(R.id.add_address_plus);
        addAddressButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddAddressActivity.class);
            startActivity(intent);
        });

        grabAddresses();
    }

    /**
     * clears the address list and rechecks grabs them in case we added a new address
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        addresses.clear();
        grabAddresses();
    }

    /**
     * @param name of new address
     * @param latitude of new address
     * @param longitude of new address
     */
    void addAddress(String name, double latitude, double longitude){
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<android.location.Address> addressList = null;

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert addressList != null;
        String[] addressSegments = addressList.get(0).getAddressLine(0).split(", ");

        addresses.add(
                new Address(
                        name,
                        //Street, city, state initials
                        addressSegments[1] + ", " + addressSegments[2] + ", " + addressSegments[3].substring(0, 3)
                ));
        Runnable r = () -> adapter.notifyDataSetChanged();
        handler.post(r);
    }

    /**
     * Grabs all locations linked to the user
     */
    @SuppressLint("SetTextI18n")
    private void grabAddresses() {
        if(loggedInUser.getUserLocations().size() > 0){
            for(int i = 0; i < loggedInUser.getUserLocations().size(); i++) {
                addAddress(loggedInUser.getUserLocations().get(i).getName(), loggedInUser.getUserLocations().get(i).getLat(), loggedInUser.getUserLocations().get(i).getLongt());
            }
        } else {
            title.setText("NO ADDRESSES");
        }
    }
}
