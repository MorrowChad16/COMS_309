package com.example.pickmeup.Messages.Messages;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Messages.UserActivity.UserActivity;
import com.example.pickmeup.Messages.UserActivity.UserActivityAdapter;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.I_MessageViewModel;
import com.example.pickmeup.ViewModel.MessageViewModel.MessageViewModel;
import com.example.pickmeup.ViewModel.MessageViewModel.MessageViewModelFactory;
import com.example.pickmeup.ViewModel.MutableGenericResult;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessagesScreen extends AppCompatActivity {

    Button sendText;
    EditText textMessage;
    TextView chatName;

    LoggedInUser loggedInUser;

    List<Message> messages;
    MessageAdapter adapter;

    List<UserActivity> userActivities;
    UserActivityAdapter adapter1;

    private Handler hand;
    private Integer chatId;

    /**
     * @param savedInstanceState return last saved instance of MessagesScreen if possible
     */
    @SuppressWarnings("unchecked")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_screen);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sendText = findViewById(R.id.button_chatbox_send);
        textMessage = findViewById(R.id.edittext_chatbox);
        chatName = findViewById(R.id.chat_name_static);
        chatName.setText(getIntent().getStringExtra("chatName"));

        chatId = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("chatId")));

        //Updates notification bar to white
        updateNotificationBar();

        RecyclerView mMessageRecycler = findViewById(R.id.messages_recycler_view);
        mMessageRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setItemViewCacheSize(20);

        //MessageViewModel init
        String url = getString(R.string.grab_game_chat_socket);
        I_MessageViewModel messageViewModel = ViewModelProviders.of(this, new MessageViewModelFactory(url, chatId)).get(MessageViewModel.class);
        loggedInUser = messageViewModel.getLoggedInUser();
        messages = messageViewModel.getMessages();

        adapter = new MessageAdapter(this, messages);
        mMessageRecycler.setAdapter(adapter);
        //end MessageViewModel init
        RecyclerView activeUsers = findViewById(R.id.users_activity_recycler_view);
        activeUsers.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        activeUsers.setLayoutManager(linearLayoutManager1);
        userActivities = messageViewModel.getUserActivities();
        adapter1 = new UserActivityAdapter(this, userActivities);
        activeUsers.setAdapter(adapter1);

        hand = new Handler();

        sendText.setOnClickListener(view -> {
            if(!textMessage.getText().toString().equals("")){
                String msg = textMessage.getText().toString();
                textMessage.setText(""); //putting this outside of websocket stuff fixed app crash

                Message message = new Message(
                            chatId,
                            msg,
                            loggedInUser.getUsername(),
                            3,
                            new Date()
                    );

                Log.d("sendText","sending " + message.toString());

                messageViewModel.send(message);
                linearLayoutManager.scrollToPosition(messageViewModel.getMessagesSize() - 1);

                Runnable r = () -> adapter.notifyDataSetChanged();
                hand.post(r);
            }

            messageViewModel.getMessageResult().observe(this, (Observer) updateResult -> {
               MutableGenericResult result = messageViewModel.getMessageResult().getValue();
                assert result != null;
                if(result.getSuccess() != null){
                   if(result.getSuccess() instanceof Message){
                       adapter.notifyDataSetChanged();
                   } else if(result.getSuccess() instanceof UserActivity){
                       adapter1.notifyDataSetChanged();
                   }
               }
            });
        });

    } //end onCreate

    /**
     * handles when the message screen is closed
     */
    @Override
    public void onStop(){
        super.onStop();
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
}
