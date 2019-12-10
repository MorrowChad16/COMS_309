package com.example.pickmeup.Data;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.httpServices.VolleyCallback;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.example.pickmeup.httpServices.CustomVolley.customJSONObjectRequest;
import static com.example.pickmeup.httpServices.CustomVolley.customStringRequest;


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class UserDataSource {

    private UserRepository userRepository;
    private static volatile UserDataSource instance;


    // private constructor : singleton access
    private UserDataSource() {
    }

    public static UserDataSource getInstance() {
        if(instance == null){
            instance = new UserDataSource();
        }
        return instance;
    }


    protected Result<LoggedInUser> login(String username, String password, Context context, UserRepository userRepository) {
        this.userRepository  = userRepository;
        try {


            String admin_username = context.getString(R.string.Admin_UserName);
            String admin_password = context.getString(R.string.Admin_Password);

            if(username.trim().equals(admin_username) && password.trim().equals(admin_password)) {
                LoggedInUser thisUser =
                        new LoggedInUser(
                                0,
                                username);

                return new Result.Success<>(thisUser);
            }
           loginUserHelper(username, password, context);
            return new Result.FailedError(1);

        } catch (Exception e) {
            Log.e("UserDatSource", e.toString());
            return new Result.ExceptionError(new IOException("Error logging in", e));
        }
    }

    public void logout() {

    }

    private void loginUserHelper(String username, String password, final Context context) throws JSONException, Exception  {

        String url = context.getString(R.string.validate_user) ;
            JSONObject user = new JSONObject();//must follow table format
            user.put("username", username);//can actually be email, but will search for username first in server
            user.put("password", password);

            //implement response handling in callback
            VolleyCallback callback = new VolleyCallback(){ //implement volleyCallback interface

                @Override
                public void onSuccessResponse(int intResponse, String jsonString) {
                    returnLoginResult(intResponse, jsonString);
                }

                @Override
                public void onErrorResponse(VolleyError error, String message) {
                    returnLoginResult(-2, null);
                }
            };

            //create response with Context context, http method, url, JSONObject obj, VolleyCallback callback
            customJSONObjectRequest(context, Request.Method.POST, url, user, callback); //use JSONObject and custom VolleyCallback interface object


    }


    public void updateUser(Context context, LoggedInUser updatedUser){
        String url = context.getString(R.string.update_user) ;//context.getString(R.string.validate_user) ;

        Gson g = new Gson();
        String userString = g.toJson(updatedUser);

        //implement response handling in callback
        VolleyCallback callback = new VolleyCallback(){ //implement volleyCallback interface


            @Override
            public void onSuccessResponse(String response){
                //do nothing
            }  // end onSuccessResponse

            @Override
            public void onSuccessResponse(int intResponse, String subString) {
                returnResult(intResponse, subString);
            }

            @Override
            public void onErrorResponse(VolleyError error, String message) {
               returnResult(-2, null);
            }
        };

        //create response with Context context, http method, url, JSONObject obj, VolleyCallback callback
        customStringRequest(context, Request.Method.POST, url, userString, callback); //use JSONObject and custom VolleyCallback interface object

    }

    private void returnResult(int intResponse,String subString){
        if(intResponse == 1) {
            userRepository.setUpdateResult(new Result.Success<>(intResponse));
        }else {
            userRepository.setUpdateResult(new Result.FailedError(intResponse));
        }
    }

    private void returnLoginResult(int intResponse,String subString){
        if(intResponse == 1) {
            Gson g = new Gson();
            LoggedInUser thisUser = g.fromJson(subString,LoggedInUser.class); //subString is jsonObj
            userRepository.setLoginResult(new Result.Success<>(thisUser));
        }else {
            userRepository.setLoginResult(new Result.FailedError(intResponse));
        }
    }

}
