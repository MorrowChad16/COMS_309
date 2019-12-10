package com.example.pickmeup.ViewModel.MessageViewModel;

import android.util.Log;

import com.example.pickmeup.Data.MsgCallBack;
import com.example.pickmeup.Data.Result;
import com.example.pickmeup.Messages.Messages.Message;
import com.example.pickmeup.Messages.UserActivity.UserActivity;
import com.example.pickmeup.ViewModel.I_MessageViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
@SuppressWarnings("ALL")
public class MessageDataSource {


    private static volatile MessageDataSource instance;
    private boolean connected;
    private I_MessageViewModel model;
    private  WebSocketClient messageSocket;
    private MsgCallBack msgCallback;
    private Gson gson;

    private static List<UserActivity> userActivities = new ArrayList<>();

    public static final String ACTIVE_USER_LIST_PATTERN ="\\{\"activeUsernameList\":(.*)\\}";

    private static Map<Integer, List<Message>> chatIdtoListMap = new HashMap<Integer, List<Message>>(); //global list of message lists for all chatIds


    // private constructor : singleton access
    private MessageDataSource() {
        connected = false;
        model = null;
        messageSocket = null;
        msgCallback = null;
        gson = new GsonBuilder()
                .setDateFormat(Message.simpleDateFormatJSON)
                .create();

    }

    public static MessageDataSource getInstance() {
        if(instance == null){
            instance = new MessageDataSource();
        }
        return instance;

    }

    public void setMsgCallback(MsgCallBack msgCallback){
        this.msgCallback = msgCallback;
    }


    public void setMessageViewModel(I_MessageViewModel model){
        this.model = model;
    }

    public boolean connect(String url){
    if(connected){
      return false;
    }
        Draft[] drafts = {new Draft_6455()};

        try {
            Log.d("Socket:", "Trying socket");

            messageSocket = new WebSocketClient(new URI(url), drafts[0]) {
                @Override
                public void onMessage(String message) {
                    Log.d("", "run() returned: " + message);
                        handleMessage(message);
                }

                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d("OPEN", "run() returned: " + "is connecting");
                    connected = true;
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d("CLOSE", "onClose() returned: " + reason);
                    connected = false;
                }

                @Override
                public void onError(Exception e) {
                    Log.d("Exception:", e.toString());
                    connected = false;

                }
            };
        }
        catch (Exception e) {
            Log.d("Exception:", Objects.requireNonNull(e.getMessage()));
            e.printStackTrace();
        }
        messageSocket.connect();
        return true;
    }

    public boolean close(){
        if(!connected){
            return false;
        }
        messageSocket.close();
        return true;
    }

    public void send(Message message){
        messageSocket.send(message.toString());
        addMessageDB(message);
    }

//    private void send(String message){
//        messageSocket.send(message);
//    }

    public List<UserActivity> getUserActivities(){
            return new ArrayList<>(userActivities);

    }

    public void handleMessage(String rawMessage){


        try{
            if(rawMessage.matches(ACTIVE_USER_LIST_PATTERN)){
                Log.d("onMessage", "got activeUsernameList");
                Pattern p = Pattern.compile( ACTIVE_USER_LIST_PATTERN);
                Matcher m = p.matcher(rawMessage);
                m.find();
                String subRawMessage= m.group(1);
                Log.d("active user", "subString " + subRawMessage);
                try{
                    List<String>  activeUsernameList = (new Gson()).fromJson(subRawMessage, List.class);

                    userActivities.clear();
                    for (String username : activeUsernameList){
                        userActivities.add(
                                    new UserActivity(
                                            username
                                    ));
                    }

                    model.getUserActivities().clear();
                    model.getUserActivities().addAll(userActivities);


                    Log.d("active user", "active list index 0:" + activeUsernameList.get(0));
                } catch(Exception e){
                    Log.d("active user", e.toString());
                }

            }
            //create message from jsonMessage
            else if(rawMessage.matches("\\{.*\\}")){


                Message newMsg = gson.fromJson(rawMessage, Message.class);


                Log.d("rawMessage", "run() returned: " + newMsg.toFormattedString());


                addMessageDB(newMsg);//always update db

                if(model == null) return;
                model.addMessage(newMsg);//add message to currently viewed list
                setResult(new Result.Success<>(newMsg));
            }
            else if(userActivities != null){
                //activeUsers
                UserActivity userAct = null;
                if(rawMessage.contains("has Joined the Chat")){ //add username to the banner above the keyboard of active users
                    String[] messageParts = rawMessage.split(" ");
                    String[] name = messageParts[0].split(":");

                    userAct = new UserActivity(
                            name[1]
                    );
                    userActivities.add(userAct);
                    model.addUserActivity(userAct);

                } else if(rawMessage.contains("disconnected")){ //remove username from the banner above the keyboard of active users
                    String[] messageParts = rawMessage.split(" ");
                    userAct = new UserActivity(
                            messageParts[0]
                    );
                    userActivities.remove(userAct);
                    model.removeUserActivity(userAct);

                }

                setResult(new Result.Success<>(userAct));
            }

        } catch(Exception e) {
            Log.d("handleMessage", e.toString());
        }


        if(msgCallback == null) return;
        msgCallback.onMessage(rawMessage);//temp solution



    }

    public void addMessageDB(Message message){
        Integer chatId = message.getChatId();

        if(getGlobalMessages(chatId).contains(message)){
            int index = getGlobalMessages(chatId).indexOf(message);
            getGlobalMessages(chatId).get(index).setSent(true);

        }else{
            getGlobalMessages(chatId).add(message); //add to global Message list
        }


    }

    public List<Message> getGlobalMessages(Integer chatId){
        if(!chatIdtoListMap.containsKey(chatId)){
            chatIdtoListMap.put(chatId, new ArrayList<>());
        }

        return chatIdtoListMap.get(chatId);
    }

    public List<Message> getMessages(Integer chatId) {
        return new ArrayList(getGlobalMessages(chatId)); //avoid data leak
    }


    private <T> void setResult(Result<T> result){
        model.setMessageResult(result);
    }


}
