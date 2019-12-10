package com.example.pickmeup;

import android.content.Context;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.pickmeup.Login.LoginActivity;
import com.example.pickmeup.Record.RecordFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(value = AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context appContext;
    @Before
    public void setup(){
        // Context of the app under test.
        FragmentScenario scenario = launchInContainer(RecordFragment.class); //launches fragment with layout up to Resume() state (after onCreateView is called)
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.pickmeup", appContext.getPackageName());
    }

    public void test1() {
        new LoginActivity();
    }

}
