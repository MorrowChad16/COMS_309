package com.example.pickmeup.ViewModel.MessageViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pickmeup.Data.Result;
import com.example.pickmeup.Data.UserRepository;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Messages.Messages.Message;
import com.example.pickmeup.Messages.UserActivity.UserActivity;
import com.example.pickmeup.ViewModel.I_MessageViewModel;
import com.example.pickmeup.ViewModel.MutableGenericResult;

import java.util.List;

public class MessageViewModel extends ViewModel implements I_MessageViewModel {

    private MessageDataSource messageDataSource;
    private UserRepository userRepository;


    private List<Message> messages;//messages for specific chatId
    private List<UserActivity> userActivities;
    private MutableLiveData<MutableGenericResult> messageResult; //see loginResult


    public MessageViewModel(){
        //don't use
    }

   public MessageViewModel(String url, Integer chatId) { //url for gameChatSocket

        messageDataSource = MessageDataSource.getInstance();

        userRepository = userRepository.getInstance();
        messageResult = new MutableLiveData<>();
        messages =  messageDataSource.getMessages(chatId); //init in getMessages()
        userActivities = messageDataSource.getUserActivities();
        messageDataSource.setMessageViewModel(this); //must remove reference model is destroyed
        messageDataSource.connect(url + getLoggedInUser().getId());
    }


    @Override
    public <T> void setMessageResult(Result<T> result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success) result).getData();
            messageResult.postValue(new MutableGenericResult(data)); //setValue onl from main
        }
        else if(result instanceof Result.FailedError){
            Integer error = (((Result.FailedError)result).getFailedError());
            messageResult.postValue(new MutableGenericResult(error));
        }
        else { //this is for exception type errors
            Exception error = (((Result.ExceptionError)result).getExceptionError());
            messageResult.postValue(new MutableGenericResult(error));
        }
    }

    @Override
    public LiveData<MutableGenericResult> getMessageResult() {
        return messageResult; //see LoginViewModel
    }

    @Override
    public void send(Message message) {
        messages.add(message);
        messageDataSource.send(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public void addMessage(Message message)
    {
        if(messages.contains(message)){
            int index = messages.indexOf(message);
            messages.get(index).setSent(true);

        }else{
            messages.add(message);
        }

    }

    public int getMessagesSize() {
        return messages.size();
    }

    @Override
    public LoggedInUser getLoggedInUser() { //might want to change this later
        return userRepository.getLoggedInUser();
    }

    @Override
    public void onCleared(){

        messageDataSource.setMessageViewModel(null);

        super.onCleared();

    }

    @Override
    public List<UserActivity> getUserActivities(){
        return userActivities;
    }

    @Override
    public void addUserActivity(UserActivity userAct) {
        userActivities.add(userAct);
    }

    @Override
    public void removeUserActivity(UserActivity userAct) {
        userActivities.remove(userAct);
    }

    @Override
    public void setUserActivities(List<UserActivity> userActivities) {
        this.userActivities = userActivities;
    }

}