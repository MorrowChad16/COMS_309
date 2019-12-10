package com.example.pickmeup.ViewModel.MessageViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


/**
 * ViewModel provider factory to instantiate MessageViewModel.
 * Required given MessageViewModel has a non-empty constructor
 */
public class MessageViewModelFactory implements ViewModelProvider.Factory {
    private String url;
    private Integer chatId;

    private MessageViewModelFactory(){
    //don't use
    }
    public MessageViewModelFactory(String url, Integer chatId){
        this.url = url; //url for gameChatSocket
        this.chatId = chatId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MessageViewModel.class)) {
            return (T) new MessageViewModel(url, chatId);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
