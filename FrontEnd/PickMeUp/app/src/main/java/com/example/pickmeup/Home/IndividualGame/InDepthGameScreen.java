package com.example.pickmeup.Home.IndividualGame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.example.pickmeup.httpServices.CustomVolley;
import com.example.pickmeup.httpServices.RequestController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class InDepthGameScreen extends AppCompatActivity {

    private TextView courtName;
    private TextView address;
    private TextView timeOfGame;
    private TextView dateOfGame;
    private TextView team1;
    private TextView team2;
    private TextView milesAway;
    private TextView outOfPlayers;

    private String gameID;
    private String locationID;

    private int numPlayers;

    private RequestQueue mQueue;
    private RequestController mRequest;

    public double currentLatitude;
    public double currentLongitude;

    private Button gameAction;

    private LoggedInUser loggedInUser;

    /**
     * @param savedInstanceState last instance of InDepthGameScreen info
     */
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_depth_game_screen);

        //Updates notification bar to white
        updateNotificationBar();

        //call from any fragment in bottomHomeScreen
        //HomeViewModel instantiated in bottomHomeScreen
        HomeViewModel homeViewModel =
                ViewModelProviders.of(Objects.requireNonNull(this), new HomeViewModelFactory()).get(HomeViewModel.class);
        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login

        mQueue = Volley.newRequestQueue(InDepthGameScreen.this);

        //Locks orientation to portrait mode
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Sets variables to objects on screen
        gameAction = findViewById(R.id.join_game_button);
        courtName = findViewById(R.id.court_name);
        address = findViewById(R.id.address_of_game);
        timeOfGame = findViewById(R.id.time_of_game);
        dateOfGame = findViewById(R.id.date_of_game);
        team1 = findViewById(R.id.team_1_players);
        team2 = findViewById(R.id.team_2_players);
        milesAway = findViewById(R.id.miles_away_dynamic);
        outOfPlayers = findViewById(R.id.out_of_players);

        gameID = getIntent().getStringExtra("id");
        courtName.setText(getIntent().getStringExtra("courtName"));
        locationID = getIntent().getStringExtra("courtId");
        currentLatitude = Double.parseDouble(Objects.requireNonNull(getIntent().getStringExtra("currentLat")));
        currentLongitude = Double.parseDouble(Objects.requireNonNull(getIntent().getStringExtra("currentLongt")));

        grabGameInfo(true);
        grabCoordinates();

        gameAction.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InDepthGameScreen.this);
            if (gameAction.getText().toString().equals("JOIN GAME")) {
                alertDialogBuilder.setTitle("Select a team")

                .setPositiveButton(R.string.team_2, (dialog, which) -> {
                    JSONObject player;
                    player = new JSONObject(); //must follow table format
                    try {
                        player.put("userId", loggedInUser.getId());
                        player.put("gameId", gameID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addUserToGame(player.toString(), 2);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gameAction.setText("LEAVE GAME");
                    grabGameInfo(false);
                })

                .setNegativeButton(R.string.team_1, (dialogInterface, i) -> {
                    JSONObject player;
                    player = new JSONObject(); //must follow table format
                    try {
                        player.put("userId", loggedInUser.getId());
                        player.put("gameId", gameID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addUserToGame(player.toString(), 1);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gameAction.setText("LEAVE GAME");
                    grabGameInfo(false);
                })

                .show();
            } else {
                alertDialogBuilder.setTitle("Are you sure?")

                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    JSONObject player;
                    player = new JSONObject(); //must follow table format
                    try {
                        player.put("userId", loggedInUser.getId());
                        player.put("gameId", gameID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    removeUserFromGame(player.toString());
                    this.onBackPressed(); //leaves the game InDepthGameScreen
                })

                .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                })

                .show();
            }
        });
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
     * adds the player to the game on either team 1 or team 2
     * @param jsonObject = json object containing user info to add to the game table
     * @param teamNum is the team number the player wants to join
     */
    private void addUserToGame(final String jsonObject, int teamNum) {
        try {
            if(mRequest == null){
                mRequest = RequestController.getInstance(this.getApplicationContext());
            }

            String url;
            if(teamNum == 1){
                url = getString(R.string.add_user_to_team1);
            } else {
                url = getString(R.string.add_user_to_team2);
            }

            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Toast errorToast;
                switch(response){
                    case "true":
                        errorToast = Toast.makeText(InDepthGameScreen.this, "Joined! Good Luck!", Toast.LENGTH_SHORT);
                        errorToast.show();
                        break;
                    case "false":
                        errorToast = Toast.makeText(InDepthGameScreen.this, "Failed!", Toast.LENGTH_SHORT);
                        errorToast.show();
                        break;
                    default:
                        errorToast = Toast.makeText(InDepthGameScreen.this, "An error occurred", Toast.LENGTH_SHORT);
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
            mRequest.addToRequestQueue(request);

        } catch(Exception a){
            Log.d("json", "Error" + a.toString());
        }
    }

    /**
     * adds the player to the game on either team 1 or team 2
     * @param jsonObject = json object containing user info to add to the game table
     */
    private void removeUserFromGame(final String jsonObject) {
        try {
            if(mRequest == null){
                mRequest = RequestController.getInstance(this.getApplicationContext());
            }

            String url = getString(R.string.remove_player);

            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Toast errorToast;
                switch(response){
                    case "true":
                        errorToast = Toast.makeText(InDepthGameScreen.this, "Removed!", Toast.LENGTH_SHORT);
                        errorToast.show();
                        break;
                    case "false":
                        errorToast = Toast.makeText(InDepthGameScreen.this, "Failed to remove!", Toast.LENGTH_SHORT);
                        errorToast.show();
                        break;
                    default:
                        errorToast = Toast.makeText(InDepthGameScreen.this, "An error occurred", Toast.LENGTH_SHORT);
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
            mRequest.addToRequestQueue(request);

        } catch(Exception a){
            Log.d("json", "Error" + a.toString());
        }
    }

    /**
     * @param time of game
     * @param date of game
     * @param maxPlayers of the game
     */
    @SuppressLint("SetTextI18n")
    private void addGameInfo(String time, String date, Integer maxPlayers){
        time = time.substring(0, 5) + time.substring(8);
        if(time.substring(0, 1).equals("0")){
            timeOfGame.setText(time.substring(1));
        } else {
            timeOfGame.setText(time);
        }

        outOfPlayers.setText(numPlayers + "/" + maxPlayers);

        Date newDate = new Date(date);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newNewDate = new SimpleDateFormat("MMMM dd, yyyy");
        dateOfGame.setText(newNewDate.format(newDate));
    }


    /**
     * @param isFirstTime is the boolean value to tell if this is the first time the screen was refreshed
     * Grabs and parses the games information
     */
    private void grabGameInfo(Boolean isFirstTime) {
        numPlayers = 0;
        String url = getString(R.string.grab_games_by_id) + gameID; //add location id from home fragment textview box

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try{

               JSONObject players = response.getJSONObject("players");
                Iterator x = players.keys();
               @SuppressLint("UseSparseArrays") HashMap<Integer, String> pMap = new HashMap<>();

                while (x.hasNext()){ //populate general hashmap
                   String key = (String) x.next();
                   JSONObject obj = players.getJSONObject(key);
                   pMap.put(obj.getInt("id"), obj.getString("username")); //hashmap of all players keyed to userID with values of username

                    if(obj.getString("username").equals(loggedInUser.getUsername()) && isFirstTime){
                       gameAction.setText("LEAVE GAME");
                   }
               }

                for(int j = 1; j <= 10; j++){ //populate Lists
                    int responseID = response.optInt("p" + j + "Id"); //opt gets an int and if there isn't an int then defaults to 0

                    if(j <= 5 && response.has("p" + j + "Id") && responseID != 0){
                        numPlayers++;
                        if(j == 1){
                            team1.setText(pMap.get(response.getInt("p" + j + "Id")));
                        } else {
                            team1.append("\n" + pMap.get(response.getInt("p" + j + "Id")));
                        }
                    } else if ( response.has("p" + j + "Id")  && responseID != 0){
                        numPlayers++;
                        if(j == 6){
                            team2.setText(pMap.get(response.getInt("p" + j + "Id")));
                        } else {
                            team2.append("\n" + pMap.get(response.getInt("p" + j + "Id")));
                        }
                    }
                }
                addGameInfo(response.getString("time"), response.getString("date"), response.getInt("pMax"));
            } catch (JSONException e){
                courtName.setText("THAT DIDN'T WORK. Error :" + e.toString());
            }

        }, CustomVolley.volleyErrorToastListener(getApplicationContext()));

        mQueue.add(request);
    }

    /**
     * @param latitude of court
     * @param longitude of court
     * @return miles to the court from current location
     */
    public double calculateDistanceTo(double latitude, double longitude){
        double dLat = Math.toRadians(latitude - currentLatitude);
        double dLon = Math.toRadians(longitude - currentLongitude);

        //Calculation to grab the distance between current location and
        double a = (Math.pow(Math.sin(dLat/2), 2) + Math.cos(currentLatitude) * Math.cos(latitude) * (Math.pow(Math.sin(dLon/2), 2)));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 3963 * c; // Distance in miles
    }

    /**
     * calculates what courts are nearby to add to the home screen
     * @param latitude is the court latitude
     * @param longitude is the court longitude
     */
    @SuppressLint("SetTextI18n")
    public void decodeCoordinates(double latitude, double longitude){

        double d = calculateDistanceTo(latitude, longitude); // Distance in miles
        //sets distance to court from current location found on the home screen
        milesAway.setText(Double.toString(d).substring(0, 4));

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert addresses != null;
        if(addresses.size() > 0){
            String[] addressSegments = addresses.get(0).getAddressLine(0).split(", ");
            //Street, city, state initials
            address.setText(addressSegments[1] + ", " + addressSegments[2] + ", " + addressSegments[3].substring(0, 3));
        }
    }

    /**
     Grabs the location coordinates
     */
    private void grabCoordinates() {
        String url = getString(R.string.grab_court_by_id) + locationID; //add location id from home fragment textview box

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try{
                decodeCoordinates(response.getDouble("lat"), response.getDouble("longt"));
            } catch (JSONException e){
                courtName.setText("THAT DIDN'T WORK. Error :" + e.toString());
            }

        }, CustomVolley.volleyErrorToastListener(getApplicationContext()));

        mQueue.add(request);
    }
}
