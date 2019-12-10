package com.example.pickmeup.ViewModel;

import androidx.lifecycle.LiveData;

import com.example.pickmeup.Data.Result;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Messages.Messages.Message;
import com.example.pickmeup.Messages.UserActivity.UserActivity;

import java.util.List;

public interface I_MessageViewModel {
    void send(Message message);
    List<Message> getMessages();
    void addMessage(Message message);
    LoggedInUser getLoggedInUser();
    <T> void setMessageResult(Result<T> result);
    LiveData<MutableGenericResult> getMessageResult();
    List<UserActivity> getUserActivities();
    void addUserActivity(UserActivity userAct);
    void removeUserActivity(UserActivity userAct);
    void setUserActivities(List<UserActivity> userActivities);
    int getMessagesSize();
}
