package com.example.pickmeup.Account.NewCourt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.pickmeup.R;
import com.example.pickmeup.httpServices.RequestController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AddNewCourtScreen extends AppCompatActivity {

    private EditText courtName;
    private EditText street;
    private EditText city;
    private EditText state;
    private EditText sport;
    private EditText hidden;

    private RequestController mQueue;
    private double courtLatitude;
    private double courtLongitude;

    @SuppressLint({"CutPasteId", "SetTextI18n", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_court_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        state = findViewById(R.id.state_of_new_court);
        sport = findViewById(R.id.sport_of_new_court);
        hidden = findViewById(R.id.hidden_storage);

        final String[] sports = getResources().getStringArray(R.array.sports_list);
        String[] states = getResources().getStringArray(R.array.states_array);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.sport_of_new_court);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sports);
        autoCompleteTextView.setAdapter(adapter);

        AutoCompleteTextView autoCompleteTextView1 = findViewById(R.id.state_of_new_court);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
        autoCompleteTextView1.setAdapter(adapter1);

        courtName = findViewById(R.id.name_of_new_court);
        street = findViewById(R.id.street_of_new_court);
        city = findViewById(R.id.city_of_new_court);
        Button createCourtButton = findViewById(R.id.submit_new_court);

        /*
        Person clicks the submit court info, so create a json object and send it to the necessary methods
         */
        createCourtButton.setOnClickListener(view -> {
            if(validateCourtInformation()){
                hidden.setText(street.getText().toString() + ", " + city.getText().toString() + ", " + state.getText().toString());
                convertAddressToLatLng(hidden.getText().toString());

                JSONObject newCourt = null;
                try {
                    newCourt = new JSONObject();//must follow table format
                    newCourt.put("sport", sport.getText().toString().trim());
                    newCourt.put("name", courtName.getText().toString().trim());
                    newCourt.put("lat", courtLatitude);
                    newCourt.put("longt", courtLongitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                createCourt(newCourt.toString());
            }
        });

        updateNotificationBar();

        /*
        Person clicks outside the current text box, so reduce the keyboard
         */
        RelativeLayout touchInterceptor = findViewById(R.id.add_new_court_screen);
        touchInterceptor.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect outRect = new Rect();
                if (courtName.isFocused()) {
                    courtName.getGlobalVisibleRect(outRect);
                } else if (street.isFocused()) {
                    street.getGlobalVisibleRect(outRect);
                } else if (city.isFocused()) {
                    city.getGlobalVisibleRect(outRect);
                } else if (state.isFocused()) {
                    state.getGlobalVisibleRect(outRect);
                } else if (sport.isFocused()) {
                    sport.getGlobalVisibleRect(outRect);
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
    private boolean validateCourtInformation() {
        if(courtName.getText().length() == 0){
            courtName.setError("Please enter the courts name");
            return false;
        }
        if(street.getText().length() == 0){
            street.setError("Please enter a street");
            return false;
        }
        if(city.getText().length() == 0){
            city.setError("Please enter a city");
            return false;
        }
        if(state.getText().length() == 0){
            state.setError("Please enter a state");
            return false;
        }
        if(sport.getText().length() == 0){
            sport.setError("Please enter a sport");
            return false;
        }
        return true;
    }

    /**
     * converts the address the user inputs into Lat and Longt
     * @param address string format of the address the user input
     */
    private void convertAddressToLatLng(String address){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList;

        try{
            addressList = geocoder.getFromLocationName(address, 5);
            if(addressList != null){
                Address location = addressList.get(0);
                courtLatitude = location.getLatitude();
                courtLongitude = location.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    Changes the notification bar to white
    */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateNotificationBar(){
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
    }

    /**
     * pushes the new court information to the backend database
     * @param jsonObject = json object that contains the info we want to add the SQL sheet
     */
    private void createCourt(final String jsonObject) {
        try {
            if(mQueue == null){
                mQueue = RequestController.getInstance(this.getApplicationContext());
            }

            String url = getString(R.string.add_court_info) ;

            // end onResponse
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {

                int intResponse = Integer.parseInt(response);

                Toast errorToast;
                switch(intResponse){
                    case 0:
                        errorToast = Toast.makeText(AddNewCourtScreen.this, "Court already exists", Toast.LENGTH_SHORT);
                        errorToast.show();
                        break;
                    case 1:
                        errorToast = Toast.makeText(AddNewCourtScreen.this, "Suggested new court!", Toast.LENGTH_SHORT);
                        errorToast.show();
                        onBackPressed();
                        break;
                    default:
                        errorToast = Toast.makeText(AddNewCourtScreen.this, "An error occurred", Toast.LENGTH_SHORT);
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
    }//end createCourt
}
