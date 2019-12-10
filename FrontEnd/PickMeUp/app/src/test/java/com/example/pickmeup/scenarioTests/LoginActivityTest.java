package com.example.pickmeup.scenarioTests;

import android.os.Build;
import android.widget.Button;
import android.widget.EditText;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ActivityScenario;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Login.LoginActivity;
import com.example.pickmeup.R;
import com.example.pickmeup.httpServices.RequestController;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1) //created using https://www.vogella.com/tutorials/Robolectric/article.html , http://robolectric.org/writing-a-test/
public class LoginActivityTest {

    private ActivityScenario<LoginActivity> activity;

    @Mock
    private Network mockedNetwork;
    private RequestController mQueue;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule(); //init mock objects
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule(); //necessary for LiveData from viewModel to be synchronous during testing


    @Before
    public void setUp() throws Exception
    {
        mQueue = RequestController.getTestInstance();

        mQueue.setTestMode(true, mockedNetwork); //start fakeQueue with mocked network

        String userPanda = "pandapanda";
        int pandaId = 5;

        byte[] mData = ("{1 {\"id\":"+ pandaId +",\"username\":\""+ userPanda +"\"}").getBytes(); //json String data to return

        //mocked response
        when(mockedNetwork.performRequest(Mockito.any(Request.class))).thenReturn(new NetworkResponse(mData));

        activity = ActivityScenario.launch(LoginActivity.class);
    }

    @Test
    public void shouldNotBeNull() throws Exception
    {
        assertNotNull( activity );
    }

    @Test
    public void shouldHaveDefaults() throws Exception {

        activity.onActivity(
                new ActivityScenario.ActivityAction<LoginActivity>() {
                    @Override
                    public void perform(LoginActivity activity) {
                        EditText usernameEditText = activity.findViewById(R.id.username);
                        EditText passwordEditText = activity.findViewById(R.id.password_new);

                        Button loginButton = activity.findViewById(R.id.login);
                        Button signupButton = activity.findViewById(R.id.sign_up);

                        String loginText = loginButton.getText().toString();
                        assertEquals("sign in", loginText);

                        String signUpText = signupButton.getText().toString();
                        assertEquals("sign up", signUpText);


                        assertEquals("", usernameEditText.getText().toString());
                        assertEquals("", passwordEditText.getText().toString());
                    }
                });


    }

    @Test
    public void buttonClickShouldStartNewActivity() throws Exception
    {

        activity.onActivity(
                new ActivityScenario.ActivityAction<LoginActivity>() {
                    @Override
                    public void perform(LoginActivity activity) {
                        EditText usernameEditText = activity.findViewById(R.id.username);
                        EditText passwordEditText = activity.findViewById(R.id.password_new);

                        //signing in
                        usernameEditText.setText("pandapanda");
                        passwordEditText.setText("pandapanda");

                        //click to send sign Request
                        Button loginButton = activity.findViewById(R.id.login);
                        loginButton.performClick();

                        try {
                            Thread.sleep(1500); //sleep main thread while volley request and other called methods happen
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        String userPanda = "pandapanda";
                        int pandaId = 5;

                        shadowOf(getMainLooper()).idle();
                        LoggedInUser loggedInUser = activity.getUser();

                        assertNotNull(loggedInUser); //sub-test1
                        assertEquals(userPanda, loggedInUser.getUsername());
                        assertEquals(pandaId, (int) loggedInUser.getId());


                    }
                }
        );

        mQueue.setTestMode(false);//required to stop fakeRequestQueue


//         assertFalse(Lifecycle.State.RESUMED == activity.getState());
//        assertEquals(activity.getResult().getResultCode(),  Activity.RESULT_OK);

    }


}
