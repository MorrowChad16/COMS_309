package pickmeup.userLocations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pickmeup.user.User;
import pickmeup.user.UserRepository;

/**
 * The type User locations controller.
 */
@Controller
@RequestMapping(path = "/userLocations")
public class UserLocationsController {
    
    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(UserLocationsController.class);
   
    @Autowired
    private UserLocationsRepository userLocationsRepo;
    @Autowired
    private UserRepository userRepo;
    
    
    /**
     * Add location string.
     *
     * @param userLocation the user location
     * @return the string
     */
    @PostMapping(path ="/addLocation")
    public @ResponseBody
    String addLocation(@RequestBody UserLocations userLocation){
       try{
            User user = userRepo.getOne(userLocation.getUserId());
           UserLocations existing = userLocationsRepo.findByNameAndUserId(userLocation.getName(), userLocation.getUserId());
           
           if(existing != null) {
                return "0";
           }

            UserLocations savedLocation = userLocationsRepo.save(userLocation);
            user.addUserLocation(savedLocation);
       } catch (Exception e){
           logger.error(e.toString());
           return "-1";
       }
       

        return "" + 1;

    }

    /**
     * Add location string.
     *
     * @param userLocation the user location
     * @return the string
     */
    @PostMapping(path ="/removeLocation")
    public @ResponseBody
    String removeLocation(@RequestBody Integer userLocationId){
       try{
           Optional<UserLocations> existing = userLocationsRepo.findById(userLocationId);
           
           if(!existing.isPresent()) {
                return "0";
           }

            userLocationsRepo.delete(existing.get());
       } catch (Exception e){
           logger.error(e.toString());
           return "-1";
       }
       

        return "" + 1;

    }
    
    /**
     * Find by user id string.
     *
     * @param userId the user id
     * @return the string
     */
    @GetMapping(path = "/findByUserId/{userId}")
    public @ResponseBody
    String findByUserId(@PathVariable Integer userId){
        try{
            User user = userRepo.findById(userId).get();
            
           if(user == null) {
                return "0";
           }

           return user.getUserLocations().toString();

       } catch (Exception e){
           logger.error(e.toString());
           return "-1";
       }
    }
    
    /**
     * Find by user locations name string.
     *
     * @param name the name
     * @param id   the id
     * @return the string
     */
    @PostMapping(path = "/findByUserLocationsName")
    public @ResponseBody
    String findByUserLocationsName(@RequestBody String name, @RequestBody Integer id) {
        // This returns a JSON or XML with the users
        try {
            UserLocations userLocations = userLocationsRepo.findByNameAndUserId(name, id);

            return userLocations.toString();
        } catch (Exception e) {
            logger.debug(e.toString());
            return "-1 error: " + e.toString();
        }

    }
    
    /**
     * Gets all user locations.
     *
     * @return the all user locations
     */
    @GetMapping(path = "/all")
    public @ResponseBody
    String getAllUserLocations() {
        // This returns a JSON or XML with the users
        try {
            List<UserLocations> userLocations = userLocationsRepo.findAll();

            String userLocationsJSON = "{\"User Locations\": " + Arrays.toString(userLocations.toArray()) + "}"; //returns json array in json object

            return userLocationsJSON;
        } catch (Exception e) {
            logger.debug(e.toString());
            return "-1 error: " + e.toString();
        }

    }

}