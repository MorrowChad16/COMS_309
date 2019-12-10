package com.example.pickmeup.Account.Password;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.example.pickmeup.httpServices.RequestController;
import com.example.pickmeup.httpServices.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.example.pickmeup.httpServices.CustomVolley.customJSONObjectRequest;

public class ChangePasswordActivity extends AppCompatActivity {

    LoggedInUser loggedInUser;
    private EditText oldPassword;

    private RequestController mRequest;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //call from any fragment in bottomHomeScreen
        //HomeViewModel instantiated in bottomHomeScreen
        HomeViewModel homeViewModel =
                ViewModelProviders.of(Objects.requireNonNull(this), new HomeViewModelFactory()).get(HomeViewModel.class);
        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login

        Button submitChanges = findViewById(R.id.submit_password_change);
        oldPassword = findViewById(R.id.old_password_verify);
        EditText newPassword = findViewById(R.id.new_password);
        EditText newPasswordVerify = findViewById(R.id.new_password_verify);

        //if the password text box gains focus and loses it again, then we will check for errors.
        oldPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                try {
                    validateUser(loggedInUser.getUsername(), oldPassword.getText().toString(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //if the password text box gains focus and loses it again, then we will check for errors.
        newPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                String newPass = newPassword.getText().toString();

                //Password needs to be greater than 8
                if(newPass.trim().length() < 8){
                    newPassword.setError("Password must be 8 characters");
                } else if(!newPass.matches("[a-zA-Z0-9!]{8,}")) {
                    newPassword.setError("Password contains an invalid symbol");
                }
            }
        });

        //if the password text box gains focus and loses it again, then we will check for errors.
        newPasswordVerify.setOnFocusChangeListener((view, hasFocus) -> {
            if(!hasFocus){
                String newPassVer = newPasswordVerify.getText().toString();

                //Password doesn't equal other new password
                if(!newPassVer.equals(newPassword.getText().toString())){
                    newPasswordVerify.setError("Passwords do not match");
                }
            }
        });

        /*
        Person clicked the submit changes button so check all the info is correct and submit it to the backend
         */
        submitChanges.setOnClickListener(view -> {
            //no errors for the new passwords, so submit the changes
            if(oldPassword.getError() == null &&  newPassword.getError() == null && newPasswordVerify.getError() == null){
                updateUserPassword(loggedInUser.getId(), oldPassword.getText().toString(), newPassword.getText().toString());
            }
        });

        /*
        Person clicked outside the current text box, so reduce the keyboard
         */
        RelativeLayout touchInterceptor = this.findViewById(R.id.change_password_screen);
        touchInterceptor.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect outRect = new Rect();
                if (oldPassword.isFocused()) {
                    oldPassword.clearFocus();
                    oldPassword.requestFocus();
                    oldPassword.getGlobalVisibleRect(outRect);
                } else if (newPassword.isFocused()) {
                    newPassword.clearFocus();
                    newPassword.requestFocus();
                    newPassword.getGlobalVisibleRect(outRect);

                } else if (newPasswordVerify.isFocused()) {
                    newPasswordVerify.clearFocus();
                    newPasswordVerify.requestFocus();
                    newPasswordVerify.getGlobalVisibleRect(outRect);
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
     * @param username of logged in user
     * @param password of logged in user
     * @param context of activity
     * @throws Exception
     */
    private void validateUser(String username, String password, final Context context) throws Exception  {

        String url = context.getString(R.string.validate_user) ;
        JSONObject user = new JSONObject();//must follow table format
        user.put("username", username);//can actually be email, but will search for username first in server
        user.put("password", password);

        //implement response handling in callback
        VolleyCallback callback = new VolleyCallback(){ //implement volleyCallback interface

            @Override
            public void onSuccessResponse(int intResponse, String jsonString) {
                if(intResponse == 2){
                    oldPassword.setError("Wrong current password");
                }
            }

            @Override
            public void onErrorResponse(VolleyError error, String message) {

            }
        };

        //create response with Context context, http method, url, JSONObject obj, VolleyCallback callback
        customJSONObjectRequest(context, Request.Method.POST, url, user, callback); //use JSONObject and custom VolleyCallback interface object
    }

    /**
     * @param userId is the logged in user id
     * @param oldPassword is the logged in users old password
     * @param newPassword is the logged in users given new address
     */
    private void updateUserPassword(int userId, String oldPassword, String newPassword) {
            if (mRequest == null) {
                mRequest = RequestController.getInstance(this.getApplicationContext());
            }

            String url = getString(R.string.update_user_password);

            JSONObject userInfo = new JSONObject();//must follow table format
            try {
                userInfo.put("userId", userId);//can actually be email, but will search for username first in server
                userInfo.put("oldPassword", oldPassword);
                userInfo.put("newPassword", newPassword);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //implement response handling in callback
            VolleyCallback callback = new VolleyCallback() { //implement volleyCallback interface

                @Override
                public void onSuccessResponse(int intResponse, String jsonString) {
                    Toast reponse;
                    if (intResponse == 1) {
                        reponse = Toast.makeText(ChangePasswordActivity.this, "Changed Password!", Toast.LENGTH_SHORT);
                        reponse.show();
                        onBackPressed();
                    } else if(intResponse == 0){
                        reponse = Toast.makeText(ChangePasswordActivity.this, "Wrong Old Password!", Toast.LENGTH_SHORT);
                        reponse.show();
                    } else {
                        reponse = Toast.makeText(ChangePasswordActivity.this, "Error Occurred", Toast.LENGTH_SHORT);
                        reponse.show();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error, String message) {
                    Log.d("status", error.toString());
                }
            };

            //create response with Context context, http method, url, JSONObject obj, VolleyCallback callback
            customJSONObjectRequest(this, Request.Method.POST, url, userInfo, callback); //use JSONObject and custom VolleyCallback interface object
    }
}
