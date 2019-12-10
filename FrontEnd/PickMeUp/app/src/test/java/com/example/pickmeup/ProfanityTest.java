package com.example.pickmeup;
import com.example.pickmeup.Login.SignUp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProfanityTest {
    @Test
    public void testingProfanity1() {
        assertEquals(1, SignUp.profanityFilter("shit"));
    }
    @Test
    public void testingProfanity2() {
        assertEquals(1,SignUp.profanityFilter("fuck"));
    }

    @Test
    public void testingProfanity3() {
        assertEquals(1,SignUp.profanityFilter("ass"));
    }

    @Test
    public void testingProfanity4() {
        assertEquals(1,SignUp.profanityFilter("bitches"));
    }

}
