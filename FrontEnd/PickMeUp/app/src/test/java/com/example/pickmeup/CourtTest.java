package com.example.pickmeup;

import com.example.pickmeup.Court.Court;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class CourtTest {

    List<Court> testCourts = new ArrayList<>();

    @Before
    public void setUp(){
        for(int i = 10; i > 0; i--){
           testCourts.add(new Court(i, 1, "test", i, R.id.loadingScreenBasketball, 42.020785, 42.020785, "good"));
        }
    }

    @Test
    public void testComparator(){
        Collections.sort(testCourts, Court.sortByDist);
        assertEquals(1, testCourts.get(0).getId());
    }

    @After
    public void tearDown(){
        testCourts.clear();
    }

}