package com.example.pickmeup;

import android.location.Geocoder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class GeocoderTest {

    private double LATITUDE = 42.020785;
    private double LONGITUDE = -93.650716;

    @Mock
    private Geocoder geocoderTest;

    @Before
    public void setUp(){
        //geocoderTest = new Geocoder(this, Locale.getDefault());
    }

    @Test
    public void testGeocoder(){

    }

}
