package com.example.pickmeup.Home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pickmeup.Court.Court;
import com.example.pickmeup.Court.CourtAdapter;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.example.pickmeup.httpServices.CustomVolley;
import com.example.pickmeup.httpServices.RequestController;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.android.volley.VolleyLog.TAG;


public class HomeFragment extends Fragment {
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

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

    private RequestController mQueue;

    //TEMP TEXT BOX FOR OUTPUTTING TEST DATA
    private TextView test_text_box;

    //displays number of courts found within the search radius
    private TextView num_courts_text;

    //Radius that we're searching around for courts, set by user, defaulted to 10 miles
    private int searchRadius = 10;
    //number of courts within our radius. Used to change the text on the front screen
    private int numCourtsNearby = 0;

    //variables stored when we find the current location from BottomHomeScreen
    private double searchingLatitude = 0;
    private double searchingLongitude = 0;

    //call from any fragment in bottomHomeScreen
    private HomeViewModel homeViewModel; //HomeViewModel instantiated in bottomHomeScreen
    private LoggedInUser loggedInUser;

    private List<Court> courts;

    private Handler locationHandler;
    private Runnable autoLocation;

    private ArrayList<String> addressesArray;
    private HashMap<String, String> addressesMap;
    private CourtAdapter adapter;
    private Handler hand;
    private Spinner addresses;

    private Button radiusFilter;
    private Button weekdayFilter;
    private Button weekendFilter;
    private TextView distanceDisplay;
    private Animation bottomDown;
    private ViewGroup distancePanel;

    /**
     * @param inflater to grab fragment info
     * @param container to hold fragment
     * @param savedInstanceState holds the most recently saved instance of the state
     * @return inflated fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //call from any fragment in bottomHomeScreen
        homeViewModel =
                ViewModelProviders.of(Objects.requireNonNull(getActivity()), new HomeViewModelFactory()).get(HomeViewModel.class); //get instance of homeViewModel associated with BottomHomeScreen

        //once the class is called it will fetch the home fragment for viewing
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * @param view of the fragment
     * @param savedInstanceState last saved instance of the fragment activity
     */
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        hand = new Handler();

        courts = new ArrayList<>();
        RecyclerView recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.court_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new CourtAdapter(getActivity(), courts);
        recyclerView.setAdapter(adapter);

        setUpFilters();

        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mQueue = RequestController.getInstance(view.getContext());

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            //userLocationChoice = savedInstanceState.getInt(BUNDLE_USER_ID);
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        Places.initialize(getActivity().getApplicationContext(), "AIzaSyBmDcA7oP050yM98gpMSPblAPbOsabHBhM");
        placesClient = Places.createClient(getActivity());

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //TEMP TEXT AREA
        test_text_box = Objects.requireNonNull(getView()).findViewById(R.id.test_text_box);
        //Number of courts displayed on the home screen
        num_courts_text = getView().findViewById(R.id.number_of_courts);

        getCurrentLocation();

        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login

        addressesArray = new ArrayList<>();
        addressesMap = new HashMap<>();
        grabAddresses();

