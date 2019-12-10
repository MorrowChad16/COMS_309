package com.example.pickmeup.Login.GuestScreen;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pickmeup.R;
import com.example.pickmeup.httpServices.CustomVolley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.android.volley.VolleyLog.TAG;

public class GuestScreen extends AppCompatActivity {
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private RequestQueue mQueue;

    //???
    private PlacesClient placesClient;

    //variables used to validate that we can use the users current location
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private LatLng[] mLikelyPlaceLatLngs;

    private double searchingLatitude = 0;
    private double searchingLongitude = 0;
    private int numGamesAtCourt = 0;

    private Button addressButton;
    private TextView numGamesNearby;

    private List<GuestGame> guestGames;
    private GuestGameAdapter adapter;

    Handler hand;

    private int searchRadius = 10;

    /**
     * @param savedInstanceState last saved state of the GuestScreen
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_screen);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        updateNotificationBar();

        hand = new Handler();

        addressButton = findViewById(R.id.address_button_guest);
        numGamesNearby = findViewById(R.id.number_of_games_guest);

        mQueue = Volley.newRequestQueue(this);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        Places.initialize(this.getApplicationContext(), "AIzaSyBmDcA7oP050yM98gpMSPblAPbOsabHBhM");
        placesClient = Places.createClient(this);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        guestGames = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.guest_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new GuestGameAdapter(this, guestGames);
        recyclerView.setAdapter(adapter);
        grabGameInfo();
    }

    /**
     * grabs current location of the guest
     */
    private void getCurrentLocation(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this), ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    ACCESS_FINE_LOCATION)) {
                //Displays alert box with option to cancel or continue to allow location services
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("Location permission is needed to locate nearby players and courts")
                        .setPositiveButton("ok", (dialogInterface, i) -> ActivityCompat.requestPermissions(this,
                                new String[]{ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION))
                        .setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            //Location services already permitted, get the current location and translate it to an address
            mLocationPermissionGranted = true;
            getDeviceLocation();
            showCurrentPlace();
        }

        if(mLocationPermissionGranted){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                    showCurrentPlace();
                    handler.postDelayed(this,300000);
                }
            },300000); //5 minutes
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        if(mLastKnownLocation == null){
                            Log.e("HomeFrag", "mLastKnownLocation null in getDeviceLocation");
                            return;
                        }
                        searchingLatitude = mLastKnownLocation.getLatitude();
                        searchingLongitude = mLastKnownLocation.getLongitude();

                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(searchingLatitude, searchingLongitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        assert addresses != null;
                        addressButton.setText(addresses.get(0).getAddressLine(0));
                    } else {
                        Log.e(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                getDeviceLocation();
                showCurrentPlace();
            }
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mLocationPermissionGranted) {

            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.builder(placeFields).build();
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    // Set the count, handling cases where less than 5 entries are returned.
                    int count;
                    assert response != null;
                    if (response.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                        count = response.getPlaceLikelihoods().size();
                    } else {
                        count = M_MAX_ENTRIES;
                    }

                    int i = 0;
                    mLikelyPlaceLatLngs = new LatLng[count];

                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Place currPlace = placeLikelihood.getPlace();
                        mLikelyPlaceLatLngs[i] = currPlace.getLatLng();
                        assert mLikelyPlaceLatLngs[i] != null;
                        Log.e(TAG, mLikelyPlaceLatLngs[i].latitude + "," + mLikelyPlaceLatLngs[i].longitude);

                        String currLatLng = (mLikelyPlaceLatLngs[i] == null) ?
                                "" : mLikelyPlaceLatLngs[i].toString();

                        Log.i(TAG, "Place " + currPlace.getName()
                                + " has likelihood: " + placeLikelihood.getLikelihood()
                                + " at " + currLatLng);

                        i++;
                        if (i > (count - 1)) {
                            break;
                        }
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        }
    }

    /**
     Changes the notification bar to white
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
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
     * @param sport of the new game
     * @param courtName where the game is
     * @param dateTime date and time of the game
     * @param team1Score of the game
     * @param team2score of the game
     * @param latitude where the game was played
     * @param longitude where the game was played
     */
    public void addGame(String sport, String courtName, String dateTime, String team1Score, String team2score, Double latitude, Double longitude){
        sport = sport.toLowerCase();
        if(searchingLatitude == 0){
            searchingLatitude = 42.020952;
        }
        if(searchingLongitude == 0){
            searchingLongitude = -93.650739;
        }


        double dLat = Math.toRadians(latitude - searchingLatitude);
        double dLon = Math.toRadians(longitude - searchingLongitude);

        //Calculation to grab the distance between current location and
        double a = (Math.pow(Math.sin(dLat/2), 2) + Math.cos(searchingLatitude) * Math.cos(latitude) * (Math.pow(Math.sin(dLon/2), 2)));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = 3963 * c; // Distance in miles
        double topTwoD;
        if(Double.toString(d).length() >= 4){
            topTwoD = Double.parseDouble(Double.toString(d).substring(0, 4));
        }
        else{
            topTwoD = Double.parseDouble(Double.toString(d));
        }
        if(topTwoD <= 10) {
            guestGames.add(
                    new GuestGame(
                            this.getResources().getIdentifier(sport, "drawable", this.getPackageName()),
                            courtName,
                            dateTime,
                            team1Score,
                            team2score
                    ));
            numGamesAtCourt++;

            Runnable r = () -> adapter.notifyDataSetChanged();

            hand.post(r);
        }

    }

    /**
     Grabs and parses all the backend stored sports courts
     */
    private void grabGameInfo() {
        //Date info
        // get a calendar instance, which defaults to "now"
        Calendar cal = Calendar.getInstance();
        Date maxDate = cal.getTime(); //get right now, date/time
        maxDate.setTime((maxDate.getTime() + 300000)); //5 minutes in the future
        @SuppressLint("SimpleDateFormat") String s_maxDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(maxDate);
        //get initial date
        cal.add(Calendar.DAY_OF_WEEK, -2);
        Date minDate = cal.getTime();
        @SuppressLint("SimpleDateFormat") String s_minDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(minDate);
        
        String url = getString(R.string.grab_games_guest) + "lat=" + searchingLatitude + "&longt=" + searchingLongitude + "&minDate=" + s_minDate + "&maxDate=" + s_maxDate; //NEW supports args for lat and longt, if 0 then defaults
        Log.d("GuestUrl",url);

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try{
                JSONArray jsonArray = response.getJSONArray("games");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject game = jsonArray.getJSONObject(i);
                    JSONObject location = game.getJSONObject("gameLocation"); //NEW

                    //Current date is before the game date
                    //or if the game is today, is the current time before the game time
                    String time = game.getString("time").substring(0, 5) + game.getString("time").substring(8);
                    if(time.substring(0, 1).equals("0")){
                        time = time.substring(1);
                    }

                    addGame(game.getString("sport"), location.getString("name"), game.getString("date").substring(0, 12) + "    " + time, game.getString("score1"), game.getString("score2"), location.getDouble("lat"),location.getDouble("longt"));
                }

                //Sets number of courts text box information
                if(numGamesAtCourt == 1){
                    numGamesNearby.setText(numGamesAtCourt + " game near you last week");
                } else if(numGamesAtCourt > 1){
                    numGamesNearby.setText(numGamesAtCourt + " games near you last week");
                }
            } catch (JSONException e){
                numGamesNearby.setText("THAT DIDN'T WORK. Error :" + e.toString());
            }

        }, CustomVolley.volleyErrorToastListener(getApplicationContext()));

        mQueue.add(request);
    }
}
