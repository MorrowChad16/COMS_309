package pickmeup.user;

import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pickmeup.game.Game;


/**
 * The type User controller.
 */
@Controller
@RequestMapping(path = "/user") // This means URL's start with /user (after Application path)
public class UserController {
    
    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserRepository userRepo;

    /**
     * this private method will search for username or email with the same argument login.
     *
     * @param login
     * @return
     */
    private User findLoginHelper(String login) {
        User usernameCheck = userRepo.findByUsernameIgnoreCase(login);
        User emailCheck = userRepo.findByEmailIgnoreCase(login);
        if (usernameCheck != null)
            return usernameCheck;
        else return emailCheck;
    }//end helper method
    
    /**
     * Create user string.
     *
     * @param input the input
     * @return the string
     */
    @PostMapping(path = "/createUser") // Map ONLY POST Requests
    public @ResponseBody
    String createUser(@RequestBody User input) {
        User newUser = null;
        // logger.debug("Entered Create User" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
        try {
            if (userRepo.findByUsernameOrEmailAllIgnoreCase(input.getUsername(), input.getEmail()) != null) { //add separate case for email
                return "0" + 0;
            }
            newUser = new User(input); //copy constructor, needs testing

            userRepo.save(newUser);
        } catch (NullPointerException a) {
            logger.debug(a.toString());
            return "Error: did you forget to include username, email, or password?";
        } catch (Exception e) {
            //  return "an error in create user: " + e.toString(); <--change to logger.debug
            logger.debug(e.toString());
            return "" + -1;
        }

        return "" + 1;
    }
    
    /**
     * Validate user string.
     *
     * @param input the input
     * @return 0 if an error occurs, 1 if username and password match, 2 if username does not exist, 3 if password does not match
     */
    @PostMapping(path = "/validateUser") // Map ONLY POST Requests
    public @ResponseBody
    String ValidateUser(@RequestBody User input) {
        String password;

        //  logger.debug("Entered Validate User"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()) + "\n");

        try {
            User curUser = findLoginHelper(input.getUsername());//username could actually be an email so use findByLogin()
            if (curUser == null) {
                return "" + 0;//no user found
            }

            password = DigestUtils.sha256Hex(input.getPassword());//common-codecs "sha2-256" tool used for encryption of sent password

            if (password.equals(curUser.getPassword())) {//match encrypted password on db
                LoggedInUser loggedInUser = new LoggedInUser(curUser);
                Gson gsonObj = new Gson();
                String jsonObject = gsonObj.toJson(loggedInUser);
                return "" + 1 + " " + jsonObject;//password and username matched
            }

        } catch (Exception e) {
            logger.debug(e.toString());
            return "" + -1;

        }

        return "" + 2;//password is incorrect
    }
    
    @PostMapping(path = "/changePassword/") // Map ONLY POST Requests
    public @ResponseBody
    String changePassword(@RequestBody PasswordChangeForm input) {
        String password;
        
        logger.debug("Entered changePassword"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()) + "\n");
        
        try {
            Optional<User> user = userRepo.findById(input.getUserId());
            
            if (!user.isPresent()) {
                return "" + 0;//no user found
            }
            
            User curUser = user.get();
            password = DigestUtils.sha256Hex(input.getOldPassword());//common-codecs "sha2-256" tool used for encryption of sent password
            
            if (password.equals(curUser.getPassword())) {//match encrypted password on db
                
                curUser.setPassword(input.getNewPassword());
                userRepo.save(curUser);
                return "" + 1;//new password set
            }
            
        } catch (Exception e) {
            logger.debug(e.toString());
            return "" + -1;
            
        }
        
