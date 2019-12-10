package com.example.pickmeup.Home.CourtGames;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.pickmeup.Game.Game;
import com.example.pickmeup.Game.GameAdapter;
import com.example.pickmeup.R;
import com.example.pickmeup.httpServices.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocationGamesScreen extends AppCompatActivity {

    private RequestQueue mQueue;

    TextView numGames;
    private TextView addGame;

    //number of courts within our radius. Used to change the text on the front screen
    int numGamesAtCourt = 0;

    private String courtName;
    private String locationID;
    private String currentLatitude;
    private String currentLongitude;

    RecyclerView recyclerView;
    GameAdapter adapter;
    List<Game> games;

    private Button sportFilter;
    private Button weekdayFilter;
    private Button weekendFilter;
    private Button sizeFilter;
    private Button difficultyFilter;
    private ViewGroup gamePanel;
    private Animation bottomDown;
    private Animation bottomUp;
    private TextView filterType;
    private ViewGroup timePanel;
    private ViewGroup datePanel;
    private Button submitTimeChanges;

    //Variables to save state of filter
    private int sportPos;
    private int beginMonthPos;
    private int beginDayPos;
    private int beginYearPos;
    private int endMonthPos;
    private int endDayPos;
    private int endYearPos;
    private int beginTimePos;
    private int endTimePos;
    private int sizePos;
    private int difficultyPos;

    /**
     * @param savedInstanceState saved state of LocationGamesScreen activity
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_games_screen);

        addGame = findViewById(R.id.add_new_game);

        games = new ArrayList<>();

        recyclerView = this.findViewById(R.id.games_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GameAdapter(this, games);
        recyclerView.setAdapter(adapter);

        Activity a = this;
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Updates notification bar to white
        updateNotificationBar();

        locationID = getIntent().getStringExtra("id");
        courtName = getIntent().getStringExtra("courtName");
        currentLatitude = getIntent().getStringExtra("currentLat");
        currentLongitude = getIntent().getStringExtra("currentLongt");
        getIntent();

        mQueue = Volley.newRequestQueue(this);

        numGames = findViewById(R.id.num_games_at_court);

        grabGameInfo();

        addGame.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("id", locationID);
            bundle.putString("courtName", courtName);

            Intent intent = new Intent(this, AddNewGameScreen.class).putExtras(bundle);
            startActivity(intent);
        });

        init_filters();
    }

    /**
     * initializes all the filter information
     */
    @SuppressLint("SetTextI18n")
    private void init_filters(){
        Spinner spinnerList = findViewById(R.id.games_list);
        Spinner beginTime = findViewById(R.id.begin_time);
        Spinner endTime = findViewById(R.id.end_time);
        Spinner beginMonth = findViewById(R.id.begin_date_month);
        Spinner beginDay = findViewById(R.id.begin_date_day);
        Spinner beginYear = findViewById(R.id.begin_date_year);
        Spinner endMonth = findViewById(R.id.end_date_month);
        Spinner endDay = findViewById(R.id.end_date_day);
        Spinner endYear = findViewById(R.id.end_date_year);

        filterType = findViewById(R.id.game_static);

        bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

        gamePanel = findViewById(R.id.sport_panel);
        gamePanel.setVisibility(View.GONE);

        timePanel = findViewById(R.id.time_panel);
        timePanel.setVisibility(View.GONE);

        datePanel = findViewById(R.id.date_panel);
        datePanel.setVisibility(View.GONE);

        submitTimeChanges = findViewById(R.id.submit_time_changes);
        ImageView closeTimeChanges = findViewById(R.id.close_time);
        Button submitDateChanges = findViewById(R.id.submit_date_changes);
        ImageView closeDateChanges = findViewById(R.id.close_date);

        sportFilter = findViewById(R.id.sport_filter);
        sportFilter.setOnClickListener(view13 -> {
            checkDefaultPanel();

            if(gamePanel.getVisibility() == View.VISIBLE){
                closePanel();
            } else {
                filterType.setText("Sport");
                String[] sportListArray = new String[] {
                        "All", "Basketball", "Baseball", "Soccer", "Tennis", "Frisbee", "Tennis", "Football"
                };
                ArrayAdapter<String> sportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sportListArray);
                spinnerList.setAdapter(sportAdapter);
                if(sportPos != 0){
                    spinnerList.setSelection(sportPos);
                }

                openPanel();
            }
        });

        ImageView closeSport = findViewById(R.id.close_sport);

        closeSport.setOnClickListener(view -> {
            sportPos = spinnerList.getSelectedItemPosition(); //saves selected sport position
            closePanel();
        });

        weekdayFilter = findViewById(R.id.weekday_filter);
        weekdayFilter.setOnClickListener(view14 -> {
            if(weekdayFilter.getCurrentTextColor() == getResources().getColor(R.color.quantum_black_100)){
                weekdayFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                weekdayFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));
            } else {
                weekdayFilter.setTextColor(getResources().getColor(R.color.quantum_black_100));
                weekdayFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_grey_border));
            }
        });

        weekendFilter = findViewById(R.id.weekend_filter);
        weekendFilter.setOnClickListener(view15 -> {
            if(weekendFilter.getCurrentTextColor() == getResources().getColor(R.color.quantum_black_100)){
                weekendFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                weekendFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));
            } else {
                weekendFilter.setTextColor(getResources().getColor(R.color.quantum_black_100));
                weekendFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_grey_border));
            }
        });

        Button dateFilter = findViewById(R.id.date_filter);
        dateFilter.setOnClickListener(view13 -> {
            if(datePanel.getVisibility() == View.VISIBLE){
                closeDatePanel();
            } else {
                checkDatePanel();
                
                String[] defaultMonthList = new String[] {
                        "January", "February", "March", "April", "May", "June", "July", "August",
                        "September", "October", "November", "December"
                };

                String[] defaultDayList = new String[] {
                        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
                        "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
                        "28", "29", "30", "31"
                };

                int year = Calendar.getInstance().get(Calendar.YEAR);
                String[] defaultYearList = new String[] {
                    Integer.toString(year), Integer.toString(year + 1)
                };

                ArrayAdapter<String> beginMonthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultMonthList);
                beginMonth.setAdapter(beginMonthAdapter);
                ArrayAdapter<String> beginDayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultDayList);
                beginDay.setAdapter(beginDayAdapter);
                ArrayAdapter<String> beginYearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultYearList);
                beginYear.setAdapter(beginYearAdapter);
                ArrayAdapter<String> endMonthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultMonthList);
                endMonth.setAdapter(endMonthAdapter);
                ArrayAdapter<String> endDayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultDayList);
                endDay.setAdapter(endDayAdapter);
                ArrayAdapter<String> endYearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultYearList);
                endYear.setAdapter(endYearAdapter);

                ArrayList<String> updatedDaysListBegin = new ArrayList<>();
                ArrayList<String> updatedDaysListEnd = new ArrayList<>();

                beginMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        int numDaysOff; //gets rid of 31 day months case
                        updatedDaysListBegin.clear();
                        if(beginMonth.getSelectedItem() == "January" || beginMonth.getSelectedItem() == "March" || beginMonth.getSelectedItem() == "May" || beginMonth.getSelectedItem() == "July" ||
                                beginMonth.getSelectedItem() == "August" || beginMonth.getSelectedItem() == "October" || beginMonth.getSelectedItem() == "December"){
                            numDaysOff = 0;
                        } else if(beginMonth.getSelectedItem() == "April" || beginMonth.getSelectedItem() == "June" || beginMonth.getSelectedItem() == "September" || beginMonth.getSelectedItem() == "November"){ //30 days
                            numDaysOff = 1;
                        } else { //February
                            if(Integer.parseInt(beginYear.getSelectedItem().toString()) % 4 == 0){ //leap year
                                numDaysOff = 2;
                            } else {
                                numDaysOff = 3;
                            }
                        }
                        updatedDaysListBegin.addAll(Arrays.asList(defaultDayList).subList(0, defaultDayList.length - numDaysOff));
                        ArrayAdapter<String> newBeginDayAdapter = new ArrayAdapter<>(LocationGamesScreen.this, android.R.layout.simple_spinner_dropdown_item, updatedDaysListBegin);
                        beginDay.setAdapter(newBeginDayAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                endMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        int numDaysOff; //gets rid of 31 day months case
                        updatedDaysListEnd.clear();
                        if(beginMonth.getSelectedItem() == "January" || beginMonth.getSelectedItem() == "March" || beginMonth.getSelectedItem() == "May" || beginMonth.getSelectedItem() == "July" ||
                                beginMonth.getSelectedItem() == "August" || beginMonth.getSelectedItem() == "October" || beginMonth.getSelectedItem() == "December"){
                            numDaysOff = 0;
                        } else if(endMonth.getSelectedItem() == "April" || endMonth.getSelectedItem() == "June" || endMonth.getSelectedItem() == "September" || endMonth.getSelectedItem() == "November"){ //30 days
                            numDaysOff = 1;
                        } else { //February
                            if(Integer.parseInt(beginYear.getSelectedItem().toString()) % 4 == 0){ //leap year
                                numDaysOff = 2;
                            } else {
                                numDaysOff = 3;
                            }
                        }
                        updatedDaysListEnd.addAll(Arrays.asList(defaultDayList).subList(0, defaultDayList.length - numDaysOff));
                        ArrayAdapter<String> newBeginDayAdapter = new ArrayAdapter<>(LocationGamesScreen.this, android.R.layout.simple_spinner_dropdown_item, updatedDaysListEnd);
                        endDay.setAdapter(newBeginDayAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                beginYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(beginMonth.getSelectedItem().toString().equals("February")) {
                            int numDaysOff;
                            updatedDaysListBegin.clear();
                            if (Integer.parseInt(beginYear.getSelectedItem().toString()) % 4 == 0) {
                                numDaysOff = 2;
                            } else {
                                numDaysOff = 3;
                            }
                            updatedDaysListBegin.addAll(Arrays.asList(defaultDayList).subList(0, defaultDayList.length - numDaysOff));
                            ArrayAdapter<String> newEndDayAdapter = new ArrayAdapter<>(LocationGamesScreen.this, android.R.layout.simple_spinner_dropdown_item, updatedDaysListBegin);
                            beginDay.setAdapter(newEndDayAdapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                endYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(endMonth.getSelectedItem().toString().equals("February")){
                            int numDaysOff;
                            updatedDaysListEnd.clear();
                            if(Integer.parseInt(endYear.getSelectedItem().toString()) % 4 == 0){
                                numDaysOff = 2;
                            } else {
                                numDaysOff = 3;
                            }
                            updatedDaysListEnd.addAll(Arrays.asList(defaultDayList).subList(0, defaultDayList.length - numDaysOff));
                            ArrayAdapter<String> newEndDayAdapter = new ArrayAdapter<>(LocationGamesScreen.this, android.R.layout.simple_spinner_dropdown_item, updatedDaysListEnd);
                            endDay.setAdapter(newEndDayAdapter);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                //Sets the variables to the saved state
                if(beginMonthPos != 0){
                    beginMonth.setSelection(beginMonthPos);
                }
                if(beginYearPos != 0){
                    beginYear.setSelection(beginYearPos);
                }
                if(endMonthPos != 0){
                    endMonth.setSelection(endMonthPos);
                }
                if(endYearPos != 0){
                    endYear.setSelection(endYearPos);
                }
                if(beginDayPos != 0){
                    beginDay.setSelection(beginDayPos);
                }
                if(endDayPos != 0){
                    endDay.setSelection(endDayPos);
                }

                openDatePanel();
            }
        });

        closeDateChanges.setOnClickListener(view -> closeDatePanel());

        submitDateChanges.setOnClickListener(view -> {
            if(endYear.getSelectedItemPosition() < beginYear.getSelectedItemPosition()){ //year is before
                Toast errorToast = Toast.makeText(LocationGamesScreen.this, "End date is before begin date", Toast.LENGTH_SHORT);
                errorToast.show();
            } else if((endYear.getSelectedItemPosition() == beginYear.getSelectedItemPosition())
                && endMonth.getSelectedItemPosition() < beginMonth.getSelectedItemPosition()){ //year is the same but month is before
                Toast errorToast = Toast.makeText(LocationGamesScreen.this, "End date is before begin date", Toast.LENGTH_SHORT);
                errorToast.show();
            } else if((Integer.parseInt(endYear.getSelectedItem().toString()) == Integer.parseInt(beginYear.getSelectedItem().toString()))
                    && endMonth.getSelectedItemPosition() == beginMonth.getSelectedItemPosition()
                    && endDay.getSelectedItemPosition() < beginDay.getSelectedItemPosition()){ //Month and year are the same for both but day is before
                Toast errorToast = Toast.makeText(LocationGamesScreen.this, "End date is before begin date", Toast.LENGTH_SHORT);
                errorToast.show();
            } else { //date is valid
                closeDatePanel();
                dateFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                dateFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));

                //saves date variables states
                beginMonthPos = beginMonth.getSelectedItemPosition();
                beginDayPos = beginDay.getSelectedItemPosition();
                beginYearPos = beginYear.getSelectedItemPosition();
                endMonthPos = endMonth.getSelectedItemPosition();
                endDayPos = endDay.getSelectedItemPosition();
                endYearPos = endYear.getSelectedItemPosition();
            }
        });

        Button timeFilter = findViewById(R.id.time_filter);
        timeFilter.setOnClickListener(view13 -> {
            if(timePanel.getVisibility() == View.VISIBLE){
                closeTimePanel();
            } else {
                checkTimePanel();

                ArrayList<String> defaultTimeList = new ArrayList<>();
                defaultTimeList.add("12:00am");
                defaultTimeList.add("12:30am");
                defaultTimeList.add("1:00am");
                defaultTimeList.add("1:30am");
                defaultTimeList.add("2:00am");
                defaultTimeList.add("2:30am");
                defaultTimeList.add("3:00am");
                defaultTimeList.add("3:30am");
                defaultTimeList.add("4:00am");
                defaultTimeList.add("4:30am");
                defaultTimeList.add("5:00am");
                defaultTimeList.add("5:30am");
                defaultTimeList.add("6:00am");
                defaultTimeList.add("6:30am");
                defaultTimeList.add("7:00am");
                defaultTimeList.add("7:30am");
                defaultTimeList.add("8:00am");
                defaultTimeList.add("8:30am");
                defaultTimeList.add("9:00am");
                defaultTimeList.add("9:30am");
                defaultTimeList.add("10:00am");
                defaultTimeList.add("10:30am");
                defaultTimeList.add("11:00am");
                defaultTimeList.add("11:30am");
                defaultTimeList.add("12:00pm");
                defaultTimeList.add("12:30pm");
                defaultTimeList.add("1:00pm");
                defaultTimeList.add("1:30pm");
                defaultTimeList.add("2:00pm");
                defaultTimeList.add("2:30pm");
                defaultTimeList.add("3:00pm");
                defaultTimeList.add("3:30pm");
                defaultTimeList.add("4:00pm");
                defaultTimeList.add("4:30pm");
                defaultTimeList.add("5:00pm");
                defaultTimeList.add("5:30pm");
                defaultTimeList.add("6:00pm");
                defaultTimeList.add("6:30pm");
                defaultTimeList.add("7:00pm");
                defaultTimeList.add("7:30pm");
                defaultTimeList.add("8:00pm");
                defaultTimeList.add("8:30pm");
                defaultTimeList.add("9:00pm");
                defaultTimeList.add("9:30pm");
                defaultTimeList.add("10:00pm");
                defaultTimeList.add("10:30pm");
                defaultTimeList.add("11:00pm");
                defaultTimeList.add("11:30pm");

                ArrayList<String> startTimeList = new ArrayList<>(defaultTimeList);
                ArrayList<String> endTimeList = new ArrayList<>(defaultTimeList);

                ArrayAdapter<String> beginTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultTimeList);
                beginTime.setAdapter(beginTimeAdapter);
                ArrayAdapter<String> endTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, defaultTimeList);
                endTime.setAdapter(endTimeAdapter);

//ATTEMPT TO ACTIVELY REDO TIME LISTS AS THE USER SELECTS TIMES, BUT FAILED
//                beginTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                        if(endTime.getSelectedItemPosition() < beginTime.getSelectedItemPosition() && beginTime.getSelectedItem() != endTime.getSelectedItem()){
//                            endTimeList.clear();
//                            endTimeList.addAll(defaultTimeList.subList(position, defaultTimeList.size()));
//                            ArrayAdapter<String> newEndTimeAdapter = new ArrayAdapter<>(LocationGamesScreen.this, android.R.layout.simple_spinner_dropdown_item, endTimeList);
//                            endTime.setAdapter(newEndTimeAdapter);
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//                        //DO NOTHING
//                    }
//                });
//
//                endTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                        if(beginTime.getSelectedItemPosition() > endTime.getSelectedItemPosition() && beginTime.getSelectedItem() != endTime.getSelectedItem()){
//                            startTimeList.clear();
//                            startTimeList.addAll(defaultTimeList.subList(0, position));
//                            ArrayAdapter<String> newBeginTimeAdapter = new ArrayAdapter<>(LocationGamesScreen.this, android.R.layout.simple_spinner_dropdown_item, startTimeList);
//                            beginTime.setAdapter(newBeginTimeAdapter);
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//                        //DO NOTHING
//                    }
//                });

                if(beginTimePos != 0){
                    beginTime.setSelection(beginTimePos);
                }

                if(endTimePos != 0){
                    endTime.setSelection(endTimePos);
                }

                openTimePanel();

                submitTimeChanges.setOnClickListener(view -> {
                    if(beginTime.getSelectedItemPosition() > endTime.getSelectedItemPosition()){
                        Toast errorToast = Toast.makeText(LocationGamesScreen.this, "Please enter a real time frame", Toast.LENGTH_SHORT);
                        errorToast.show();
                    } else {
                        timeFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                        timeFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));

                        //saves time variable states
                        beginTimePos = beginTime.getSelectedItemPosition();
                        endTimePos = endTime.getSelectedItemPosition();

                        closeTimePanel();
                    }
                });
            }
        });

        closeTimeChanges.setOnClickListener(view -> closeTimePanel());

        sizeFilter = findViewById(R.id.size_filter);
        sizeFilter.setOnClickListener(view13 -> {
            checkDefaultPanel();

            if(gamePanel.getVisibility() == View.VISIBLE){
                closePanel();
            } else {
                filterType.setText("Size");
                String[] difficultyList = new String[] {
                        "All", "1v1", "2v2", "3v3", "4v4", "5v5"
                };
                ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, difficultyList);
                spinnerList.setAdapter(difficultyAdapter);

                if(sizePos != 0){
                    spinnerList.setSelection(sizePos);
                }

                openPanel();
            }
        });

        difficultyFilter = findViewById(R.id.difficulty_filter);
        difficultyFilter.setOnClickListener(view13 -> {
            checkDefaultPanel();

            if(gamePanel.getVisibility() == View.VISIBLE){
                closePanel();
            } else {
                filterType.setText("Difficulty");
                String[] difficultyList = new String[] {
                        "All", "Beginner", "Intermediate", "Expert"
                };
                ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, difficultyList);
                spinnerList.setAdapter(difficultyAdapter);

                if(difficultyPos != 0){
                    spinnerList.setSelection(difficultyPos);
                }

                openPanel();
            }
        });

        Button submitGameChange = findViewById(R.id.submit_game_change);
        submitGameChange.setOnClickListener(view -> {
            if(filterType.getText().toString().equals("Sport")){
                sportPos = spinnerList.getSelectedItemPosition();
                //if the sports list item doesn't equal All then query based on the sport selected
                if(!spinnerList.getSelectedItem().toString().equals("All")){
                    sportFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                    sportFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));
                    reset_games();
                } else { //query all games at this court
                    sportFilter.setTextColor(getResources().getColor(R.color.quantum_black_100));
                    sportFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_grey_border));
                    reset_games();
                    grabGameInfo();
                }
            } else if(filterType.getText().toString().equals("Difficulty")){
                difficultyPos = spinnerList.getSelectedItemPosition();
                if(!spinnerList.getSelectedItem().toString().equals("All")){
                    difficultyFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                    difficultyFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));
                    reset_games();
                } else { //query all games at this court
                    difficultyFilter.setTextColor(getResources().getColor(R.color.quantum_black_100));
                    difficultyFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_grey_border));
                    reset_games();
                    grabGameInfo();
                }
            } else if(filterType.getText().toString().equals("Size")){
                sizePos = spinnerList.getSelectedItemPosition();
                if(!spinnerList.getSelectedItem().toString().equals("All")){
                    sizeFilter.setTextColor(getResources().getColor(R.color.quantum_googred));
                    sizeFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_red_background));
                    reset_games();
                } else { //query all games at this court
                    sizeFilter.setTextColor(getResources().getColor(R.color.quantum_black_100));
                    sizeFilter.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_white_grey_border));
                    reset_games();
                    grabGameInfo();
                }
            }
            closePanel();
        });
    }

    /**
     * check if other filters are open when we open the default panel
     */
    private void checkDefaultPanel(){
        if(timePanel.getVisibility() == View.VISIBLE){
            closeTimePanel();
        } else if(datePanel.getVisibility() == View.VISIBLE){
            closeDatePanel();
        }
    }

    /**
     * check if other filters are open when we open the time panel
     */
    private void checkTimePanel(){
        if(gamePanel.getVisibility() == View.VISIBLE){
            closePanel();
        } else if(datePanel.getVisibility() == View.VISIBLE){
            closeDatePanel();
        }
    }

    /**
     * check if other filters are open when we open the date panel
     */
    private void checkDatePanel(){
        if(gamePanel.getVisibility() == View.VISIBLE){
            closePanel();
        } else if(timePanel.getVisibility() == View.VISIBLE){
            closeTimePanel();
        }
    }

    /**
     * resets the LocationGamesScreen activity
     */
    @SuppressLint("SetTextI18n")
    private void reset_games(){
        numGamesAtCourt = 0;
        games.clear();
        numGames.setText("NO OPEN GAMES");
    }

    /**
     * opens date panel
     */
    private void openDatePanel(){
        adapter.isClickable = false;
        addGame.setVisibility(View.GONE);
        datePanel.setVisibility(View.VISIBLE);
        datePanel.startAnimation(bottomUp);
    }

    /**
     * opens time panel
     */
    private void openTimePanel(){
        adapter.isClickable = false;
        addGame.setVisibility(View.GONE);
        timePanel.setVisibility(View.VISIBLE);
        timePanel.startAnimation(bottomUp);
    }

    /**
     * opens the default panel
     */
    private void openPanel(){
        adapter.isClickable = false;
        addGame.setVisibility(View.GONE);
        gamePanel.setVisibility(View.VISIBLE);
        gamePanel.startAnimation(bottomUp);
    }

    /**
     * closes the date panel
     */
    private void closeDatePanel(){
        adapter.isClickable = true;
        datePanel.startAnimation(bottomDown);
        datePanel.setVisibility(View.GONE);
        addGame.setVisibility(View.VISIBLE);
    }

    /**
     * closes the time panel
     */
    private void closeTimePanel(){
        adapter.isClickable = true;
        timePanel.startAnimation(bottomDown);
        timePanel.setVisibility(View.GONE);
        addGame.setVisibility(View.VISIBLE);
    }

    /**
     * closes the default panel
     */
    private void closePanel(){
        adapter.isClickable = true;
        gamePanel.startAnimation(bottomDown);
        gamePanel.setVisibility(View.GONE);
        addGame.setVisibility(View.VISIBLE);
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
     * @param id of the court/game
     * @param dateTime of the the game
     */
    public void addGame(String sport, Integer id, String dateTime){
        sport = sport.toLowerCase();

        games.add(
                new Game(
                        id,
                        Integer.parseInt(locationID),
                        courtName,
                        this.getResources().getIdentifier(sport, "drawable", this.getPackageName()),
                        currentLatitude,
                        currentLongitude,
                        dateTime
                ));

        numGamesAtCourt++;
    }

    /**
    Grabs and parses all the backend stored sports courts
     */
    private void grabGameInfo() {
        String url = getString(R.string.grab_games_by_location_id) + locationID;

        //converts game time strings into Dates
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatGameDate = new SimpleDateFormat("MMMM dd, yyyy");

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try{
                JSONArray jsonArray = response.getJSONArray("games");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject game = jsonArray.getJSONObject(i);

                    Date displayDate = formatGameDate.parse(game.getString("date"));

                    //Current date is before the game date
                    //or if the game is today, is the current time before the game time
                    //if(todayDate.compareTo(displayDate) < 0 || (todayDate.compareTo(displayDate) == 0 && todayTime.compareTo(displayTime) <= 0)){
                    String time = game.getString("time").substring(0, 5) + game.getString("time").substring(8);
                    if(time.substring(0, 1).equals("0")){
                        time = time.substring(1);
                    }

                    //String[] dateSections = game.getString("date").split(" ");

                    assert displayDate != null;
                    addGame(game.getString("sport"), game.getInt("gameId"), (displayDate.toString().substring(0, 10) + " " + time));
                }

                //Sets number of courts text box information
                if(numGamesAtCourt == 1){
                    numGames.setText(numGamesAtCourt + " game at " + courtName);
                } else if(numGamesAtCourt > 1){
                    numGames.setText(numGamesAtCourt + " games at " + courtName);
                }
            } catch (JSONException e){
               // numGames.setText("THAT DIDN'T WORK. Error :" + e.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }, CustomVolley.volleyErrorToastListener(this.getApplicationContext()));

        mQueue.add(request);
    }
}
