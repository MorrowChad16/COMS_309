package com.example.pickmeup.ViewModel.HomeViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pickmeup.Data.Result;
import com.example.pickmeup.Data.UserRepository;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.ViewModel.I_UserViewModel;
import com.example.pickmeup.ViewModel.MutableGenericResult;

public class HomeViewModel extends ViewModel implements I_UserViewModel {

    private UserRepository userRepository;
    private MutableLiveData<MutableGenericResult> updateResult; //see loginResult

    HomeViewModel(UserRepository userRepository) {
        this();
        this.userRepository = userRepository;
    }

   public HomeViewModel() {
       updateResult = new MutableLiveData<>();
    }

    @Override
    public <T> void setResult(Result<T> result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success) result).getData();
            updateResult.postValue(new MutableGenericResult(data)); //setValue onl from main
        }
        else if(result instanceof Result.FailedError){
            Integer error = (((Result.FailedError)result).getFailedError());
            updateResult.postValue(new MutableGenericResult(error));
        }
        else { //this is for exception type errors
            Exception error = (((Result.ExceptionError)result).getExceptionError());
            updateResult.postValue(new MutableGenericResult(error));
        }
    }

    @Override
    public LiveData getResult() {
        return updateResult; //see LoginViewModel
    }

    @Override
    public LoggedInUser getLoggedInUser() { //might want to change this later
        return userRepository.getLoggedInUser();
    }


    @Override
    public void logout(Context context){
        userRepository.logout(context);
    }


    @Override
    public void updateUser(Context context, LoggedInUser updatedUser){
        userRepository.updateUser(context, this, updatedUser);
    }

    @Override
    public void login(String username, String password, Context context) {
        //do not implement
    }

    @Override
    public void loginDataChanged(String username, String password) {
        //do not implement
    }

    @Override
    public void autoLogin(Context context) {
        //do not implement
    }
}