package pickmeup.webSockets;

import java.io.IOException;
import java.util.*;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pickmeup.game.Game;
import pickmeup.game.GameRepository;
import pickmeup.user.User;
import pickmeup.user.UserRepository;
import pickmeup.webSockets.config.CustomConfigurator;

/**
 * The type Test socket.
 */
@ServerEndpoint(value = "/gameChatSocket/{userId}", configurator = CustomConfigurator.class)
@Component
public class GameChatSocket {

// Store all socket session and their corresponding userId.
private static Map<Session, Integer> sessionuserIdMap = new HashMap<>();
private static Map<Integer, Session> userIdSessionMap = new HashMap<>();
private static Map<Integer, String> userIdUsernameMap = new HashMap<>();

private static String simpleDateFormatJSON = "yyyy-MM-dd'T'HH:mm:ss";
private static final String GET_ACTIVE_USER_METHOD_PATTERN = "^getActiveUsernameList\\(\\) [0-9]+$";
private static Gson gson = new GsonBuilder()
.setDateFormat(simpleDateFormatJSON)
.create();
 //for auto converting from json
private static Logger log; //static log for static methods
private final Logger logger = LoggerFactory.getLogger(GameChatSocket.class);

@Autowired
private GameRepository gameRepo;

@Autowired
private UserRepository userRepo;

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
          @PathParam("userId") Integer userId) throws IOException 
{
    logger.info("Entered into Open");


    log = logger;

    //add user to chats
    sessionuserIdMap.put(session, userId);
    userIdSessionMap.put(userId, session);

    //get username
    String username = null;
    try{
        username = userRepo.findById(userId).get().getUsername();
        userIdUsernameMap.put(userId , username);
    } catch(Exception e) {
        logger.info(e.toString());
    }

    //send initial message
    broadcast("User:" + username + " has Joined the Chat");
    sendMessageToPArticularUser(userId, "{\"activeUsernameList\": " + gson.toJson(getActiveUsernameList()) + "}");
    
}

/**
 * On message.
 *
 * @param session the session
 * @param message the message
 * @throws IOException the io exception
 */
@SuppressWarnings("DuplicateExpressions")
@OnMessage
public void onMessage(Session session, String message) throws IOException 
{
    // Handle new messages
    logger.info("Entered into Message: Got Message:" + message);
    Integer messengerId = sessionuserIdMap.get(session);
    
    GameChatMessage gcmsg = null;    
        try{

            if (message.startsWith("@")) // Direct message to a user using the format "@userId:::<message>"
            {
                Integer destuserId = Integer.parseInt( message.split(":::")[0].substring(1)); // don't do this in your code!

                sendMessageToPArticularUser(destuserId, "[DM] " + messengerId + ": " + message);
                sendMessageToPArticularUser(messengerId, "[DM] " + messengerId + ": " + message);
            }
            else if(handleCommand(message)){
               //do nothing
            }
            else // Message to whole game chat
            {
                gcmsg = gson.fromJson(message, GameChatMessage.class);
                logger.info("Incoming message " + gcmsg.toFormattedString());
                
                handleCommand(gcmsg.getMessage());//temp
                
                broadcastToDB(gcmsg);
                
                Integer chatId = gcmsg.getChatId();
                broadcastToGame( chatId, message, gameRepo);
            }


        } catch(Exception e) {
            logger.warn("JSON Parsing failed in on message");
            logger.info(e.toString());
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
    

    Integer userId = sessionuserIdMap.get(session);

    String message = userIdUsernameMap.get(userId) + " disconnected";
    sessionuserIdMap.remove(session);
    userIdSessionMap.remove(userId);
    userIdUsernameMap.remove(userId);
    
    
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
    logger.info("Entered into Error " + throwable.getMessage());
    
}

public boolean handleCommand(String message){
    boolean cmd = false;
    
    if(message.matches(GET_ACTIVE_USER_METHOD_PATTERN)){
        logger.debug("entered getActiveUsernameList if statement");
        String intString = message.substring("getActiveUsernameList() ".length(),message.length());
        try{
            
            Integer userId = Integer.parseInt(intString);
            logger.debug("sending to " + userId);
            
            String list = "{\"activeUsernameList\": " + gson.toJson(getActiveUsernameList()) + "}";
            sendMessageToPArticularUser(userId, list);
            cmd = true;
        } catch(Exception e){
            logger.debug(e.toString());
            
        }
        
        
    }
    
    return cmd;
}

public static List<String> getActiveUsernameList(){
    
    List<String> list = new ArrayList<String>(userIdUsernameMap.values());
    return list;
}

private void sendMessageToPArticularUser(Integer userId, String message) 
{	
    try {
        logger.info("entered send to particular, message:" + message);
        userIdSessionMap.get(userId).getBasicRemote().sendText(message);
    } catch (IOException e) {
        logger.info("Exception: " + e.getMessage());
        e.printStackTrace();
    }
}

private static void broadcast(String message) 
          throws IOException 
{	  
    
    //send to all sessions
    sessionuserIdMap.forEach((session, userId) -> {
        
        synchronized (session) {
            try {
                //session.getBasicRemote().sendText(gcmsg);
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
}

private static void broadcastToGame(Integer chatId, String jsonMsg, GameRepository gameRepo)
          throws IOException 
{
    log.info("entered game broadcast");
   
    try{

    Game game = gameRepo.findById(chatId).get(); //find game
    Map<Integer, User> players = game.getPlayers(); //get map from game keyed with userIds

    players.forEach((userId, user) ->{ // format ((key) userId,(Val) User object), players is of type Map<Integer, User>
        if(userIdSessionMap.containsKey(userId)) {  
            Session session = userIdSessionMap.get(userId);//use map to get session with userId
            synchronized (session) { //synchronize so messages are sent in FIFO
                try {
                    session.getBasicRemote().sendText(jsonMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
            }
        }
    });
} catch (Exception e) {
     log.info(e.toString());
    }

}

private void broadcastToDB(GameChatMessage gcmsg){
    
}



}