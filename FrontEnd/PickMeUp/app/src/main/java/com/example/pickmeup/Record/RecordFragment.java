package com.example.pickmeup.Record;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pickmeup.Data.MsgCallBack;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.example.pickmeup.ViewModel.I_UserViewModel;
import com.example.pickmeup.httpServices.CustomVolley;
import com.example.pickmeup.httpServices.RequestController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecordFragment extends Fragment {

    private static boolean testMode = false; //for testing only

    //converts game time strings into Dates
    @SuppressLint("SimpleDateFormat") static SimpleDateFormat formatGameDate = new SimpleDateFormat("MMMM dd, yyyy");
    @SuppressLint("SimpleDateFormat") static SimpleDateFormat formatGameTime = new SimpleDateFormat("HH:mm:ss");


    private RequestController mQueue;

    private List<Record> records;
    private RecordAdapter adapter;

    @VisibleForTesting
    public static MsgCallBack onResponseCallback;

    //call from any fragment in bottomHomeScreen
    private I_UserViewModel homeViewModel; //HomeViewModel instantiated in bottomHomeScreen
    private LoggedInUser loggedInUser;

    private TextView testText;

    private volatile double longitude;
    private volatile double latitude;
    private String address;
    private volatile String courtName;

    //handler for updating ui view in ui thread, avoids Volley Errors "cannot update UI View on background thread"
    private static Handler testTextHandler;

    /**
     * @param inflater inflates the record fragment
     * @param container holds the fragment
     * @param savedInstanceState grabs the last saved instance of the fragment if possible
     * @return the fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //call from any fragment in bottomHomeScreen
        homeViewModel =
                ViewModelProviders.of(Objects.requireNonNull(getActivity()),  new HomeViewModelFactory()).get(HomeViewModel.class); //get instance of homeViewModel associated with BottomHomeScree

        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    /**
     * @param view of fragment
     * @param savedInstanceState grabs last saved instance of the fragment if possible
     */
    @SuppressWarnings("HandlerLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!testMode){ //
            init(view);
        }

    }

    /**
     * @param testMode tells us which test to run
     */
    public static void setTest(Boolean testMode){
        RecordFragment.testMode = testMode;
    }

    /**
     * initializes test mode information
     */
    public void testModeInit(){
        if(testMode){
            init(Objects.requireNonNull(this.getView()));
        }

    }

    /**
     * @param homeViewModel allows the test to access homeViewModel information
     */
    public void testModeViewModel(I_UserViewModel homeViewModel){
        if(testMode){
            this.homeViewModel = homeViewModel;
        }

    }

    /**
     * @param view of fragment
     * initializes all the fragment information
     */
    @SuppressLint("HandlerLeak")
    private void init(View view){
        testText = Objects.requireNonNull(getActivity()).findViewById(R.id.testText);


        testTextHandler =  new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 1){

                    testText.setText((String)msg.obj);
                }
                super.handleMessage(msg);
            }
        };
        mQueue = RequestController.getInstance(view.getContext());

        loggedInUser = homeViewModel.getLoggedInUser(); //get user from login

        records = new ArrayList<>();


        RecyclerView recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.record_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemViewCacheSize(20);

        adapter = new RecordAdapter(getActivity(), records) {
        };
        recyclerView.setAdapter(adapter);

        getUserGames();
    }


    /**
     * @param score of the game
     * @param date of the game
     * @param time of the game
     * @param winLoss of whether you won or lost that game
     */
    private void addGame(String score, String date, String time, String winLoss){
        time = time.substring(0, 5) + time.substring(8);
        if(time.substring(0, 1).equals("0")){
            time = time.substring(1);
        }
        if(getActivity() == null) return;

        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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
            //if the first substring contains an integer then grab the 0, 1, and 2nd options, otherwise skip the name and do the 1, 2 and 3rd position
            if(addressSegments[0].matches(".*\\d.*")){
                address = addressSegments[0] + ", " + addressSegments[1] + ", " + addressSegments[2].substring(0, 3);
            } else {
                address = addressSegments[1] + ", " + addressSegments[2] + ", " + addressSegments[3].substring(0, 3);
            }
        }

        records.add(
                new Record(
                        score,
                        address,
                        courtName,
                        date,
                        time,
                        winLoss
                )
        );
    }

    /**
     * grabs all completed and future games linked to user account
     */
    private void getUserGames() {
        String url = getString(R.string.grab_user_games) + loggedInUser.getId();

        @SuppressLint("SetTextI18n")
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            getUserGamesHelper(response);

            Runnable r = () -> adapter.notifyDataSetChanged();
            new Handler().post(r);

            if(onResponseCallback != null){
                onResponseCallback.onMessage(null);//for testing
            }

        }, CustomVolley.volleyErrorListener);
        mQueue.addToRequestQueue(request);
    }


    /**
     * @param response is the jsonOjbect when calling all the games linked to the account
     */
    private void getUserGamesHelper(JSONObject response){
        try {
            JSONArray jsonArray = response.getJSONArray("User " + loggedInUser.getId() + " Games");
            boolean firstPastGame = true;

            //if there are no games in the record, then let them know
            if(jsonArray.length() == 0){
                records.add(
                        new Record(
                                "",
                                "",
                                "No Games",
                                "",
                                "",
                                ""
                        )
                );
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject game = jsonArray.getJSONObject(i);

                //testTextHandler.sendMessage(testTextHandler.obtainMessage(1, "we're in"));

                JSONObject gameLocation = game.getJSONObject("gameLocation");
                latitude = gameLocation.getDouble("lat");
                longitude = gameLocation.getDouble("longt");
                courtName = gameLocation.getString("name");

                String winLossStatus = "";

                Date todayDate = formatGameDate.parse(formatGameDate.format(new Date()));
                Date todayTime = formatGameTime.parse(formatGameTime.format(new Date()));

                Date displayDate = formatGameDate.parse(game.getString("date"));
                Date displayTime = formatGameTime.parse(game.getString("time"));

                //If todays date is before after the games date then throw it into the Completed Game section
                if (Objects.requireNonNull(todayDate).compareTo(displayDate) > 0 ||
                        (todayDate.compareTo(displayDate) == 0 && Objects.requireNonNull(todayTime).compareTo(displayTime) >= 0)) {
                    if(firstPastGame){
                        records.add(
                                new Record(
                                        "",
                                        "",
                                        "Completed Games",
                                        "",
                                        "",
                                        ""
                                )
                        );
                        firstPastGame = false;
                    }
                    Log.d("outside if: server", Integer.toString(game.optInt("p7Id")));
                    Log.d("outside if: user", loggedInUser.getId().toString());
                    //if the game has a complete score then edit throw in the scores to be displayed
                    if (game.optInt("score1") != 0 && game.optInt("score2") != 0) {
                        boolean onTeam1 = false;
                        boolean onTeam2 = false;

                        //loops through games players and finds what team the current user was on
                        if(game.optInt("p1Id") != 0 && game.optInt("p1Id") == loggedInUser.getId()){
                            onTeam1 = true;
                        } else if(game.optInt("p2Id") != 0 && game.optInt("p2Id") == loggedInUser.getId()){
                            onTeam1 = true;
                        } else if(game.optInt("p3Id") != 0 && game.optInt("p3Id") == loggedInUser.getId()){
                            onTeam1 = true;
                        } else if(game.optInt("p4Id") != 0 && game.optInt("p4Id") == loggedInUser.getId()){
                            onTeam1 = true;
                        } else if(game.optInt("p5Id") != 0 && game.optInt("p5Id") == loggedInUser.getId()){
                            onTeam1 = true;
                        } else if(game.optInt("p6Id") != 0 && game.optInt("p6Id") == loggedInUser.getId()){
                            onTeam2 = true;
                        } else if(game.optInt("p7Id") != 0 && game.optInt("p7Id") == loggedInUser.getId()){
                            onTeam2 = true;
                        } else if(game.optInt("p8Id") != 0 && game.optInt("p8Id") == loggedInUser.getId()){
                            onTeam2 = true;
                        } else if(game.optInt("p9Id") != 0 && game.optInt("p9Id") == loggedInUser.getId()){
                            onTeam2 = true;
                        } else if(game.optInt("p10Id") != 0 && game.optInt("p10Id") == loggedInUser.getId()){
                            onTeam2 = true;
                        }

                        if(onTeam1){
                            if (game.optInt("score1") > game.optInt("score2")) {
                                winLossStatus = "W";
                            } else {
                                winLossStatus = "L";
                            }
                        } else if(onTeam2){
                            if (game.optInt("score2") > game.optInt("score1")) {
                                winLossStatus = "W";
                            } else {
                                winLossStatus = "L";
                            }
                        }
                        addGame(game.getInt("score1") + " - " + game.getInt("score2"), Objects.requireNonNull(displayDate).toString().substring(0, 10), game.getString("time"), winLossStatus);
                    } else { //if the game is in the past, but doesn't have a complete score then pass the score 0 - 0
                        addGame("0 - 0", Objects.requireNonNull(displayDate).toString().substring(0, 10), game.getString("time"), "D");
                    }
                } else { //if the game is upcoming show the everything, but mute the WIN/LOSS box
                    if(i == 0){
                        records.add(
                                new Record(
                                        "",
                                        "",
                                        "Upcoming Games",
                                        "",
                                        "",
                                        ""
                                )
                        );
                    }
                    addGame("", Objects.requireNonNull(displayDate).toString().substring(0, 10), game.getString("time"), "");
                }
            }
        } catch (JSONException e) {
            Log.d("RecordFragment", e.toString());
            // testTextHandler.sendMessage(testTextHandler.obtainMessage(1, "THAT DIDN'T WORK. Error :" + e.toString()));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return for testing, returns copy of list
     */
    @VisibleForTesting
    public List<Record> getRecords(){
        return new ArrayList<>(records);
    }

    /**
     * handles when the app is destroyed
     */
    @Override
    public void onDestroy(){
        testTextHandler = null;
        super.onDestroy();
    }
}