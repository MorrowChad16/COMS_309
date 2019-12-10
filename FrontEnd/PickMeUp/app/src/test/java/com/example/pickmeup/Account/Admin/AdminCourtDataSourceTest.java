package com.example.pickmeup.Account.Admin;

import android.os.Build;

import com.example.pickmeup.Data.MsgCallBack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP_MR1) //created using https://www.vogella.com/tutorials/Robolectric/article.html , http://robolectric.org/writing-a-test/

public class AdminCourtDataSourceTest {

    private AdminCourtDataSource dataSource;

    /**
     * Countdown latch
     */
    private CountDownLatch lock = new CountDownLatch(1); //helps make async task synchronous

    public static final String url = "ws://coms-309-bs-3.misc.iastate.edu:8080/adminLocationSocket/"; //"ws://localhost:8080/adminLocationSocket/";



    private void connectSocket() throws Exception{
        if(dataSource.isClosed()){
            MsgCallBack onOpen = message -> lock.countDown();//set counter to 1
            dataSource.setOnOpenCallback(onOpen); //callback called when webSocket OnOpen is called.

            dataSource.connect(url + 1); //default test user
            lock.await(4000, TimeUnit.MILLISECONDS); //start counter

        }
    }
    @Before
   public void setUp(){
        dataSource = AdminCourtDataSource.getInstance();
    }
    @Test
    public void getInstance() {
        assertNotNull(AdminCourtDataSource.getInstance());
    }

    @Test
    public void count() throws Exception{
        connectSocket();
        dataSource.count();
        Thread.sleep(1000);
        assertNotNull(dataSource.getLastMessageIn());
        assertTrue(dataSource.getLastMessageIn().contains("game locations"));
        System.out.println(dataSource.getLastMessageIn());

    }

    @Test
    public void all() throws Exception{
        connectSocket();
        dataSource.all();
        Thread.sleep(3000);
        assertNotNull(dataSource.getLastMessageIn());
        assertTrue(dataSource.getLastMessageIn().contains("{\"locations\":"));
        System.out.println(dataSource.getLastMessageIn());
    }

    @Test
    public void close() throws Exception{
        connectSocket();
        assertFalse(dataSource.isClosed());
        dataSource.close();
        Thread.sleep(1000);
        assertTrue(dataSource.isClosed());

    }
}