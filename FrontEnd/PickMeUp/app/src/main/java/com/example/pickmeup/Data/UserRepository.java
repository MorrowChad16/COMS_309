package com.example.pickmeup.Data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.ViewModel.I_UserViewModel;

import static com.example.pickmeup.Data.SHARED_PREFERENCES_CONSTANTS.PASSWD;
import static com.example.pickmeup.Data.SHARED_PREFERENCES_CONSTANTS.SHARED_PREFERENCES_KEY;
import static com.example.pickmeup.Data.SHARED_PREFERENCES_CONSTANTS.USERNAME;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class UserRepository {

    private static volatile UserRepository instance;

    private I_UserViewModel viewModel;
    private UserDataSource dataSource;
    private Context context;
    private String password;
    private boolean autoConnectMode;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;
    private LoggedInUser updatedUser = null;

    // private constructor : singleton access
    private UserRepository() {
        autoConnectMode = false;
        this.dataSource = UserDataSource.getInstance();
    }

    public static UserRepository getInstance() {
        if(instance == null){
            instance = new UserRepository();
        }

        return instance;
    }

    public void autoLogin(Context context, I_UserViewModel loginViewModel){
        this.context = context;
        this.viewModel =  loginViewModel;
        autoConnectMode = true;

        try{
            if(!context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).contains(USERNAME)) {
                Result<LoggedInUser> result = new Result.FailedError(1);
                autoConnectMode = false;
                setResult(result);

            } else {
                String username = readString(context, USERNAME);
                String password = readString(context, PASSWD);

                dataSource.login(username, password, context, this);
            }
        } catch (Exception e){
            Result<LoggedInUser> result = new Result.ExceptionError(e);
            autoConnectMode = false;
            setResult(result);
        }

    }


    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout(Context context) {
        user = null;
        removeUserCache(context);
        dataSource.logout();
    }

    public void setLoggedInUser(LoggedInUser user, String password) {
        setLoggedInUser(user);
        if(!autoConnectMode) {
            addUserCache(context, user, password);
        }

        context = null;

    }

    private static String readString(Context context, final String KEY) {
        return context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).getString(KEY, null);
    }

    private static void addUserCache(Context context, LoggedInUser user, String password) {

        // If user credentials will be cached in local storage, it is recommended it be encrypted n
        // @see https://developer.android.com/training/articles/keystore
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(USERNAME, user.getUsername());
        editor.putString(PASSWD, password);
        editor.apply();

    }

    private static void removeUserCache(Context context) {

        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE).edit();
        editor.remove(USERNAME);
        editor.remove(PASSWD);
        editor.apply();
    }

    public void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public LoggedInUser getLoggedInUser(){
        LoggedInUser copy = null;
        if(user != null) copy = (LoggedInUser) user.clone();
        return copy;
    }


    //only forViewModel
    public Result<LoggedInUser> login(String username, String password, Context context, I_UserViewModel viewModel) {
        this.viewModel =  viewModel;
        this.context = context;
        this.password = password;
        autoConnectMode = false;

        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password, context, this);

        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public void updateUser(Context context, I_UserViewModel viewModel, LoggedInUser updatedUser){
        this.viewModel = viewModel;
        this.updatedUser = updatedUser;
        dataSource.updateUser(context, updatedUser);
    }

    protected void setUpdateResult(Result result){
        if (result instanceof Result.Success) {
            setLoggedInUser(updatedUser);
            updatedUser = null;
        }
        setResult(result);
    }



    private void setResult(Result result){
        if(viewModel == null) {return;}
        viewModel.setResult(result);
    }


    protected void  setLoginResult(Result<LoggedInUser> result) {//this method is exclusively for returning a result after login has been called once before

       if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData(), this.password);
        }
        else if(autoConnectMode){
           result = new Result.FailedError(-7);
           autoConnectMode = false;
       }
       setResult(result);

    }

}
