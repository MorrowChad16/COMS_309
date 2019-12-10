package com.example.pickmeup.Account.Admin;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.Data.MsgCallBack;
import com.example.pickmeup.R;
import com.example.pickmeup.CustomObjects.SwipeToDeleteCourt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {

    private List<AdminCourt> adminCourtsNew;
    private RecyclerView newRecyclerView;
    private AdminCourtAdapter newAdapter;

    private List<AdminCourt> adminCourtsOld;
    private RecyclerView oldRecyclerView;
    private AdminCourtAdapter oldAdapter;
    private AdminCourtDataSource src;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initNewCourtsRecycler();
        initOldCourtsRecycler();
        initSocket();

        handler = new Handler();
    }

    /**
     * initializes all the info we need for the admin new courts recycler view
     */
    public void initNewCourtsRecycler(){
        //Recycler View set up for un-verified games
        adminCourtsNew = new ArrayList<>();
        newRecyclerView = findViewById(R.id.admin_confirm_new_court_recycler);
        newRecyclerView.setHasFixedSize(true);
        newRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newAdapter = new AdminCourtAdapter(this, adminCourtsNew);
        newRecyclerView.setAdapter(newAdapter);
        ItemTouchHelper newItemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCourt(newAdapter, this));
        newItemTouchHelper.attachToRecyclerView(newRecyclerView);
    }

    /**
     * initializes all the info we need for the admin old courts recycler view
     */
    public void initOldCourtsRecycler(){
        //Recycler View set up for un-verified games
        adminCourtsOld = new ArrayList<>();
        oldRecyclerView = findViewById(R.id.admin_update_old_court_recycler);
        oldRecyclerView.setHasFixedSize(true);
        oldRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        oldAdapter = new AdminCourtAdapter(this, adminCourtsOld);
        oldRecyclerView.setAdapter(oldAdapter);
        ItemTouchHelper oldItemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCourt(oldAdapter, this));
        oldItemTouchHelper.attachToRecyclerView(newRecyclerView);
    }

    /**
     * initializes the web socket when communicates court info to the front end
     */
    public void initSocket(){
        int loggedInUserId = 6;
        String url = getString(R.string.grab_admin_court_socket) + loggedInUserId;
       src = AdminCourtDataSource.getInstance();

        EditText search = findViewById(R.id.search_bar_admin);
        ImageButton img = findViewById(R.id.search_bar_image_admin);
        View.OnClickListener onClickListener = v -> {
            try {
                Log.d("onClickInitScoket", "clicked");
                if(!src.isConnected()){
                    src.send(search.getText().toString());
                }else{
                    src.connect(url);
                }
            } catch (Exception e) {
                Log.d("onClickInitScoket", e.toString());
            }
        };
        img.setOnClickListener(onClickListener);

        src.setCourtCallback(message -> {
            String tempLongt = "" , tempLat = "", tempName = "", id = "", info = "";

            String[] jsonObjects = message.split("\\}");
            for (String jsonObject : jsonObjects) {
                if(jsonObject.contains("longt") && jsonObject.contains("lat") && jsonObject.contains("name")
                        && jsonObject.contains("id") && jsonObject.contains("info")){
                    String[] individualItems = jsonObject.split(",");
                    for (String individualItem : individualItems) {
                        if (individualItem.contains("longt")) {
                            String[] tempSplit = individualItem.split(":");
                            tempLongt = tempSplit[1];
                        } else if (individualItem.contains("lat")) {
                            String[] tempSplit = individualItem.split(":");
                            tempLat = tempSplit[1];
                        } else if (individualItem.contains("name")) {
                            String[] tempSplit = individualItem.split(":");
                            tempName = tempSplit[1].substring(1, tempSplit[1].length() - 1);
                        } else if(individualItem.contains("id")){
                            String[] tempSplit = individualItem.split(":");
                            id = tempSplit[1];
                        } else if(individualItem.contains("info")){
                            String[] tempSplit = individualItem.split(":");
                            info = tempSplit[1].substring(1, tempSplit[1].length() - 1);
                        }
                    }
                    grabAdminInfo(tempName, Double.parseDouble(tempLat), Double.parseDouble(tempLongt), id, info);
                }
            }
            Runnable runnable1 = () -> newAdapter.notifyDataSetChanged();
            handler.post(runnable1);


        }); //end callback

        if(!src.isConnected()) {
            src.setOnOpenCallback(message -> src.all());
            src.connect(url);
        }
        else{
            src.all();
        }

        MsgCallBack errorCallBack = message -> {
            @SuppressLint("ShowToast") Toast toast = Toast.makeText(AdminActivity.this,message, Toast.LENGTH_LONG);
        };
        src.setErrorCallback(errorCallBack); //sets errorCallback which will be called in socket OnError

    }



    @Override
    protected void onStop() {
        src.setErrorCallback(null);
        src.setCourtCallback(null);
        src.setOnOpenCallback(null);
        super.onStop();
    }

    private void grabAdminInfo(String courtName, double lat, double longt, String id, String info){
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(lat, longt, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert addresses != null;
        if(addresses.size() > 0){
            String[] addressSegments = addresses.get(0).getAddressLine(0).split(", ");
            //Street, city, state initials
            adminCourtsNew.add(
                    new AdminCourt(
                            courtName,
                            addressSegments[1] + ", " + addressSegments[2] + ", " + addressSegments[3].substring(0, 3),
                            id,
                            info,
                            Double.toString(longt),
                            Double.toString(lat)
                    )
            );
        }
    }
}
