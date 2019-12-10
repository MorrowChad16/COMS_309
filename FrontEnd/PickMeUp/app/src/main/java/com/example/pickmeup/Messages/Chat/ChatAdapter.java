package com.example.pickmeup.Messages.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickmeup.Messages.Messages.MessagesScreen;
import com.example.pickmeup.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context mCtx;
    private List<Chat> chats;

    /**
     * @param mCtx context of the chat activity
     * @param chats list of all chats user is in
     */
    ChatAdapter(Context mCtx, List<Chat> chats) {
        this.mCtx = mCtx;
        this.chats = chats;
    }

    /**
     * @param parent ??
     * @param viewType ??
     * @return new Chat View object
     */
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.chat_layout, null);
        return new ChatAdapter.ChatViewHolder(view);
    }

    /**
     * @param holder ChatView item to assign values to
     * @param position of the ChatView item
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);

        holder.chatName.setText(chat.getChatName());
        holder.lastMessage.setText(chat.getLastChat());
        //compresses image to ensure high frame rate
        Picasso.with(mCtx)
                .load(chat.getChatImage())
                .fit()
                .centerInside()
                .into(holder.chatImage);
        holder.chatId.setText(Integer.toString(chat.getChatId()));
    }

    /**
     * @return number of chats on screen
     */
    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView chatImage;
        TextView chatName;
        TextView lastMessage;
        TextView chatId;

        /**
         * @param itemView instance of item to grab on screen values of
         */
        ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            chatImage = itemView.findViewById(R.id.chat_image);
            chatName = itemView.findViewById(R.id.chat_name);
            lastMessage = itemView.findViewById(R.id.chat_last_message);
            chatId = itemView.findViewById(R.id.chat_id_invis);

            itemView.setOnClickListener(view1 -> {
                Bundle bundle = new Bundle();
                bundle.putString("chatId", chatId.getText().toString());
                bundle.putString("chatName", chatName.getText().toString());

                mCtx.startActivities(new Intent[]{new Intent(mCtx, MessagesScreen.class).putExtras(bundle)});
            });
        }
    }
}
