package com.example.pickmeup.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.pickmeup.BottomHomeScreen;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Data.model.LoggedInUserView;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.LoginViewModel.LoginViewModel;
import com.example.pickmeup.ViewModel.LoginViewModel.LoginViewModelFactory;
import com.example.pickmeup.ViewModel.MutableGenericResult;


public class SplashScreen extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    public LoggedInUserView loggedInUserView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.hide();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loggedInUserView = null;
        //calls handleLoginResult when loginResult changes
        loginViewModel.getLoginResult().observe(this, loginResult ->
                        new Handler().postDelayed(this::handleLoginResult, 3000)
                );

        loginViewModel.autoLogin(SplashScreen.this.getApplicationContext());//try to autConnect firstad

        //Sets animation for images to rotate 360 degress infinitely
        //Used for login page to create spinning basketball
        //Finds the basketball image from the splash screen and starts the 360 animation on it
        ImageView image = findViewById(R.id.loadingScreenBasketball);
        image.animate().rotation(2160f).setDuration(3000);

    }

    /**
     * this method handles loginResult, called by a change in LoginResult or during testing.
     */
    public void handleLoginResult(){
        MutableGenericResult loginResult  = loginViewModel.getLoginResult().getValue();

        if (loginResult == null) {
            return;
        }
        if (loginResult.getSuccess() != null ) {
            updateUiWithUser((LoggedInUserView) loginResult.getSuccess());
            //Complete and destroy splashscreen activity once successful
            Intent nextAct = new Intent(SplashScreen.this, BottomHomeScreen.class);
            startActivity(nextAct);
            finish();
        }
        else {
            Intent nextAct = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(nextAct);
            finish();
        }
        setResult(Activity.RESULT_OK);
    }

    /**
     * @param model grab loggedinuser to display welcome message
     */
    private void updateUiWithUser(LoggedInUserView model) {
        loggedInUserView = model;
        String welcome = getString(R.string.welcome) + loggedInUserView.getDisplayName() +"!";
        //initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    /**
     * @return instance of logged in user
     */
    public LoggedInUser getUser(){
        return loginViewModel.getLoggedInUser();
    }
}
