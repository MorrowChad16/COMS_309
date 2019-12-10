package com.example.pickmeup;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.pickmeup.Account.AccountFragment;
import com.example.pickmeup.CustomObjects.MovableFloatingActionButton;
import com.example.pickmeup.Home.HomeFragment;
import com.example.pickmeup.Messages.Chat.ChatScreen;
import com.example.pickmeup.Record.RecordFragment;
import com.example.pickmeup.Search.SearchFragment;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class BottomHomeScreen extends AppCompatActivity {

    MovableFloatingActionButton messages_button;

    private int currFragment = 0;
    Boolean isFirstTime = true;

    /**
     * @param savedInstanceState returns the last saved instance of BottomHomeScreen if possible
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Android initial setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_home_screen);

        messages_button = findViewById(R.id.messages_button);

        //get HomeViewModel
//        //create new HomeViewModel
        HomeViewModel homeViewModel =
                ViewModelProviders.of(Objects.requireNonNull(this), new HomeViewModelFactory()).get(HomeViewModel.class);

        //Updates notification bar to white
        updateNotificationBar();

        //Opens the bottom navigation window containing the separate tabs
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //Grabs the correct xml file when clicking through the tabs
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //sets initial fragment to open, otherwise no initial tab is open
        bottomNav.setSelectedItemId(R.id.nav_home);

        messages_button.setOnClickListener(view -> startActivity(new Intent(BottomHomeScreen.this, ChatScreen.class)));
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
     * listens for user action on the tabs, if the the user clicks on a different tab, then
     * it pulls that fragment and loads it
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        Fragment selectedFragment;

        if(currFragment != menuItem.getItemId() || isFirstTime){
            if(isFirstTime){
                isFirstTime = false;
            }
            switch (menuItem.getItemId()){
                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    currFragment = R.id.nav_search;
                    break;
                case R.id.nav_record:
                    selectedFragment = new RecordFragment();
                    currFragment = R.id.nav_record;
                    break;
                case R.id.nav_account:
                    selectedFragment = new AccountFragment();
                    currFragment = R.id.nav_account;
                    break;
                default:
                    selectedFragment = new HomeFragment();
                    currFragment = R.id.nav_home;
                    break;
            }
            //uses the selected fragment from above to open the correct tab
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }

        //tells the function to grab the fragment
        return true;
    };

    /**
     * disables back button from being pressed once logged in, user needs to log out to go back to login screen
     */
    @Override
    public void onBackPressed(){
        //Disables the back button, so the user can't go back to the splash screen
    }
}
