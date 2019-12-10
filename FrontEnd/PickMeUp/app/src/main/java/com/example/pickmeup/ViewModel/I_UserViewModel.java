package com.example.pickmeup.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.pickmeup.Data.Result;
import com.example.pickmeup.Data.model.LoggedInUser;

//methods that need to be handled by models that will use UserRepository
public interface I_UserViewModel {

    public void login(String username, String password, Context context);
    public void loginDataChanged(String username, String password);
    public void autoLogin(Context context);
    public <T> void setResult(Result<T> result);
    public LiveData getResult();
    public LoggedInUser getLoggedInUser();
    public void logout(Context context);
    public void updateUser(Context context, LoggedInUser updatedUser);

}
