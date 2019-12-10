package com.example.pickmeup.Home.CourtGames;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
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

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddNewGameScreen extends AppCompatActivity {

    private EditText gameDate;
    private EditText gameTime;
    private String tempGameDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private RequestController mQueue;

    private int locationId;
    private String gameTime24;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> sizeAdapter;

    Handler hand;

    /**
     * @param savedInstanceState saved activity state for recreation
     */
    @SuppressLint({"SimpleDateFormat", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_game_screen);
        hand = new Handler();

        String courtName = getIntent().getStringExtra("courtName");
        locationId = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));

        final String[] sports = getResources().getStringArray(R.array.sports_list);
        AutoCompleteTextView sport = findViewById(R.id.sport_of_new_game);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sports);
        sport.setAdapter(adapter);

        sport.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                if(sport.getText().toString().length() == 0){
                    sport.setError("Please enter a sport");
                } else if(!contains(sports, sport.getText().toString())){
                    sport.setError("Sport not supported");
                } else {
                    sport.setError(null);
                }
            } else {
                sport.showDropDown();
            }
        });

        sport.setOnClickListener(view -> sport.showDropDown());

        final String[] teamSizes = getResources().getStringArray(R.array.team_sizes);
        AutoCompleteTextView teamSize = findViewById(R.id.size_of_new_game);
        sizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, teamSizes);
        teamSize.setAdapter(sizeAdapter);
        teamSize.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                if(teamSize.getText().toString().length() == 0){
                    teamSize.setError("Please enter a match up size");
                } else if(!contains(teamSizes, teamSize.getText().toString())){
                    teamSize.setError("Size not supported");
                } else {
                    teamSize.setError(null);
                }
            } else {
                teamSize.showDropDown();
            }
        });

        teamSize.setOnClickListener(view -> teamSize.showDropDown());

        EditText court = findViewById(R.id.game_location);
        court.setFocusable(false);
        court.setText(courtName);

        Button submitNewGame = findViewById(R.id.submit_new_game);
        gameDate = findViewById(R.id.game_date);
        gameTime = findViewById(R.id.game_time);

        gameDate.setOnFocusChangeListener((view, hasFocus) -> {
           if(!hasFocus){
               if(gameDate.getText().toString().length() == 0){
                   gameDate.setError("Please enter a date");
               }
           } else {
               showGameDate();
           }
        });

        gameDate.setOnClickListener(view -> showGameDate());

        gameTime.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                if(gameTime.getText().toString().length() == 0){
                    gameTime.setError("Please enter a time");
                }
            } else {
                showGameTime();
            }
        });

        gameTime.setOnClickListener(view -> showGameTime());

        mTimeSetListener = (timePicker, hour, minute) -> {
            gameTime24 = hour + ":" + minute + ":00";
            String selectedDate;
            if(hour > 12){
                hour -= 12;
                selectedDate = hour + ":" + minute + " PM";
            } else {
                selectedDate = hour + ":" + minute + " AM";
            }
            gameTime.setText(selectedDate);
            gameTime.setError(null);
        };

        mDateSetListener = (datePicker, year, month, day) -> {
            month++;

            String selectedDateString = "";
            switch(month){
                case 1:
                    selectedDateString += "January ";
                    break;
                case 2:
                    selectedDateString += "February ";
                    break;
                case 3:
                    selectedDateString += "March ";
                    break;
                case 4:
                    selectedDateString += "April ";
                    break;
                case 5:
                    selectedDateString += "May ";
                    break;
                case 6:
                    selectedDateString += "June ";
                    break;
                case 7:
                    selectedDateString += "July ";
                    break;
                case 8:
                    selectedDateString += "August ";
                    break;
                case 9:
                    selectedDateString += "September ";
                    break;
                case 10:
                    selectedDateString += "October ";
                    break;
                case 11:
                    selectedDateString += "November ";
                    break;
                case 12:
                    selectedDateString += "December ";
                    break;
            }
            selectedDateString += day + ", " + year;
            tempGameDate = month + "/" + (day + 1) + "/" + year;
            gameDate.setText(selectedDateString);
            gameDate.setError(null);
        };

        submitNewGame.setOnClickListener(view -> {
            if(sport.getError() == null && court.getError() == null && gameDate.getError() == null && gameTime.getError() == null) {
                JSONObject newGame = null;
                try {
                    newGame = new JSONObject();//must follow table format
                    newGame.put("gameLocationId", locationId);
                    newGame.put("sport", sport.getText().toString());
                    newGame.put("status", 0);
                    Date date1 = new SimpleDateFormat("MM/dd/yyyy").parse(tempGameDate);
                    assert date1 != null;
                    newGame.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date1));
                    newGame.put("time", gameTime24);
                    if(teamSize.getText().toString().equals("1v1")){
                        newGame.put("pMax", 2);
                    } else if(teamSize.getText().toString().equals("2v2")){
                        newGame.put("pMax", 4);
                    } else if(teamSize.getText().toString().equals("3v3")){
                        newGame.put("pMax", 6);
                    } else if(teamSize.getText().toString().equals("4v4")){
                        newGame.put("pMax", 8);
                    } else if(teamSize.getText().toString().equals("5v5")){
                        newGame.put("pMax", 10);
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                addGame(newGame.toString());
            }
        });

        RelativeLayout touchInterceptor = findViewById(R.id.add_new_game);
        touchInterceptor.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect outRect = new Rect();
                if (sport.isFocused()) {
                    sport.clearFocus();
                    sport.requestFocus();
                    sport.dismissDropDown();
                    sport.getGlobalVisibleRect(outRect);
                } else if (gameDate.isFocused()) {
                    gameDate.clearFocus();
                    gameDate.requestFocus();
                    gameDate.getGlobalVisibleRect(outRect);
                } else if (gameTime.isFocused()) {
                    gameTime.clearFocus();
                    gameTime.requestFocus();
                    gameTime.getGlobalVisibleRect(outRect);
                } else if(teamSize.isFocused()){
                    teamSize.clearFocus();
                    teamSize.requestFocus();
                    teamSize.dismissDropDown();
                    teamSize.getGlobalVisibleRect(outRect);
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
     * displays game date animation picker
     */
    private void showGameDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                AddNewGameScreen.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    /**
     * displays game time animation picker
     */
    private void showGameTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                AddNewGameScreen.this,
                mTimeSetListener,
                hour,
                minute,
                android.text.format.DateFormat.is24HourFormat(this));
        dialog.show();
    }

    /**
     * @param arr string array
     * @param val we are searching for
     * @return true if found
     */
    private Boolean contains(String[] arr, String val){
        for (String s : arr) {
            if (s.equals(val)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param jsonObject object we want to add in the backend
     */
    private void addGame(final String jsonObject) {
        try {
            if(mQueue == null){
                mQueue = RequestController.getInstance(this.getApplicationContext());
            }

            String url = getString(R.string.add_game) + locationId;

            // end onResponse
            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                Toast errorToast;

                if(response.contains("0")){
                    errorToast = Toast.makeText(AddNewGameScreen.this, "An error occurred", Toast.LENGTH_SHORT);
                    errorToast.show();
                } else if(response.contains("1")){
                    errorToast = Toast.makeText(AddNewGameScreen.this, "Added new game!", Toast.LENGTH_SHORT);
                    errorToast.show();
                    Runnable r = this::onBackPressed;
                    hand.post(r);
                } else {
                    errorToast = Toast.makeText(AddNewGameScreen.this, "An error occurred", Toast.LENGTH_SHORT);
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
