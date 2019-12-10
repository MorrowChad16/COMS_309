package pickmeup.webSockets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import pickmeup.gameLocations.GameLocations;
import pickmeup.gameLocations.GameLocationsController;
import pickmeup.user.UserRepository;
import pickmeup.webSockets.config.CustomConfigurator;

@ServerEndpoint(value = "/adminLocationSocket/{userId}", configurator = CustomConfigurator.class) 
@Component
public class AdminLocationSocket {

    // Store all socket session and their corresponding userId.
private static Map<Session, Integer> sessionuserIdMap = new HashMap<>();
private static Map<Integer, Session> userIdSessionMap = new HashMap<>();
private static Map<Integer, String> userIdUsernameMap = new HashMap<>();

private static String simpleDateFormatJSON = "yyyy-MM-dd'T'HH:mm:ss";
private static Gson gson = new GsonBuilder()
.setDateFormat(simpleDateFormatJSON)
.create();
 //for auto converting from json
private final Logger logger = LoggerFactory.getLogger(AdminLocationSocket.class);
private static final String GAME_LOCATION_METHOD_PATTERN = "^([-_a-zA-Z0-9]+\\(([-_ a-zA-Z0-9]*)\\))" + "( \\{.*\\})?$";

@Autowired
private GameLocationsController gameLocationController;

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
public void onOpen(Session session, @PathParam("userId") Integer userId) throws IOException {
    try{
        logger.info("Entered into Open");

        //add user to chats
        sessionuserIdMap.put(session, userId);
        userIdSessionMap.put(userId, session);
        

        //get username
        String username = null;
    
        username = userRepo.findById(userId).get().getUsername();
        userIdUsernameMap.put(userId , username);
        //send initial message
         broadcast("User:" + username + " is active");
    } catch(Exception e) {
        logger.info(e.toString());
    }


    
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
public void onMessage(Session session, String message) throws IOException {
    // Handle new messages
    logger.info("Entered into Message: Got Message:" + message);
    
        try{
            Integer userId =sessionuserIdMap.get(session);
            broadcast(message);
            handleCommand(message, userId);
        } catch(Exception e) {
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
    // Using a StringWriter, 
    // to convert trace into a String: 
    StringWriter sw = new StringWriter(); 

    // create a PrintWriter 
    PrintWriter pw = new PrintWriter(sw); 
    throwable.printStackTrace(pw); 
  
    String error = sw.toString(); 
    // Do error handling here
    logger.info("Entered into Error " + "stackTrace:"+ error +" throwable: " + throwable.toString());
    
}

public boolean handleCommand(String message,Integer userId){
    boolean cmd = false;
     String broadcastMessage = "";

    Pattern p = Pattern.compile(GAME_LOCATION_METHOD_PATTERN);
    Matcher m = p.matcher(message);

    if(m.find()){
        logger.debug("in LOCATION method using pattern " + GAME_LOCATION_METHOD_PATTERN);
        
        String method = m.group(1);
        String args = m.group(2);
        String json = m.group(3);
        logger.debug("found " + method + " " + args + " " + json);
        switch(method){
            case "count()":
                broadcastMessage = "count() " + gameLocationController.count();
                break;
            case "all()": 
                broadcastMessage = "all() " + gameLocationController.getAllGameLoc();
                break;
            case "updateCourt()":
                logger.debug("updateCourt", "updatingCourt:"+ json);
                GameLocations loc = gson.fromJson(json, GameLocations.class);
                gameLocationController.updateLocation(loc);
                break;
            case "deleteCourt()":
                logger.debug("DeleteCourt", "deletingCourt:" + json);
                GameLocations deleteLocation = gson.fromJson(json, GameLocations.class);
                gameLocationController.deleteLocation(deleteLocation);
                break;
            default: logger.debug("entered switch case statement " + method + " " + args + " " + json);
                }

            cmd = true;
            logger.debug("sending to " + userIdUsernameMap.get(userId));
            // String list = "{\"activeUsernameList\": " + gson.toJson(getActiveUsernameList()) + "}";
            sendMessageToPArticularUser(userId, broadcastMessage);
    
    }

    
    return cmd;
}

private void sendMessageToPArticularUser(Integer userId, String message) 
{	
    try {
        logger.info("entered send to particular");
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
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });
}
   
}