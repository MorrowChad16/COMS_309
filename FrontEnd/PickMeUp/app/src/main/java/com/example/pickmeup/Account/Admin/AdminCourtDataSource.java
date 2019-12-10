package com.example.pickmeup.Account.Admin;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.example.pickmeup.Data.MsgCallBack;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AdminCourtDataSource {
    private static final AdminCourtDataSource ourInstance = new AdminCourtDataSource();
    private static SourceConnection connected = SourceConnection.CLOSED;
    private WebSocketClient adminSocket;

    private String lastMessageIn;
    private MsgCallBack errorCallback;
    private MsgCallBack courtCallback;
    private MsgCallBack onOpenCallback;

    private static final String METHOD_INT_RESPONSE_PATTERN = "^([-_a-zA-Z0-9]+\\([-_ a-zA-Z0-9]*\\))" +
            "( [-0-9]+)?" + "( .*)?$"; //method(arg) int(+/-) {some json}

    static AdminCourtDataSource getInstance() {
        return ourInstance;
    }

    private AdminCourtDataSource() {
    }

    boolean isClosed(){
        return connected == SourceConnection.CLOSED;
    }

    boolean isConnected() {
        return connected == SourceConnection.CONNECTED;
    }

    @VisibleForTesting
    String getLastMessageIn(){
        return lastMessageIn;
    }

    public boolean connect(String url){
        if(connected == SourceConnection.CONNECTED | connected == SourceConnection.CONNECTING){
            return false;
        }
        Draft[] drafts = {new Draft_6455()};

        try {
            Log.d("Socket:", "Trying socket @ " + url);
            connected = SourceConnection.CONNECTING;

           adminSocket = new WebSocketClient(new URI(url), drafts[0]) {
                @Override
                public void onMessage(String message) {
                    lastMessageIn = message;
                    Log.d("", "run() returned: " + message);
                    handleMessage(message);
                }

                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d("OPEN", "run() returned: " + "is connecting");
                    connected = SourceConnection.CONNECTED;
                    onOpenCallback.onMessage(null);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("CLOSE", "onClose() returned: " + reason);
                    connected = SourceConnection.CLOSED;
                }

                @Override
                public void onError(Exception e) {
                    Log.d("Exception:", e.toString());
                    connected = SourceConnection.CLOSED;

                    if(errorCallback != null){
                        errorCallback.onMessage(e.toString());
                    }


                }
            };
        }
        catch (Exception e) {
            Log.d("Exception:", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
            return false;
        }
       adminSocket.connect();
        return true;
    }

    void setOnOpenCallback(MsgCallBack onOpenCallback){
        this.onOpenCallback = onOpenCallback;
    }
    void setErrorCallback(MsgCallBack errorCallback){
        this.errorCallback = errorCallback;
    }
    void setCourtCallback(MsgCallBack courtCallback) { this.courtCallback = courtCallback;}

    void count(){
        send("count()");
    }

    public void all(){
        send("all()");
    }

    public void send(String message){
        if(connected != SourceConnection.CONNECTED){
            throw new IllegalStateException("not connected");
        }
        adminSocket.send(message);
    }

    void close(){
        if(connected != SourceConnection.CONNECTED){
           return;
        }
       adminSocket.close();
    }

    private void handleMessage(String rawMessage){
        try{

            Pattern p = Pattern.compile(METHOD_INT_RESPONSE_PATTERN);
            Matcher m = p.matcher(rawMessage);

            if(m.find()){
                Log.d("handleMessage","using pattern " + METHOD_INT_RESPONSE_PATTERN);

                String method = m.group(1);
                String intResponse = m.group(2);
                String subString = m.group(3);
                Log.d("adminCourt_handleMsg","found " + " " + method + " " + intResponse + " " + subString);
                assert method != null;
                switch(method){
                    case "all()":
                        courtCallback.onMessage(subString.trim());
                        JSONObject obj = new JSONObject(subString.trim());
                        Log.d("adminCourt_handleMsg","jsonObj:" + obj.toString());
                        break;
                    case "count()":

                        break;

                    default: Log.d("INT JSON","entered switch case statement");
                }
            }//end find

        } catch (Exception e){
            Log.e("handleMessage", e.toString());
        }
    }

    enum SourceConnection{
        CLOSED,
        CONNECTING,
        CONNECTED
    }
}
