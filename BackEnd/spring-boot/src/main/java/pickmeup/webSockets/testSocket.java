package pickmeup.webSockets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pickmeup.webSockets.config.CustomConfigurator;

/**
 * The type Test socket.
 */
@ServerEndpoint(value = "/websocket/{userId}", configurator = CustomConfigurator.class)
@Component
public class testSocket {
	// Store all socket session and their corresponding userId.
    private static Map<Session, String> sessionuserIdMap = new HashMap<>();
    private static Map<String, Session> userIdSessionMap = new HashMap<>();
    
    private final Logger logger = LoggerFactory.getLogger(testSocket.class);
    
    /**
     * On open.
     *
     * @param session the session
     * @param userId  the user id
     * @throws IOException the io exception
     */
    @OnOpen
    public void onOpen(
    	      Session session, 
    	      @PathParam("userId") String userId) throws IOException 
    {
        logger.info("Entered into Open");
        
        sessionuserIdMap.put(session, userId);
        userIdSessionMap.put(userId, session);
        
        String message="Server: User:" + userId + " has Joined the Chat";
        	broadcast(message);
		
    }

    /**
     * On message.
     *
     * @param session the session
     * @param message the message
     * @throws IOException the io exception
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException 
    {
        // Handle new messages
    	logger.info("Entered into Message: Got Message:"+message);
    	String userId = sessionuserIdMap.get(session);
    	
    	if (message.startsWith("@")) // Direct message to a user using the format "@userId <message>"
    	{
    		String destuserId = message.split(" ")[0].substring(1); // don't do this in your code!
    		sendMessageToPArticularUser(destuserId, "[DM] " + userId + ": " + message);
    		sendMessageToPArticularUser(userId, "[DM] " + userId + ": " + message);
    	}
    	else // Message to whole chat
    	{
	    	broadcast(userId + ": " + message);
    	}
    }

    /**
     * On close.
     *
     * @param session the session
     * @throws IOException the io exception
     */
    @OnClose
    public void onClose(Session session) throws IOException
    {
    	logger.info("Entered into Close");
    	
    	String userId = sessionuserIdMap.get(session);
    	sessionuserIdMap.remove(session);
    	userIdSessionMap.remove(userId);
        
    	String message= "Server: " + userId + " disconnected";
        broadcast(message);
    }

    /**
     * On error.
     *
     * @param session   the session
     * @param throwable the throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) 
    {
        // Do error handling here
    	logger.info("Entered into Error");
    }
    
	private void sendMessageToPArticularUser(String userId, String message) 
    {	
    	try {
    		userIdSessionMap.get(userId).getBasicRemote().sendText(message);
        } catch (IOException e) {
        	logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }
    
    private static void broadcast(String message) 
    	      throws IOException 
    {	  
    	sessionuserIdMap.forEach((session, userId) -> {
    		synchronized (session) {
	            try {
	                session.getBasicRemote().sendText(message);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    });
	}


}