        addresses = getActivity().findViewById(R.id.address_button);
        ArrayAdapter<String> addressesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, addressesArray);
        addresses.setAdapter(addressesAdapter);

        ImageView dropDownArrow = getActivity().findViewById(R.id.address_arrow_dropdown);
        dropDownArrow.setOnClickListener(view1 -> addresses.performClick());
        TextView dropDownExpansion = getActivity().findViewById(R.id.address_dropdown_expansion);
        dropDownExpansion.setOnClickListener(view12 -> addresses.performClick());

        addresses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * @param parentView of the addresses
             * @param selectedItemView of the spinner list
             * @param position of item selected
             * @param id ??
             */
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("current item", addresses.getSelectedItem().toString());

                initCourtInfo();

                String[] coordinates = Objects.requireNonNull(addressesMap.get(addresses.getSelectedItem().toString())).split(",");
                searchingLatitude = Double.parseDouble(coordinates[0]);
                searchingLongitude = Double.parseDouble(coordinates[1]);
                Log.d("item", coordinates[0] + ", " + coordinates[1]);
                grabCourtInfo();

                adapter.notifyDataSetChanged();
                Log.d("court size", Integer.toString(courts.size()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //DO NOTHING
            }
        });

        @SuppressLint("ClickableViewAccessibility") View.OnTouchListener handleTouch = (v, event) -> {

            Display display = v.getDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;

            int x = (int) event.getX();
            int y = (int) event.getY();

            Log.d("test", Integer.toString(y));

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("TAG", "touched down");
                    if(y < (height - 230)){
                        closePanel();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("TAG", "touched up");
                    break;
            }

            return true;
        };

        Button submitDistanceChange = getActivity().findViewById(R.id.submit_distance_change);
        submitDistanceChange.setOnClickListener(view13 -> {
            //if the searching radius doesn't equal the default value, color the tile
            if(Integer.parseInt(distanceDisplay.getText().toString().substring(0, distanceDisplay.getText().toString().length() - 3)) != 10){
                radiusFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                radiusFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));
            } else {
                radiusFilter.setTextColor(getResources().getColor(R.color.quantum_black_100));
                radiusFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_grey_border));
            }

            //searching radius changed
            if(Integer.parseInt(distanceDisplay.getText().toString().substring(0, distanceDisplay.getText().toString().length() - 3)) != searchRadius){
                searchRadius = Integer.parseInt(distanceDisplay.getText().toString().substring(0, distanceDisplay.getText().toString().length() - 3));
                numCourtsNearby = 0;
                courts.clear();
                grabCourtInfo();
                adapter.notifyDataSetChanged();
            }
            closePanel();
        });
    }

    /**
     * sets up filter information for the general, date and time tabs
     */
    @SuppressLint("SetTextI18n")
    private void setUpFilters(){
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);

        distancePanel = Objects.requireNonNull(getActivity()).findViewById(R.id.distance_panel);
        distancePanel.setVisibility(View.GONE);

        radiusFilter = getActivity().findViewById(R.id.distance_filter_home);
        weekdayFilter = getActivity().findViewById(R.id.weekday_filter_home);
        weekendFilter = getActivity().findViewById(R.id.weekend_filter_home);
        SeekBar maxDistance = getActivity().findViewById(R.id.distance_bar);
        distanceDisplay = getActivity().findViewById(R.id.distance_display);
        ImageView closeDistance = getActivity().findViewById(R.id.close_distance);

        distanceDisplay.setText(searchRadius + " mi");
        maxDistance.setProgress(searchRadius);
        maxDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(seekBar.getProgress() == 0){
                    distanceDisplay.setText("1 mi");
                } else {
                    distanceDisplay.setText(seekBar.getProgress() + " mi");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radiusFilter.setOnClickListener(view13 -> {
            if(distancePanel.getVisibility() == View.VISIBLE){
                closePanel();
            } else {
                adapter.isClickable = false;
                distancePanel.setVisibility(View.VISIBLE);
                distancePanel.startAnimation(bottomUp);
            }
        });

        weekdayFilter.setOnClickListener(view14 -> {
            if(weekdayFilter.getCurrentTextColor() == getResources().getColor(R.color.quantum_black_100)){
                weekdayFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                weekdayFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));
            } else {
                weekdayFilter.setTextColor(getResources().getColor(R.color.quantum_black_100));
                weekdayFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_grey_border));
            }
        });

        weekendFilter.setOnClickListener(view15 -> {
            if(weekendFilter.getCurrentTextColor() == getResources().getColor(R.color.quantum_black_100)){
                weekendFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                weekendFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));
            } else {
                weekendFilter.setTextColor(getResources().getColor(R.color.quantum_black_100));
                weekendFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_grey_border));
            }
        });

        closeDistance.setOnClickListener(view16 -> closePanel());
    }

    /**
     * closes general tab
     */
    private void closePanel(){
        adapter.isClickable = true;
        distancePanel.startAnimation(bottomDown);
        distancePanel.setVisibility(View.GONE);
    }

    /**
     * when the fragment loses focus remove the locationHandler
     */
    @Override
    public void onPause(){
        if(locationHandler != null){
            locationHandler.removeCallbacks(autoLocation); //stop runnable from calling getActivity() while it's null
        }

        super.onPause();
    }

    /**
     * initializes the home fragment screen to default case
     */
    @SuppressLint("SetTextI18n")
    private void initCourtInfo(){
        num_courts_text.setText("NO COURTS NEAR YOU");
        courts.clear();
        numCourtsNearby = 0;
    }

    /**
     * checks if user allowed them to access location and if not then ask and grab location info
     */
    private void getCurrentLocation(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_FINE_LOCATION)) {
                //Displays alert box with option to cancel or continue to allow location services
                new AlertDialog.Builder(getActivity())
                        .setTitle("Permission needed")
                        .setMessage("Location permission is needed to locate nearby players and courts")
                        .setPositiveButton("ok", (dialogInterface, i) -> ActivityCompat.requestPermissions(getActivity(),
                                new String[]{ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION))
                        .setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            //Location services already permitted, get the current location and translate it to an address
            mLocationPermissionGranted = true;
            getDeviceLocation();
            showCurrentPlace();
        }

        if(mLocationPermissionGranted){
            locationHandler = new Handler();
            //need reference to stop runner
            autoLocation = () -> {
                mLocationPermissionGranted = true;
                getDeviceLocation();
                showCurrentPlace();
            };
            locationHandler.postDelayed(autoLocation,300000); //5 minutes
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        if(getActivity() == null){ //causes null pointer exception causing app to crash
            Log.e("HomeFrag", "getActivity null in getDeviceLocation");
            return;
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        if(mLastKnownLocation == null){
                            Log.e("HomeFrag", "mLastKnownLocation null in getDeviceLocation");
                            return;
                        }
                        addressesArray.add("Current Location");
                        addressesMap.put("Current Location", mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
                        Runnable r = () -> adapter.notifyDataSetChanged();
                        hand.post(r);
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
     * calculates what courts are nearby to add to the home screen
     * @param latitude is the court latitude
     * @param longitude is the court longitude
     * @param name is the court name
     */
    private void grabNearbyCourt(double latitude, double longitude, String name, int locationID){
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

        if(d <= searchRadius){
            String drawableName = name.replace(' ', '_');
            drawableName = drawableName.toLowerCase();

            //increment the number of courts found
            numCourtsNearby++;

            if(courts != null){
                if(getActivity() == null) return;
                courts.add(
                        new Court(
                                numCourtsNearby,
                                locationID,
                                name,
                                topTwoD,
                                Objects.requireNonNull(getActivity()).getResources().getIdentifier(drawableName, "drawable", getActivity().getPackageName()),
                                searchingLatitude,
                                searchingLongitude,
                                "No Games"
                        ));
            }

            Runnable r = () -> adapter.notifyDataSetChanged();

            hand.post(r);
        }
    }

    /**
     * Grabs and parses all the backend stored sports courts
     */
    private void grabCourtInfo() {
        String url = getString(R.string.grab_court_info_all);

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try{

                JSONArray jsonArray = response.getJSONArray("locations");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject court = jsonArray.getJSONObject(i);
                    if(court != null){
                        grabNearbyCourt(court.getDouble("lat"), court.getDouble("longt"), court.getString("name"), court.getInt("id"));
                    }
                }
                Collections.sort(courts, Court.sortByDist);

                //Sets number of courts text box information
                if(numCourtsNearby == 1){
                    num_courts_text.setText(numCourtsNearby + " court near you " + loggedInUser.getUsername());
                } else if(numCourtsNearby > 1){
                    num_courts_text.setText(numCourtsNearby + " courts near you " + loggedInUser.getUsername());
                }
            } catch (JSONException e){
                test_text_box.setText("THAT DIDN'T WORK. Error :" + e.toString());
            }

        }, CustomVolley.volleyErrorListener);

        mQueue.addToRequestQueue(request);
    }

    /**
     * Grabs all locations linked to the user
     */
    private void grabAddresses() {
        for(int i = 0; i < loggedInUser.getUserLocations().size(); i++) {
            addressesArray.add(loggedInUser.getUserLocations().get(i).getName());
            addressesMap.put(loggedInUser.getUserLocations().get(i).getName(), loggedInUser.getUserLocations().get(i).getLat() + "," + loggedInUser.getUserLocations().get(i).getLongt());
        }
    }
}
