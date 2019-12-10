package com.example.pickmeup.ViewModel.LoginViewModel;

import android.content.Context;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pickmeup.Data.Result;
import com.example.pickmeup.Data.UserRepository;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Data.model.LoggedInUserView;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.I_UserViewModel;
import com.example.pickmeup.ViewModel.MutableGenericResult;

public class LoginViewModel extends ViewModel implements I_UserViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<MutableGenericResult> loginResult = new MutableLiveData<>();
    private UserRepository userRepository;

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<MutableGenericResult> getLoginResult() {
        return getResult();
    }

    public LiveData getResult() {
        return loginResult;
    }

    @Override
    public void login(String username, String password, Context context) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = userRepository.login(username, password, context, this);
       setResult(result);
    }

    @Override
    public void autoLogin(Context context){
        userRepository.autoLogin(context, this);
    }

    @Override
    public <T> void setResult(Result<T> result){
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.postValue(new MutableGenericResult(new LoggedInUserView(data.getUsername()))); //setValue onl from main
        }
        else if(result instanceof Result.FailedError){
            Integer error = (((Result.FailedError)result).getFailedError());
            loginResult.postValue(new MutableGenericResult(error));
        }
        else { //this is for exception type errors
            Exception error = (((Result.ExceptionError)result).getExceptionError());
            loginResult.postValue(new MutableGenericResult(error));
        }

    }


    @Override
    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    @Override
    public LoggedInUser getLoggedInUser() {
        return userRepository.getLoggedInUser(); //returns loggedInUser for .putExtra()
    }

    // validation methods before actual authentication
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
        else {
            return !username.trim().isEmpty();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 8;
    }

    @Override
    public void logout(Context context) {
        //do not implement
    }

    @Override
    public void updateUser(Context context, LoggedInUser updatedUser) {
        //do not implement
    }

}
