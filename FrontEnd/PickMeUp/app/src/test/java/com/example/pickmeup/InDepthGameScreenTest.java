package com.example.pickmeup;

import com.example.pickmeup.Home.IndividualGame.InDepthGameScreen;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class InDepthGameScreenTest {

    InDepthGameScreen testScreen = new InDepthGameScreen();

    private double LATITUDE = 42.024963;
    private double LONGITUDE = -93.653624;

    @Before
    public void setUp(){
        testScreen.currentLatitude = 42.020808;
        testScreen.currentLongitude = -93.650779;
    }

    @Test
    public void testDecoder(){
        assertEquals("0.29", Double.toString(testScreen.calculateDistanceTo(LATITUDE, LONGITUDE)).substring(0, 4));
    }
}