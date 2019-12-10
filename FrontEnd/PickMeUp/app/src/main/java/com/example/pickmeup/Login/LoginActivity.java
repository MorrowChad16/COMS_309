package com.example.pickmeup.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.pickmeup.BottomHomeScreen;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Data.model.LoggedInUserView;
import com.example.pickmeup.Login.GuestScreen.GuestScreen;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.LoginViewModel.LoginViewModel;
import com.example.pickmeup.ViewModel.LoginViewModel.LoginViewModelFactory;
import com.example.pickmeup.ViewModel.MutableGenericResult;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ProgressBar loadingProgressBar;
    private EditText usernameEditText;
    private EditText passwordEditText;
    public LoggedInUserView loggedInUserView;
    private Button noInternet;
    private ImageView warning;


    @SuppressLint("CutPasteId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password_new);
        this.usernameEditText = findViewById(R.id.username);
        this.passwordEditText = findViewById(R.id.password_new);
        final Button loginButton = findViewById(R.id.login);
        final Button signupButton = findViewById(R.id.sign_up);
        loadingProgressBar = findViewById(R.id.loading);
        TextView guestText = findViewById(R.id.guestText);
        noInternet = findViewById(R.id.noInternet);
        warning = findViewById(R.id.warning);

        guestText.setPaintFlags(guestText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        loggedInUserView = null;


        //Changes notification bar to white
        updateNotificationBar();

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        //calls handleLoginResult when loginResult changes
        loginViewModel.getLoginResult().observe(this, loginResult -> handleLoginResult());

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {//USER LOGIN calls string request then uses response to determine what to do next

                //USER LOGIN calls string request then uses response to determine what to do next
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), LoginActivity.this.getApplicationContext());

            }
            return false;


        });

        loginButton.setOnClickListener(v -> {
            //USER LOGIN calls string request then uses response to determine what to do next
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(), LoginActivity.this.getApplicationContext());
        });

        //If the sign up button is clicked then go to the sign up page
        signupButton.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignUp.class)));

        guestText.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, GuestScreen.class)));

           loginViewModel.autoLogin(LoginActivity.this.getApplicationContext());//try to autConnect

    }

    /**
     * this method handles loginResult, called by a change in LoginResult or during testing.
     */
    public void handleLoginResult(){
        MutableGenericResult loginResult  = loginViewModel.getLoginResult().getValue();

        if (loginResult == null) {
            return;
        }
        if (loginResult.getFailedError() != null) {
            handleFailedLoginUser(loginResult.getFailedError());
        }
        else if (loginResult.getExceptionError() != null) {
            loadingProgressBar.setVisibility(View.GONE);
            handleException(loginResult.getExceptionError());
        }
        else if (loginResult.getSuccess() != null ) {
            loadingProgressBar.setVisibility(View.GONE);
            updateUiWithUser((LoggedInUserView) loginResult.getSuccess());
            //Complete and destroy login activity once successful
            Intent nextAct = new Intent(LoginActivity.this, BottomHomeScreen.class);
            startActivity(nextAct);
            finish();
        }

        setResult(Activity.RESULT_OK);
    }

    /**
    Changes the notification bar to red
    */
    private void updateNotificationBar(){
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.hide();
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

    private void updateUiWithUser(LoggedInUserView model) {
        loggedInUserView = model;
        String welcome = getString(R.string.welcome) + loggedInUserView.getDisplayName() +" !";
        //initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

     public LoggedInUser getUser(){
        return loginViewModel.getLoggedInUser();
     }

    @Override
    public void onBackPressed(){
        //Disables the back button, so the user can't go back to the splash screen
    }

    private void handleException(Exception loginError){
        loadingProgressBar.setVisibility(View.GONE);
        Log.e("LoginFailed",loginError.toString() );
        Toast.makeText(getApplicationContext(), loginError.toString(), Toast.LENGTH_SHORT).show(); //replace with getString(R.string.login_failed)
    }

    /**
     * @param intResponse response of attempted login
     * handles if the auto login or manual login fails to notify the user
     */
    private void handleFailedLoginUser(@NotNull Integer intResponse){

        if(intResponse != 1){
            loadingProgressBar.setVisibility(View.GONE);
        }
            switch(intResponse){
                case -7:
                    Toast errorToastAuto = Toast.makeText(LoginActivity.this, "Auto-Login Failed", Toast.LENGTH_SHORT);
                    errorToastAuto.show();
                    break;
                case -2:
                    noInternet.setVisibility(View.VISIBLE);
                    warning.setVisibility(View.VISIBLE);
                    Toast errorToastEN = Toast.makeText(LoginActivity.this, "Check your Internet Connection", Toast.LENGTH_LONG);
                    errorToastEN.show();
                    break;
                case -1:
                    Toast errorToastN = Toast.makeText(LoginActivity.this, "callback didn't work", Toast.LENGTH_LONG);
                    errorToastN.show();
                    break;

                case 0:
                    Toast errorToastU = Toast.makeText(LoginActivity.this, "Username incorrect/not found", Toast.LENGTH_LONG);
                    errorToastU.show();

                    usernameEditText.setText("");
                    passwordEditText.setText("");
                    break;

                case 1:
                    Log.e("volley", "waiting for volley response or auto login exited" );
                    break;

                case 2:
                    Toast errorToastP = Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_LONG);
                    errorToastP.show();

                    passwordEditText.setText("");
                    break;

                default:
                    Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_LONG).show();

            }
    }

}
