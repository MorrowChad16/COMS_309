package com.example.pickmeup.Account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.pickmeup.Account.Address.AddressesActivity;
import com.example.pickmeup.Account.Admin.AdminActivity;
import com.example.pickmeup.Account.NewCourt.AddNewCourtScreen;
import com.example.pickmeup.Account.Password.ChangePasswordActivity;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Login.SplashScreen;
import com.example.pickmeup.R;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModel;
import com.example.pickmeup.ViewModel.HomeViewModel.HomeViewModelFactory;

import java.util.Objects;

public class AccountFragment extends Fragment {
    //call from any fragment in bottomHomeScreen
    private HomeViewModel homeViewModel; //HomeViewModel instantiated in bottomHomeScreen
    private LoggedInUser loggedInUser;

    private EditText firstName;
    private EditText lastName;
    private EditText userName;
    private EditText email;

    private Button submitChanges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //call from any fragment in bottomHomeScreen
        homeViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), new HomeViewModelFactory()).get(HomeViewModel.class); //get instance of homeViewModel associated with BottomHomeScreen

        //once the class is called it will fetch the account fragment for viewing
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstName = Objects.requireNonNull(getActivity()).findViewById(R.id.account_first_name_dynamic);
        Button firstNameEditButton = getActivity().findViewById(R.id.edit_first_name);
        lastName = getActivity().findViewById(R.id.account_last_name_dynamic);
        Button lastNameEditButton = getActivity().findViewById(R.id.edit_last_name);
        userName = getActivity().findViewById(R.id.account_user_name_dynamic);
        Button userNameEditButton = getActivity().findViewById(R.id.edit_username);
        email = getActivity().findViewById(R.id.account_email_dynamic);
        Button emailEditButton = getActivity().findViewById(R.id.edit_email);
        TextView changePassword = getActivity().findViewById(R.id.account_password);
        TextView accountAddresses = getActivity().findViewById(R.id.account_addresses);

        TextView addNewCourt = getActivity().findViewById(R.id.new_court_text);
        Button logout = getActivity().findViewById(R.id.log_out);
        loggedInUser = homeViewModel.getLoggedInUser(); ///get user from login

        TextView adminTab = getActivity().findViewById(R.id.admin_tab);
        ImageView adminArrow = getActivity().findViewById(R.id.admin_arrow);
        View adminLine = getActivity().findViewById(R.id.admin_line);
        submitChanges = getActivity().findViewById(R.id.submit_account_changes);

        //If the user isn't an admin then don't show the admin tab
        if(loggedInUser.getGid() != 2){
            adminTab.setVisibility(View.INVISIBLE);
            adminArrow.setVisibility(View.INVISIBLE);
            adminLine.setVisibility(View.INVISIBLE);
        }

        setFirstName();
        setLastName();
        setUserName();
        setEmail();

        /*
        Person clicks the logout button, so call the homeViewModel logout and go back to the splash screen
         */
        logout.setOnClickListener(view1 -> {
            homeViewModel.logout(getActivity());
            Intent intent = new Intent(getActivity(), SplashScreen.class);
            startActivity(intent);
        });

        /*
        Person clicks on the edit addresses tab, so go to the editAccount screen
         */
        accountAddresses.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivity(), AddressesActivity.class);
            startActivity(intent);
        });

        /*
        Person clicks the add new court text box, so open up that screen
         */
        addNewCourt.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AddNewCourtScreen.class);
            startActivity(intent);
        });

        /*
        Goes to change password screen when the user clicks the change password tab
         */
        changePassword.setOnClickListener(view114 -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        /*
        if the first name clicks the username edit button then the focus changes to the first name text box
         */
        firstNameEditButton.setOnClickListener(view17 -> {
            firstName.setEnabled(true);
            firstName.requestFocus(firstName.getText().length());
        });

        /*
        if the user clicks the last name edit button then the focus changes to the last name text box
         */
        lastNameEditButton.setOnClickListener(view18 -> {
            lastName.setEnabled(true);
            lastName.requestFocus(lastName.getText().length());
        });

        /*
        if the user clicks the username edit button then the focus changes to the username text box
         */
        userNameEditButton.setOnClickListener(view19 -> {
            userName.setEnabled(true);
            userName.requestFocus(userName.getText().length());
        });

        /*
        if the user clicks the email edit button then the focus changes to the email text box
         */
        emailEditButton.setOnClickListener(view110 -> {
            email.setEnabled(true);
            email.requestFocus(email.getText().length());
        });

        /*
        If the first name text box loses focus and the user changed the info then show the submit changes button
         */
        firstName.setOnFocusChangeListener((view13, hasFocus) -> {
            //if the first name edit box loses focus and the user changes the first name from its original then show the submit changes button if not already set
            if(!hasFocus && !firstName.getText().toString().equals(loggedInUser.getFirstname()) && submitChanges.getVisibility() == View.INVISIBLE){
                submitChanges.setVisibility(View.VISIBLE);
            }
        });

        /*
        If the last name text box loses focus and the user changed the info then show the submit changes button
         */
        lastName.setOnFocusChangeListener((view14, hasFocus) -> {
            //if the lastName edit box loses focus and the user changes the last name from its original then show the submit changes button if not already set
            if(!hasFocus && !lastName.getText().toString().equals(loggedInUser.getLastname()) && submitChanges.getVisibility() == View.INVISIBLE){
                submitChanges.setVisibility(View.VISIBLE);
            }
        });

        /*
        If the username text box loses focus and the user changed the info then show the submit changes button
         */
        userName.setOnFocusChangeListener((view15, hasFocus) -> {
            //if the username edit box loses focus and the user changes the username from its original then show the submit changes button if not already set
            if(!hasFocus && !userName.getText().toString().equals(loggedInUser.getUsername()) && submitChanges.getVisibility() == View.INVISIBLE){
                submitChanges.setVisibility(View.VISIBLE);
            }
        });

        /*
        If the email text box loses focus and the user changed the info then show the submit changes button
         */
        email.setOnFocusChangeListener((view16, hasFocus) -> {
            //if the email edit box loses focus and the user changes the email from its original then show the submit changes button if not already set
            if(!hasFocus && !email.getText().toString().equals(loggedInUser.getEmail()) && submitChanges.getVisibility() == View.INVISIBLE){
                submitChanges.setVisibility(View.VISIBLE);
            }
        });

        /*
        Sends the updated user info the logged in user container
        Then user the homeViewModel to submit these changes to the backend
        Then hides the 'submit changes' button
         */
        submitChanges.setOnClickListener(view111 -> {
            //if the user put in a different first name then change it
            if(!firstName.getText().toString().equals(loggedInUser.getFirstname())){
                loggedInUser.setFirstname(firstName.getText().toString());
            }

            //if the user put in a different last name then change it
            if(!lastName.getText().toString().equals(loggedInUser.getLastname())){
                loggedInUser.setLastname(lastName.getText().toString());
            }

            //if the user put in a different email then change it
            if(!email.getText().toString().equals(loggedInUser.getEmail())){
                loggedInUser.setEmail(email.getText().toString());
            }

            firstName.setEnabled(false);
            lastName.setEnabled(false);
            userName.setEnabled(false);
            email.setEnabled(false);

            homeViewModel.updateUser(getActivity(), loggedInUser);

            //set the button back to invisible
            submitChanges.setVisibility(View.INVISIBLE);
        });

        /*
        Goes to the admin screen when user clicks the admin tab
         */
        adminTab.setOnClickListener(view113 -> {
            Intent intent = new Intent(getActivity(), AdminActivity.class);
            startActivity(intent);
        });


        /*
        Reduces the keyboard when the user clicks outside of the current text box they are in
        Keeps the focus on the current text box so it doesn't default back to the top
         */
        RelativeLayout touchInterceptor = Objects.requireNonNull(getActivity()).findViewById(R.id.account_fragment);
        touchInterceptor.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (firstName.isFocused()) {
                    Rect outRect = new Rect();
                    firstName.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        firstName.clearFocus();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                } else if (lastName.isFocused()) {
                    Rect outRect = new Rect();
                    lastName.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        lastName.clearFocus();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                } else if (email.isFocused()) {
                    Rect outRect = new Rect();
                    email.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        email.clearFocus();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                } else if (userName.isFocused()) {
                    Rect outRect = new Rect();
                    userName.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                        userName.clearFocus();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
            return false;
        });
    }

    /**
     * grabs the first name of the logged in user and places it in the account first name section
     */
    private void setFirstName(){
        firstName.setText(loggedInUser.getFirstname());
    }

    /**
     * grabs the last name of the logged in user and places it in the account last name section
     */
    private void setLastName(){
        lastName.setText(loggedInUser.getLastname());
    }

    /**
     * grabs the username of the logged in user and places it in the account username section
     */
    private void setUserName(){
        userName.setText(loggedInUser.getUsername());
    }

    /**
     * grabs the email of the logged in user and places it in the account email section
     */
    private void setEmail(){
        email.setText(loggedInUser.getEmail());
    }
}
