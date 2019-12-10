package pickmeup.gameLocations;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**
 * The type Game locations controller.
 */
@Controller
@RequestMapping(path = "/gameLocations") // This means URL's start with /user (after Application path)
public class GameLocationsController {
    
    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(GameLocations.class);
    @Autowired
    private GameLocationsRepository gLocRepo;
    
    /**
     * Add new location.
     *
     * @param newLoc the new loc
     * @return the string
     */
    @PostMapping(path = "/addGameLocation")
    public @ResponseBody
    String addNewLoc(@RequestBody GameLocations newLoc) {
        try {
            newLoc.setInfo(newLoc.getInfo());
            newLoc.setVerified(newLoc.getVerified());

            gLocRepo.save(newLoc);
            return "" + 1 + " " + newLoc.getId(); //duplicate location check done on clientside, always add gameLocations
        } catch (Exception e) {
            logger.debug(e.toString());
            return "0 - error: " + e.toString();
        }

    }

     /**
     * update location.
     *
     * @param updatedLoc updated location
     * @return the string
     */
    @PostMapping(path = "/updateGameLocation")
    public @ResponseBody
    String updateLocation(@RequestBody GameLocations updatedLoc) {
        try {
            logger.debug("toUpdate:" + updatedLoc.getId());
            gLocRepo.save(updatedLoc);
            return "" + 1; //updated
        } catch (Exception e) {
            logger.debug(e.toString());
            return "0 - error: " + e.toString();
        }

    }


    public String deleteLocation(GameLocations deleteLocation) {
        try {      
            gLocRepo.deleteById(deleteLocation.getId());
            return "" + 1; //deleted

        } catch (Exception e) {
            logger.debug(e.toString());
            return "0 - error: " + e.toString();
        }

    }
    
    /**
     * Find by id.
     *
     * @param id the id
     * @return the game locations
     */
    @GetMapping(path = "/findById/{id}")
    public @ResponseBody
    GameLocations findById(@PathVariable Integer id) {
        Optional<GameLocations> gameLocation = gLocRepo.findById(id);
        if (gameLocation.isPresent()) {
            return gameLocation.get();
        } else
            return null;

    }

    /**
     * Find by coordinates
     *
     * @param lat latitude
     * @param longt longitude
     * @return the game locations
     */
    @GetMapping(path = "/findIdByCoordinates/{lat}/{longt}")
    public @ResponseBody
    GameLocations findIdByName(@PathVariable Double lat, @PathVariable Double longt) {
        GameLocations gameLocation = gLocRepo.findByLatAndLongt(lat, longt);
        if (gameLocation != null) {
            return gameLocation;
        } else
            return null;

    }
    
    /**
     * Gets all game locations.
     *
     * @return the all game locations
     */
    @GetMapping(path = "/all")
    public @ResponseBody
    String getAllGameLoc() {
        try {
            List<GameLocations> locList = gLocRepo.findAll();
            Gson gson = new Gson();
            String jsonObj = gson.toJson(locList);

            String locObj = "{\"locations\":" + jsonObj + "}"; //returns json array in json object

            return locObj;
        } catch (Exception e) {
            logger.debug(e.toString());
            return "error: " + e.toString();
        }
    }
    
    /**
     * Print all.
     *
     * @return the string
     */
    @GetMapping(path = "/printAll")
    public @ResponseBody
    String printAll() {
        String all = "";
        List<GameLocations> it = gLocRepo.findAll();

        for (GameLocations i : it) {
            all += "\n" + i.toStringFormatted();
        }
        return all;
    }
    
    /**
     * Count.
     *
     * @return the string
     */
    @GetMapping(path = "/count")
    public @ResponseBody
    String count() {
        return "game locations count: " + gLocRepo.count();
    }


}