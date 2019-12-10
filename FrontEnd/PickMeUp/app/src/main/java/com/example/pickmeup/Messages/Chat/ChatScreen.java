package com.example.pickmeup.Messages.Chat;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.example.pickmeup.httpServices.CustomVolley;
import com.example.pickmeup.httpServices.RequestController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatScreen extends AppCompatActivity {

    private TextView testTextChat;
    private RequestController mQueue;
    private Handler handler;
    private List<Chat> chats;
    private ChatAdapter adapter;

    private LoggedInUser loggedInUser;

    /**
     * @param savedInstanceState grabs last instance of ChatScreen if possible
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        //call from any fragment in bottomHomeScreen
        //HomeViewModel instantiated in bottomHomeScreen
        HomeViewModel homeViewModel =
                ViewModelProviders.of(Objects.requireNonNull(this), new HomeViewModelFactory()).get(HomeViewModel.class);
        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login

        handler = new Handler();
        testTextChat = findViewById(R.id.test_text_chat);
        mQueue = RequestController.getInstance(this);

        //Updates notification bar to white
        updateNotificationBar();

        chats = new ArrayList<>();
        RecyclerView mChatRecycler = findViewById(R.id.conversations_recycler_view);
        mChatRecycler.setHasFixedSize(true);
        mChatRecycler.setLayoutManager(new LinearLayoutManager(this));
        mChatRecycler.setItemViewCacheSize(20);
        adapter = new ChatAdapter(this, chats) {
        };
        mChatRecycler.setAdapter(adapter);

        grabUserChats();
    }

    /**
     Changes the notification bar to red
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
     * @param chatName court name and short date combined for generic chat names
     * @param lastChat last message in the chat
     * @param drawableName custom drawable image
     */
    private void addChat(String chatName, String lastChat, String drawableName, int chatId){
        chats.add(
                new Chat(
                        chatName,
                        lastChat,
                        getResources().getIdentifier(drawableName, "drawable", getPackageName()),
                        chatId
                )
        );
        Runnable r = () -> adapter.notifyDataSetChanged();
        handler.post(r);
    }

    /**
     * Grabs and parses all the backend stored chats linked to the user
     */
    private void grabUserChats() {
        String url = getString(R.string.grab_user_chats) + loggedInUser.getId();

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try{

                JSONArray jsonArray = response.getJSONArray("ChatInfoList");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject chat = jsonArray.getJSONObject(i);
                    addChat(chat.getString("name") + " - " + chat.getString("date").substring(5, 10), "",
                            chat.getString("sport").toLowerCase(), chat.getInt("chatId"));
                }

            } catch (JSONException e){
                testTextChat.setText("THAT DIDN'T WORK. Error :" + e.toString());
            }

        }, CustomVolley.volleyErrorToastListener(getApplicationContext()));

        mQueue.addToRequestQueue(request);
    }
}
