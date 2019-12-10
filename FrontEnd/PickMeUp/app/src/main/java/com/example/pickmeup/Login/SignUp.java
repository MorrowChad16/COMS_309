package com.example.pickmeup.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.pickmeup.R;
import com.example.pickmeup.httpServices.CustomVolley;
import com.example.pickmeup.httpServices.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.pickmeup.httpServices.CustomVolley.customStringRequest;

public class SignUp extends AppCompatActivity {

    volatile Boolean isSignUpError = false;
    volatile Boolean isPasswordError = false;

    /**
     * @param savedInstanceState grabs last saved instance of SignUp if possible
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Activity a = this;
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Button signUpConfirm = findViewById(R.id.create_account);
        final EditText firstName = findViewById(R.id.firstName);
        final EditText lastName = findViewById(R.id.lastName);
        final EditText email = findViewById(R.id.emailAddress);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password_new);
        final EditText passwordVerification = findViewById(R.id.password_new_verification);
        final EditText streetAddress = findViewById(R.id.homeAddress);
        final EditText city = findViewById(R.id.city);
        final AutoCompleteTextView state = findViewById(R.id.state);
        final EditText zipCode = findViewById(R.id.zip_code);
        final EditText phoneNumber = findViewById(R.id.phoneNumber);

        //Updates notification bar to white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            updateNotificationBar();
        }

        //get the states array
        String[] states = getResources().getStringArray(R.array.states_array);
        //creates and adds the states to autocomplete dropdown
        ArrayAdapter<String> states_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, states);
        //sets state text box to have autocomplete dropdown
        state.setAdapter(states_adapter);

        //Sign up button is pressed, so add the data to the SQL database
        signUpConfirm.setOnClickListener(view -> {
            //Trims all the whitespace when clicking the sign up page to ensure accurate info
            String firstName_s =  firstName.getText().toString().trim();
            String lastName_s =  lastName.getText().toString().trim();
            String email_s =  email.getText().toString().trim().toLowerCase();
            String username_s =  username.getText().toString().trim().toLowerCase();
            String password_s =  password.getText().toString().trim();
            //String passwordVerification_s =  passwordVerification.getText().toString().trim();
            String streetAddress_s =  streetAddress.getText().toString().trim();
            String city_s =  city.getText().toString().trim();
            String state_s =  state.getText().toString().trim();
            String zipCode_s =  zipCode.getText().toString().trim();
            String phoneNumber_s = phoneNumber.getText().toString().trim();

            //Reset sign up error boolean to re-validate any errors
            isSignUpError = false;

            //Handles blank first name
            if(firstName_s.length() == 0){
                firstName.setError("Please enter your first name");
                isSignUpError = true;
            }

            //Handles blank last name
            if(lastName_s.length() == 0){
                lastName.setError("Please enter your last name");
                isSignUpError = true;
            }

            //Handles email errors
            //validate email??
            if(email_s.length() == 0){
                email.setError("Please enter your email address");
                isSignUpError = true;
            } else if(!email_s.contains("@")){
                email.setError("Invalid email address");
                isSignUpError = true;
            }
            //invalid email service provider

            //Handles username errors
            if(username_s.length() <= 5) {
                username.setError("Username must be greater than 5");
                isSignUpError = true;
            }
            else if(profanityFilter(username_s) == 1){
                username.setError("Username contains profanity");
                isSignUpError = true;
            }


            //Handles address issues
            if(streetAddress_s.isEmpty()){
                streetAddress.setError("Please enter your streetAddress");
                isSignUpError = true;
            }
            if(city_s.isEmpty()){
                city.setError("Please enter your city");
                isSignUpError = true;
            }
            if(state_s.isEmpty()){
                state.setError("Please enter your state");
                isSignUpError = true;
            }
            if(zipCode_s.isEmpty()){
                zipCode.setError("Please enter your zip code");
                isSignUpError = true;
            } else if(!zipCode_s.matches("^[0-9]+$")){
                zipCode.setError("Phone number contains non-numerical values");
                isSignUpError = true;
            }
            //Handle invalid addresses

            //Handle blank numbers
            //validate phone number??
            if(phoneNumber_s.length() != 10){
                phoneNumber.setError("Please enter your phone number");
                isSignUpError = true;
            } else if(!phoneNumber_s.matches("^[0-9]+$")){
                phoneNumber.setError("Phone number contains non-numerical values");
                isSignUpError = true;
            }

            JSONObject newUser = null;
            try {
                newUser = new JSONObject();//must follow table format
                newUser.put("username", username_s);
                newUser.put("password", password_s);
                newUser.put("email", email_s);
                newUser.put("firstname", firstName_s);
                newUser.put("lastname", lastName_s);
                newUser.put("phoneNumber", phoneNumber_s);
                newUser.put("streetAddress", streetAddress_s);
                newUser.put("state", state_s);
                newUser.put("city", city_s);
                newUser.put("zipcode", zipCode_s);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //No Errors? then continue to home page
            if(!isSignUpError && !isPasswordError){
                createUser(newUser);
            }

        });

        //if the password text box gains focus and loses it again, then we will check for errors.
        password.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                String password_s = password.getText().toString();

                //Password needs to be greater than 8
                if(password_s.trim().length() < 8){
                    password.setError("Password must be 8 characters");
                    isPasswordError = true;
                    //Password
                } else if(!password_s.matches("[a-zA-Z0-9!]{8,}")) {
                    password.setError("Password needs a valid symbol");
                    isPasswordError = true;
                } else {
                    isPasswordError = false;
                }
            }
        });

        username.setOnFocusChangeListener((view, b) -> {

            if(username.getText().toString().isEmpty()){
                return;
            }
            String url = SignUp.this.getString(R.string.find_by_username) + "/" + username.getText().toString().trim();


            //implement response handling in callback
            VolleyCallback callback = new VolleyCallback(){ //implement volleyCallback interface


                @Override
                public void onSuccessResponse(String response){
                    //do nothing
                }  // end onSuccessResponse

                @Override
                public void onSuccessResponse(int intResponse, String subString) {
                    switch(intResponse){
                        case 0:
                            isSignUpError = false;
                            break;
                        case 1:
                            username.setError("This username is taken");
                            isSignUpError = true;
                            break;
                        default:
                            Log.e("SignUp","Volley intResponse " + intResponse);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error, String message) {
                    //errors automatically logged in customVolley, use if needed
                }
            };

            //create response with Context context, http method, url, JSONObject obj, VolleyCallback callback
            customStringRequest(SignUp.this.getApplicationContext(), Request.Method.GET, url, null, callback); //use JSONObject and custom VolleyCallback interface object


        });

        email.setOnFocusChangeListener((view, b) -> {

            if(email.getText().toString().isEmpty()){
                return;
            }
            String url = SignUp.this.getString(R.string.find_by_email) + "/" +  email.getText().toString().trim();


            //implement response handling in callback
            VolleyCallback callback = new VolleyCallback(){ //implement volleyCallback interface


                @Override
                public void onSuccessResponse(String response){
                    //do nothing, we only need the intResponse which is parsed automatically in customVolley and returned in other response method
                }  // end onSuccessResponse

                @Override
                public void onSuccessResponse(int intResponse, String subString) {
                    switch(intResponse){
                        case 0:
                            isSignUpError = false;
                            break;
                        case 1:
                            email.setError("This email is taken");
                            isSignUpError = true;
                            break;
                        default:
                            Log.e("SignUp","Volley intResponse " + intResponse);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error, String message) {
                        //errors automatically logged in customVolley, use if needed
                }
            };

            //create response with Context context, http method, url, JSONObject obj, VolleyCallback callback
            customStringRequest(SignUp.this.getApplicationContext(), Request.Method.GET, url, null, callback); //use JSONObject and custom VolleyCallback interface object

        });

        passwordVerification.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                if(passwordVerification.getText().toString().trim().isEmpty()){
                    passwordVerification.setError("Not equal to password");
                    isPasswordError = true;
                } else if(!passwordVerification.getText().toString().trim().equals(password.getText().toString())){
                    passwordVerification.setError("Not equal to password");
                    isPasswordError = true;
                } else {
                    isPasswordError = false;
                }
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
     * @param jsonObject is a json object containing all of the new user info to pass to the backend
     */
    private void createUser(JSONObject jsonObject) {
        try{
            String url = getString(R.string.create_user) ;

            //implement response handling in callback
            VolleyCallback callback = new VolleyCallback(){ //implement volleyCallback interface
              @Override
                public void onSuccessResponse(String response){
                    //do nothing, we only need the intResponse which is parsed automatically in customVolley and returned in other response method
                }  // end onSuccessResponse

                @Override
                public void onSuccessResponse(int intResponse, String subString) {
                    Toast errorToast;
                    switch(intResponse){
                        case 0:
                            errorToast = Toast.makeText(SignUp.this, "Username/email exists already", Toast.LENGTH_SHORT);
                            errorToast.show();
                            break;
                        case 1:
                            errorToast = Toast.makeText(SignUp.this, "Welcome!", Toast.LENGTH_SHORT);
                            errorToast.show();
                            startActivity(new Intent(SignUp.this, LoginActivity.class));
                            finish();
                            break;
                        default:
                            errorToast = Toast.makeText(SignUp.this, "An error occurred", Toast.LENGTH_SHORT);
                            errorToast.show();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error, String message) {

                }
            };

            //create response with Context context, http method, url, JSONObject obj, VolleyCallback callback
            CustomVolley.customJSONObjectRequest(this.getApplicationContext(), Request.Method.POST, url, jsonObject, callback); //use JSONObject and custom VolleyCallback interface object


        } catch(Exception a){
            Log.e("json", "Error" + a.toString());
        }
    }//end createUser

    /**
     * @param username is the string text we want to check for profanity
     * @return true if there is profanity in the username, otherwise return false
     */
    public static int profanityFilter(String username){
        String tempname = username;
        String[] naughtywords = {"fuck", "shit", "ass", "bitches"};
        //remove leetspeak
        tempname = tempname.replaceAll("1","i");
        tempname = tempname.replaceAll("!","i");
        tempname = tempname.replaceAll("3","e");
        tempname = tempname.replaceAll("4","a");
        tempname = tempname.replaceAll("@","a");
        tempname = tempname.replaceAll("5","s");
        tempname = tempname.replaceAll("7","t");
        tempname = tempname.replaceAll("0","o");
        tempname = tempname.replaceAll("9","g");
        tempname = tempname.toLowerCase().replaceAll("[^a-zA-Z]", "");

        for (String naughtyword : naughtywords) {
            if (tempname.equals(naughtyword)) {
                return 1;
            }
        }
        return 0;
    }
}
