package com.example.pickmeup.Account.Address;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Data.model.UserLocations;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.example.pickmeup.httpServices.RequestController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AddAddressActivity extends AppCompatActivity {

    private LoggedInUser loggedInUser;
    private HomeViewModel homeViewModel;

    private RequestController mQueue;

    EditText addressName;
    EditText addressStreet;
    EditText addressCity;
    EditText addressState;
    EditText storage;
    Button submitAddress;

    private Handler handler;

    private double addressLatitude = 0;
    private double addressLongitude = 0;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        handler = new Handler();

        //call from any fragment in bottomHomeScreen
        //HomeViewModel instantiated in bottomHomeScreen
        homeViewModel = ViewModelProviders.of(this, new HomeViewModelFactory()).get(HomeViewModel.class);
        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login

        addressName = findViewById(R.id.name_of_new_address);
        addressStreet = findViewById(R.id.street_of_new_address);
        addressCity = findViewById(R.id.city_of_new_address);
        addressState = findViewById(R.id.state_of_new_address);
        storage = findViewById(R.id.hidden_storage_address);
        submitAddress = findViewById(R.id.submit_new_address);

        /*
        Person clicks the submit address button, so it creates the json object and sends it to
        the addAddress method
         */
        submitAddress.setOnClickListener(view -> {
            if(validateAddressInfo()){
                storage.setText(addressStreet.getText().toString() + ", " + addressCity.getText().toString() + ", " + addressState.getText().toString());
                convertAddressToLatLng(storage.getText().toString());

                JSONObject newAddress = null;
                try {
                    newAddress = new JSONObject();//must follow table format
                    newAddress.put("name", addressName.getText().toString().trim());
                    newAddress.put("lat", addressLatitude);
                    newAddress.put("longt", addressLongitude);
                    newAddress.put("userId", loggedInUser.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addAddress(newAddress.toString());
            }
        });

        /*
        Person clicks outside the current textbox, so reduce the keyboard
         */
        RelativeLayout touchInterceptor = findViewById(R.id.add_address_screen);
        touchInterceptor.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect outRect = new Rect();
                if (addressName.isFocused()) {
                    addressName.getGlobalVisibleRect(outRect);
                } else if (addressStreet.isFocused()) {
                    addressStreet.getGlobalVisibleRect(outRect);

                } else if (addressCity.isFocused()) {
                    addressCity.getGlobalVisibleRect(outRect);
                } else if (addressState.isFocused()) {
                    addressState.getGlobalVisibleRect(outRect);
                }

                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return false;
        });
    }

    /**
     * Check if the user put any information into the text boxes
     * @return true if information is good, false if empty or invalid
     */
    private boolean validateAddressInfo() {
        if(addressName.getText().length() == 0){
            addressName.setError("Please enter an address name");
            return false;
        }
        if(addressStreet.getText().length() == 0){
            addressStreet.setError("Please enter a street");
            return false;
        }
        if(addressCity.getText().length() == 0){
            addressCity.setError("Please enter a city");
            return false;
        }
        if(addressState.getText().length() == 0){
            addressState.setError("Please enter a state");
            return false;
        }
        return true;
    }

    /**
     * converts the address the user inputs into Lat and Longt
     * @param address string format of the address the user input
     */
    @SuppressLint("SetTextI18n")
    private void convertAddressToLatLng(String address){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList;

        try{
            addressList = geocoder.getFromLocationName(address, 5);
            if(addressList != null){
                android.location.Address location = addressList.get(0);
                addressLatitude = location.getLatitude();
                addressLongitude = location.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the logged in user addresses when they submit a new address
     */
    private void updateLoggedInUserLocations() {
        UserLocations userLocations = new UserLocations();
        userLocations.setLat(addressLatitude);
        userLocations.setLongt(addressLongitude);
        userLocations.setName(addressName.getText().toString());
        userLocations.setUserId(loggedInUser.getId());
        loggedInUser.addUserLocation(userLocations);
        homeViewModel.updateUser(this, loggedInUser);
    }

    /**
     * @param jsonObject contains name, longitude, latitude and userID linked to the saved address
     * Adds a new address linked to the logged in user
     */
    private void addAddress(final String jsonObject) {
        try {
            if(mQueue == null){
                mQueue = RequestController.getInstance(this.getApplicationContext());
            }

            String url = getString(R.string.add_user_address) ;

            // end onResponse
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                int intResponse = Integer.parseInt(response);

                Toast errorToast;

                switch(intResponse){
                    case 0:
                        errorToast = Toast.makeText(AddAddressActivity.this, "Name already exists", Toast.LENGTH_SHORT);
                        errorToast.show();
                        break;
                    case 1:
                        updateLoggedInUserLocations();
                        errorToast = Toast.makeText(AddAddressActivity.this, "Added new address!", Toast.LENGTH_SHORT);
                        errorToast.show();
                        Runnable r = this::onBackPressed;
                        handler.post(r);
                        break;
                    default:
                        errorToast = Toast.makeText(AddAddressActivity.this, "An error occurred", Toast.LENGTH_SHORT);
                        errorToast.show();
                }

            }, error -> Log.d("statuscode", error.toString())) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public byte[] getBody() {
                    return jsonObject.getBytes(StandardCharsets.UTF_8);
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response){
                    String responseString;
                    if(response != null){
                        responseString = String.valueOf(response.statusCode);
                        //can get more details such as response.headers
                        Log.d("statuscode", responseString);
                    }
                    assert response != null;
                    return super.parseNetworkResponse(response);
                }
            };//end custom string request
            mQueue.addToRequestQueue(request);

        } catch(Exception a){
            Log.d("json", "Error" + a.toString());
        }
    }
}
