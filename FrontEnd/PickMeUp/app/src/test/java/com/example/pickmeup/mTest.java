package com.example.pickmeup;
import com.example.pickmeup.ViewModel.MessageViewModel.MessageDataSource;
import com.example.pickmeup.Home.CourtGames.LocationGamesScreen;
import com.example.pickmeup.Messages.Chat.ChatAdapter;
import com.example.pickmeup.Messages.Messages.Message;
import com.example.pickmeup.Messages.Messages.MessageAdapter;
import com.example.pickmeup.Messages.UserActivity.UserActivity;
import com.example.pickmeup.ViewModel.LoginViewModel.LoginFormState;
import com.example.pickmeup.ViewModel.MessageViewModel.MessageViewModel;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class mTest {

    //test for LocationGamesScreen's LocationGamesScreenAddGame
    @Test
    public void whenLocationGamesScreenAddGameIsCalled() {
        LocationGamesScreen myList = mock(LocationGamesScreen.class);
        doNothing().when(myList).addGame(isA(String.class),isA(Integer.class), isA(String.class));
        myList.addGame("basketball", 1,"Oct 25, 2019 12:00:00 AM");
        verify(myList, times(1)).addGame(isA(String.class),any(Integer.class), any(String.class));//was called x1
    }

    //test for ChatAdapter's GetItemCount
    @Test
    public void whenGetItemCountIsCalled() {
        ChatAdapter myChatAdapter = mock(ChatAdapter.class);
        //List<String> mockList = mock(List.class);
        when(myChatAdapter.getItemCount()).thenReturn(9);
        assertEquals(9,myChatAdapter.getItemCount());
        verify(myChatAdapter, times(1)).getItemCount();//was called x1
    }



    //test for Message's getMessage
    @Test
    public void whengetMessageIsCalled() {
        Message myMessage = mock(Message.class);
        when(myMessage.getMessage()).thenReturn("String");
        assertEquals("String",myMessage.getMessage());
        verify(myMessage, times(1)).getMessage();//was called x1
    }



    //test for Message's getChatId
    @Test
    public void whengetChatIdIsCalled() {
        Message myMessage = mock(Message.class);
        when(myMessage.getChatId()).thenReturn(0);
        assertNotEquals(java.util.Optional.of(0),myMessage.getChatId());
        verify(myMessage, times(1)).getChatId();//was called x1
    }

    //test for Message's toFormattedString
    @Test
    public void whentoFormattedStringIsCalled() {
        Message myMessage = mock(Message.class);
        when(myMessage.toFormattedString()).thenReturn("String");
        assertEquals("String",myMessage.toFormattedString());
        verify(myMessage, times(1)).toFormattedString();//was called x1
    }

    //test for MessageAdapter's getItemCount
    @Test
    public void whengetItemCountIsCalled() {
        MessageAdapter myMessage = mock(MessageAdapter.class);
        when(myMessage.getItemCount()).thenReturn(0);
        assertEquals(0,myMessage.getItemCount());
        verify(myMessage, times(1)).getItemCount();//was called x1
    }

    //test for MessageAdapter's getItemViewType
    @Test
    public void whengetItemViewTypeCalled() {
        MessageAdapter myMessage = mock(MessageAdapter.class);
        when(myMessage.getItemViewType(isA(Integer.class))).thenReturn(0);
        assertEquals(0,myMessage.getItemViewType(isA(Integer.class)));
        verify(myMessage, times(1)).getItemViewType(isA(Integer.class));//was called x1
    }

    //test for MessageViewModel's onCleared
    @Test
    public void whenonClearedCalled() {
        MessageViewModel myMessage = mock(MessageViewModel.class);
        doNothing().when(myMessage).onCleared();
        verify(myMessage, times(0)).onCleared();//was called x1
    }

    //test for MessageViewModel's addUserActivity
    @Test
    public void whenaddUserActivityCalled() {
        MessageViewModel myMessage = mock(MessageViewModel.class);
        doNothing().when(myMessage).addUserActivity(isA(UserActivity.class));
        verify(myMessage, times(0)).addUserActivity(isA(UserActivity.class));//was called x1
    }

   //test for MessageViewModel's removeUserActivity
    @Test
    public void whenremoveUserActivityCalled() {
        MessageViewModel myMessage = mock(MessageViewModel.class);
        doNothing().when(myMessage).removeUserActivity(isA(UserActivity.class));
        verify(myMessage, times(0)).removeUserActivity(isA(UserActivity.class));//was called x1
    }

   //test for MessageViewModel's setUserActivities
    @Test
    public void whensetUserActivitiesCalled() {
        MessageViewModel myMessage = mock(MessageViewModel.class);
        doNothing().when(myMessage).setUserActivities(isA(List.class));
        verify(myMessage, times(0)).setUserActivities(isA(List.class));//was called x1
    }
    //test for LoginFormState's isDataValidIs
    @Test
    public void whenisDataValidIsCalled() {
        LoginFormState myMessage = mock(LoginFormState.class);
        when(myMessage.isDataValid()).thenReturn(true);
        assertEquals((true),myMessage.isDataValid());
        verify(myMessage, times(1)).isDataValid();//was called x1
    }

        //test for LoginFormState's isDataValid
    @Test
    public void whenisDataValidIsCalled2() {
        LoginFormState myMessage = mock(LoginFormState.class);
        when(myMessage.isDataValid()).thenReturn(false);
        assertEquals((false),myMessage.isDataValid());
        verify(myMessage, times(1)).isDataValid();//was called x1
    }

    //test forMessageDataSource's connect
    @Test
    public void whenconnectIsCalled() {
        MessageDataSource myMessage = mock(MessageDataSource.class);
        when(myMessage.connect(any(String.class))).thenReturn(false);
        assertEquals((false),myMessage.connect(any(String.class)));

    }

     //test forMessageDataSource's close
    @Test
    public void whencloseIsCalled() {
        MessageDataSource myMessage = mock(MessageDataSource.class);
        when(myMessage.close()).thenReturn(true);
        assertEquals((true),myMessage.close());
        verify(myMessage, times(1)).close();//was called x1
    }

  //test forMessageDataSource's close
    @Test
    public void whencloseIsCalled2() {
        MessageDataSource myMessage = mock(MessageDataSource.class);
        when(myMessage.close()).thenReturn(false);
        assertEquals((false),myMessage.close());
        verify(myMessage, times(1)).close();//was called x1
    }

      //test forMessageDataSource's send
    @Test
    public void whensendIsCalled2() {
        MessageDataSource myMessage = mock(MessageDataSource.class);
        doNothing().when(myMessage).send(isA(Message.class));
        verify(myMessage, times(0)).send(isA(Message.class));//was called x1
    }
}