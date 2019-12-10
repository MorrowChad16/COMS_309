package com.example.pickmeup.Messages.Messages;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageTest {

    @Test
    public void getTimestamp() {
    }

    //test for Message's getJsonTime
    @Test
    public void whengetJsonTimeIsCalled() {
        Message myMessage = mock(Message.class);
        when(myMessage.getJsonTime()).thenReturn("String");
        assertEquals("String",myMessage.getJsonTime());
        verify(myMessage, times(1)).getJsonTime();//was called x1
    }

    //test for Message's getMessengerName
    @Test
    public void whengetMessengerNameIsCalled() {
        Message myMessage = mock(Message.class);
        when(myMessage.getMessengerName()).thenReturn("String");
        assertEquals("String",myMessage.getMessengerName());
        verify(myMessage, times(1)).getMessengerName();//was called x1
    }

    //test for Message's getMessengerImage
    @Test
    public void whengetMessengerImageIsCalled() {
        Message myMessage = mock(Message.class);
        when(myMessage.getMessengerImage()).thenReturn(0);
        assertEquals(0,myMessage.getMessengerImage());
        verify(myMessage, times(1)).getMessengerImage();//was called x1
    }

    //test for Message's getTimestamp
    @Test
    public void whengetTimestampIsCalled() {
        Message myMessage = mock(Message.class);
        when(myMessage.getTimestamp()).thenReturn("String");
        assertEquals("String",myMessage.getTimestamp());
        verify(myMessage, times(1)).getTimestamp();//was called x1
    }
}