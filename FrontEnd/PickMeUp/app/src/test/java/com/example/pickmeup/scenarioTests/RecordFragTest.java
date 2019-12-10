package com.example.pickmeup.scenarioTests;


import android.os.Build;

import androidx.fragment.app.testing.FragmentScenario;

import com.example.pickmeup.Data.MsgCallBack;
import com.example.pickmeup.Data.model.LoggedInUser;
import com.example.pickmeup.Record.Record;
import com.example.pickmeup.Record.RecordFragment;
import com.example.pickmeup.ViewModel.I_UserViewModel;
import com.example.pickmeup.httpServices.RequestController;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.fragment.app.testing.FragmentScenario.launchInContainer;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1) //created using https://www.vogella.com/tutorials/Robolectric/article.html , http://robolectric.org/writing-a-test/
public class RecordFragTest {


    private volatile  List<Record> records;
    private LoggedInUser loggedInUser;
    private FragmentScenario<RecordFragment> scenario;

    /**
     * Countdown latch
     */
    private CountDownLatch lock = new CountDownLatch(1); //helps make async task synchronous



    //Mockito
    @Mock
    private I_UserViewModel mockedModel;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule(); //init mock objects

    @Before
    public void setUp(){
      records = new ArrayList<>();
      loggedInUser = new LoggedInUser(5,"pandapanda");
      //stop initialization of variables for all RecordFragments
      RecordFragment.setTest(true);

      //set mockedModel Condition
      when(mockedModel.getLoggedInUser()).thenReturn(loggedInUser);

        //create fragment instance
        scenario = launchInContainer(RecordFragment.class); //launches fragment with layout up to Resume() state (after onCreateView is called)

        //set ViewModel of fragment instance to mocked
        scenario.onFragment(fragment -> {
            fragment.testModeViewModel(mockedModel); //set ViewModel
        });

        MsgCallBack onResponse = message -> lock.countDown();//set counter to 1
        RecordFragment.onResponseCallback = onResponse;

    }

    @Test
    public void recordTestMocked() throws Exception{
        RequestController mQueue = RequestController.getTestInstance(); //call fakeRequestQueue
        String mData = "{\"User 5 Games\": [{\"gameId\":1,\"chatId\":null,\"date\":\"Oct 25, 2019 12:00:00 AM\"," +
                "\"gameLocation\":{\"id\":1,\"info\":\"*\",\"verified\":false,\"longt\":-93.6538021,\"lat\":42.0246748,\"name\":\"Tokyo Arena\"}," +
                "\"p10Id\":null,\"p1Id\":4,\"p2Id\":6,\"p3Id\":null,\"p4Id\":null,\"p5Id\":null,\"p6Id\":5,\"p7Id\":null,\"p8Id\":null,\"p9Id\":null," +
                "\"pMax\":8,\"players\":[{\"id\":4,\"username\":\"jichavez\"},{\"id\":5,\"username\":\"pandapanda\"},{\"id\":6,\"username\":\"lionking\"}]," +
                "\"score1\":7,\"score2\":3,\"sport\":\"Basketball\",\"status\":0,\"team1Count\":2,\"team2Count\":1,\"time\":\"05:00:00 PM\"}]}";
        mQueue.setFakeNetworkResponse(mData); //set fake data response


      // continue initialization of variables
        scenario.onFragment(fragment -> {
            fragment.testModeInit(); //continue init with mockedModel
        });

        lock.await(4000, TimeUnit.MILLISECONDS); //start counter
      //Thread.sleep(1000);//allow async task time to finish

        scenario.onFragment(fragment ->
                  records = fragment.getRecords()
        );

      assertTrue( records.size() == 2);
      System.out.println("Records " + Arrays.toString(records.toArray()));
      mQueue.setTestMode(false); //must set to false for normal RequestController to work
    }

    @Test
    public void recordTestLive() throws Exception {

        // continue initialization of variables
        scenario.onFragment(fragment -> {
            fragment.testModeInit(); //continue init with mockedModel
        });

        lock.await(4000, TimeUnit.MILLISECONDS); //start counter
        //Thread.sleep(3000);//allow async task time to finish

        scenario.onFragment(fragment ->
                records = fragment.getRecords()
        );

      assertTrue( records.size() >= 2);
      System.out.println("Records " + Arrays.toString(records.toArray()));
    }
}
