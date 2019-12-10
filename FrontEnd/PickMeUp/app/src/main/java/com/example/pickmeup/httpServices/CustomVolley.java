package com.example.pickmeup.httpServices;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides static methods for creating custom Volley Requests.
 */
public class CustomVolley {


    /**
     * This method takes a JSONObject as an argument to then call customStringRequest.
     * @param context Android app context
     * @param method   HTTP method type GET, POST,PUT, DELETE
     * @param url       String URL to connect to
     * @param jsonObject a JSONObject to send
     * @param callback  VolleyCallback interface object with override
     */
    public static void customJSONObjectRequest(final Context context, int method, String url, JSONObject jsonObject, final VolleyCallback callback){
              String jsonObj = jsonObject.toString();
            customStringRequest(context, method, url, jsonObj, callback);
    }

    /**
     * This custom Volley method uses Volley's StringRequest class and implements all methods in local VolleyCallback. This method uses context object to add request
     * to the RequestController's RequestQueue. And pushes callbacks on the UI Thread.
     * @param context Android app context
     * @param method   Volley's HTTP method type GET, POST, PUT, DELETE
     * @param url       URL to connect to
     * @param message a message to send
     * @param callback  VolleyCallback object with overridden or implemented methods
     */
    public static void customStringRequest(final Context context, int method, String url, final String message, final VolleyCallback callback){// throws Exception{

        RequestController  mQueue = RequestController.getInstance(context);
        Handler mHandler = new Handler();


        // end onResponse
        StringRequest request = new StringRequest(method, url, response -> {

            Runnable r = () -> {
                callback.onSuccessResponse(response);


                //callback for intResponse
                String jsonString = null;
                int intResponse;
                try{
                    Matcher matcher = Pattern.compile("\\d+").matcher(response);
                    matcher.find();
                    intResponse = Integer.parseInt(matcher.group());
                    if(response.length() > 2 && response.trim().contains(" ")) {//check if length is greater than 2 and trim whitespace at begin/end of string to check if there's a space
                        String[] respArr = response.split(" ", 2); //split string to  get json obj or extra substring
                        jsonString = respArr[1]; //
                    }


                } catch(Exception e){
                    Log.e("CustomVolley", e.toString());
                    intResponse = -3;
                }

                callback.onSuccessResponse(intResponse,  jsonString);

            };

            mHandler.post(r); //run on main thread

        }, error -> {
            String message1 = "";
            if (error instanceof NetworkError) {
                message1 = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ServerError) {
                message1 = "The server could not be found. Please try again after some time!!";
            } else if (error instanceof AuthFailureError) {
                message1 = "Cannot connect to server!";
            } else if (error instanceof ParseError) {
                message1 = "Parsing error! Please try again after some time!!";
            } else if (error instanceof TimeoutError) {
                message1 = "Connection TimeOut! Please check your internet connection.";
            }
            Log.e("customVolley",error.toString());
            final String finalMsg = message1;

            Runnable e = () -> callback.onErrorResponse(error, finalMsg);
            mHandler.post(e); //run on mainThread;

        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public byte[] getBody() {
                return message.getBytes(StandardCharsets.UTF_8);
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

    }

    public static Response.ErrorListener volleyErrorToastListener(Context context){
        return volleyError -> {
            String message = null;
            if (volleyError instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!!";
            } else if (volleyError instanceof AuthFailureError) {
                message = "Cannot connect to server!";
            } else if (volleyError instanceof ParseError) {
                message = "Parsing error! Please try again after some time!!";
            } else if (volleyError instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            }
            Log.d("VolleyError", message + ": VolleyError:" + volleyError.toString());

            Toast.makeText(context, "Error: Volley", Toast.LENGTH_SHORT).show();

        };
    }

    public static Response.ErrorListener volleyErrorListener = volleyError -> {
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to server!";
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        Log.d("VolleyError", message + ": VolleyError:" + volleyError.toString());

    };

}