        return "" + 0;//password is incorrect
    }
    
    
    /**
     * Validate user string.
     *
     * @param input the input
     * @return 0 if an error occurs, 1 if username and password match, 2 if username does not exist, 3 if password does not match
     */
    @PostMapping(path = "/updateUser") // Map ONLY POST Requests
    public @ResponseBody
    String updateUser(@RequestBody User input) {
        
        try {
            User curUser = userRepo.findById(input.getId()).get();
            if (curUser == null) {
                return "" + 0;//no user found
            }
            
            curUser.updateUser(input); //update through method
            userRepo.save(curUser);


            return "" + 1;

        } catch (Exception e) {
            logger.debug(e.toString());
            return "" + -1;

        }
    }

    
    /**
     * Gets all users.
     *
     * @return the all users
     */
    @GetMapping(path = "/all")
    public @ResponseBody
    String getAllUsers() {
        // This returns a JSON or XML with the users
        try {
            List<User> userList = userRepo.findAll();

            String userListJSON = "{\"Users\": " + userList.toString() + "}"; //returns json array in json object

            return userListJSON;
        } catch (Exception e) {
            logger.debug(e.toString());
            return "-1 error: " + e.toString();
        }

    }
    
    /**
     * Find by email string.
     *
     * @param email the email
     * @return the string
     */
    @GetMapping(path = "/findByEmail/{email}")
    public @ResponseBody
    String findByEmail(@PathVariable String email) {
        // This returns a JSON or XML with the users
        try {
            if (userRepo.findByEmailIgnoreCase(email) != null)
                return "" + 1;
        } catch (Exception e) {
            logger.debug(e.toString());
        }
        return "" + 0;
    }
    
    /**
     * Find by username string.
     *
     * @param username the username
     * @return the string
     */
    @GetMapping(path = "/findByUsername/{username}")
    public @ResponseBody
    String findByUsername(@PathVariable String username) {
        // This returns a JSON or XML with the users
        try {
            if (userRepo.findByUsernameIgnoreCase(username) != null)
                return "" + 1;
        } catch (Exception e) {
            logger.debug(e.toString());
        }
        return "" + 0;
    }
    
    /**
     * Findy game users by id string.
     *
     * @param id the id
     * @return the string
     */
    @GetMapping(path = "/findGameRecordsByUserId/{id}")
    public @ResponseBody
    String findyGameUsersById(@PathVariable Integer id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            try {

                ExclusionStrategy strategy = new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        if (f.getDeclaringClass() == Game.class ){
                                return false;
                        }
                        else if ((f.getDeclaringClass() == User.class && !f.getName().equals("username"))
                                 && ( f.getDeclaringClass() == User.class && !f.getName().equals("id"))) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }

                    
                };
                Gson gson = new GsonBuilder().serializeNulls()
                .addSerializationExclusionStrategy(strategy)
                .create();
        
                List<Game> games = user.get().getGames();
                //sort records
                Collections.sort(games, 
                Collections.reverseOrder( new Comparator<Game>() {
                    public int compare(Game o1, Game o2) {
                        int comp = 0;
                        comp =o1.getDate().compareTo(o2.getDate());
                        if (comp == 0){
                           comp = o1.getTime().compareTo(o2.getTime());
                        }
                        return comp;
                        }
                    }
                 ) 
                  );

                String jsonObj = gson.toJson(games);


            String gameObj = "{\"User " + id + " Games\": \n" + jsonObj + "}\n"; //returns json array in json object
            return gameObj;

            } catch (Exception e) {
                logger.debug(e.getMessage());
                return "{\"User Games\": null}"; //"{\"games\": \n" + jsonObj + "}\n"
            }

        } else
            return "[]";

    }
    
    @GetMapping(path = "/findChatInfoByUserId/{id}")
    public @ResponseBody
    String findChatInfoById(@PathVariable Integer id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            try {
                 Gson gson = new GsonBuilder().serializeNulls().create();
            
                List<Game> games = user.get().getGames();
                List<ChatInfo> chatInfoList = new ArrayList<>();
               games.forEach(game ->
                    chatInfoList.add(new ChatInfo(game.getGameLocation().getName(),(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(game.getDate()), game.getGameId(), game.getSport()))
               );
                
                String jsonObj = gson.toJson(chatInfoList);
            
            
                String gameObj = "{\"ChatInfoList\": \n" + jsonObj + "}\n"; //returns json array in json object
                return gameObj;
            
            } catch (Exception e) {
                logger.debug(e.getMessage());
                return "{\"ChatInfoList\": null}";
            }
        
        } else
            return "{\"ChatInfoList\": null}";
    
    }
    
    
    
    /**
     * Count string.
     *
     * @return the string
     */
    @GetMapping(path = "/count")
    public @ResponseBody
    String count() {
        return "users in tables " + userRepo.count();
    }
    
    /**
     * Testme string.
     *
     * @return the string
     */
    @GetMapping(path = "/testme")
    public @ResponseBody
    String testme() {
        return "server UP!";
    }
}