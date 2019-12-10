package com.example.pickmeup.Messages.Messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private volatile List<Message> mMessageList;

    private LoggedInUser loggedInUser;
    private Context mContext;

    /**
     * @param mActivity current activity message recycler view is on
     * @param messageList array of messages in chat
     */
    MessageAdapter(AppCompatActivity mActivity, List<Message> messageList) {
        mMessageList = messageList;
        mContext = mActivity.getApplicationContext();

        //call from any fragment in bottomHomeScreen , HomeViewModel instantiated in bottomHomeScreen
        HomeViewModel homeViewModel = ViewModelProviders.of(Objects.requireNonNull(mActivity), new HomeViewModelFactory()).get(HomeViewModel.class); //get instance of homeViewModel associated with BottomHomeScreen
        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login
    }

    /**
     * @return number of messages from all users
     */
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    /**
     * @param position of message we want to analyze
     * @return int value of type of message (received vs sent)
     */
    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if (message.getMessengerName().equals(loggedInUser.getUsername())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    /**
     * @param parent ??
     * @param viewType ??
     * @return Inflates the appropriate layout according to the ViewType.
     */
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_message_layout, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_message_layout, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    /**
     * @param holder ??
     * @param position position in recycler view we want to change object
     * Passes the message object to a ViewHolder so that the contents can be bound to UI.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTimestamp());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTimestamp());
            nameText.setText(message.getMessengerName());

            int ResID = message.getMessengerImage();
            if(ResID == 0) ResID = R.drawable.basketball;
            //compresses image to ensure high frame rate
            Picasso.with(mContext)
                    .load(ResID)
                    .fit()
                    .centerCrop()
                    .into(profileImage);
        }
    }
}