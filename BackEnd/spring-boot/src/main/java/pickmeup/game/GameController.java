package pickmeup.game;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pickmeup.gameLocations.GameLocationsRepository;
import pickmeup.user.User;
import pickmeup.user.UserRepository;

/**
 * The type Game controller.
 */
@Controller
@RequestMapping(path = "/game") 
public class GameController {
    
    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(GameController.class);
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired 
    private GameLocationsRepository gameLocationsRepository;
    
    
    
    /**
     * Add new game.
     *
     * @param newGame        the new game
     * @param gameLocationId the game location id
     * @return the int code 1 if created, else 0
     */
    @PostMapping(path = "/addGame/{gameLocationId}")
    public @ResponseBody String addNewGame(@RequestBody Game newGame, @PathVariable Integer gameLocationId) {
        try {
            Game game = new Game();
            game.setGameLocation(gameLocationsRepository.getOne(gameLocationId)); 
           
            Calendar time = Calendar.getInstance();
            
            time.setTime(newGame.getTime()); //set calendar with time

            Calendar c = Calendar.getInstance(); 
            c.setTime(newGame.getDate()); //set calendar wih date
            //c.add(Calendar.DAY_OF_MONTH, 1);

            //set calendar with date to the time calendar's Hour, Min, Sec, MS
            c.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            c.set(Calendar.SECOND, time.get(Calendar.SECOND));
            c.set(Calendar.MILLISECOND, time.get(Calendar.MILLISECOND));
            
            //update newGame's date variable with date and time combined
            Date newDate = c.getTime();
            newGame.getDate().setTime(newDate.getTime());
            logger.debug(newGame.getDate().toString());

            game.setStatus(newGame.getStatus());
            game.setTime(newGame.isTime());
            game.setDate(newGame.getDate());
            game.setPMax(newGame.getPMax());
            game.setSport(newGame.getSport());
            game.setTeam1Count(newGame.getTeam1Count());
            game.setTeam2Count(newGame.getTeam2Count());           
           gameRepo.save(game);
            
            logger.debug("Added game " + game.getGameId() + " " + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
            
                    return "" + 1 + " " + game.getGameId();

        } catch (Exception e) {
            logger.debug("AddGame Caught: " + e.toString());
            return "0 - debug: " + e.toString();
        }
    }
    
    /**
     * Add team 1 player.
     *
     * @param json the json string, with gameId and userId
     * @return the boolean, true if player added, else false
     */
    @PostMapping(path = "/addTeam1Player")
    public @ResponseBody synchronized Boolean addTeam1Player(@RequestBody String json) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
           // logger.debug("Entered add player " + jsonObj.toString());
           
            Optional<Game> g = gameRepo.findById(jsonObj.get("gameId").getAsInt());
            Game game = null;
            if (g.isPresent()) {
                game = g.get();
            } else {
                return false;
            }

            if (game.isTeam1Full()) {
                return false;
            }
            User player = userRepo.getOne(jsonObj.get("userId").getAsInt());
            game.addTeam1Player(player);
            
            gameRepo.save(game);
           
           
           


            return true;
        } catch (Exception e) {
            logger.debug(e.toString());
            return false;
        }
    }
    
    /**
     * Remove player.
     *
     * @param json the json string, with gameId and userId
     * @return the boolean, true if player removed, else false
     */
    @PostMapping(path = "/removePlayer")
    public @ResponseBody synchronized Boolean removePlayer(@RequestBody String json) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
           logger.debug("Entered remove player " + jsonObj.toString());

            Optional<Game> g = gameRepo.findById(jsonObj.get("gameId").getAsInt());
            Game game = null;
            if (g.isPresent()) {
                game = g.get();
            } else {
                return false;
            }

            User player = userRepo.getOne(jsonObj.get("userId").getAsInt());
            game.removeTeamPlayer(player);
           
            gameRepo.save(game);

            return true;
        } catch (Exception e) {
            logger.debug(e.toString());
            return false;
        }
    }
    
    /**
     * Add team 2 player.
     *
     * @param json the json string, with gameId and userId
     * @return the boolean, true if added, else false
     */
    @PostMapping(path = "/addTeam2Player")
    public @ResponseBody synchronized Boolean addTeam2Player(@RequestBody String json) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
          //  logger.debug("Entered add player 2 " + jsonObj.toString());

            Optional<Game> g = gameRepo.findById(jsonObj.get("gameId").getAsInt());
            Game game = null;
            if (g.isPresent()) {
                game = g.get();
            } else {
                return false;
            }

            if (game.isTeam2Full()) {
                return false;
            }
            
            User player = userRepo.getOne(jsonObj.get("userId").getAsInt());
            game.addTeam2Player(player);

            gameRepo.save(game);
           

            return true;
        } catch (Exception e) {
            logger.debug(e.toString());
            return false;
        }
    }
    
   /**
    * Upcoming games string by game location id.
    *
    * @param id the id
     * @return the json object string with list of all games at location
    */
   @GetMapping(path = {"/findByGameLocationId/{id}/{sport}", "/findByGameLocationId/{id}"})
   public @ResponseBody 
   String findUpcomingGamesByGameLocationId(@PathVariable("id") Integer id, @PathVariable(name = "sport", required = false) Optional<String> sport) {
        try {
            // get a calendar instance, which defaults to "now"
            Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();
            date.setTime((date.getTime() - 300000)); //-5 minutes
            
            //logger.debug("filter date " + date.toString());
            
            List<Game> gameList = null;
            if(!sport.isPresent() || sport.get().equals("all")){
                gameList = gameRepo.findByGameLocation_IdAndDateAfter(id, date,
                    Sort.by(Sort.Direction.ASC, "date").and(Sort.by(Sort.Direction.ASC, "time")));
            }
            else {
                gameList = gameRepo.findByGameLocation_IdAndDateAfterAndSport(id, date, sport.get(),
                    Sort.by(Sort.Direction.ASC, "date").and(Sort.by(Sort.Direction.ASC, "time")));
            }
            

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

            String jsonObj = gson.toJson(gameList);

            String gameObj = "{\"games\": \n" + jsonObj + "}\n"; //returns json array in json object

            return gameObj;

        } catch (Exception e) {
            logger.debug(e.toString());
            return "debug: " + e.toString();
        }
    }
    
    /**
     * Find by id.
     *
     * @param id the game id
     * @return the Game object as a string
     */
    @GetMapping(path = "/findByGameId/{id}")
    public @ResponseBody 
    String findById(@PathVariable Integer id) {
        Optional<Game> game = gameRepo.findById(id);
        if (game.isPresent()) {
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
                String jsonObj = gson.toJson(game.get());


                return jsonObj;
            } catch (Exception e) {
                logger.debug(e.getMessage());
                return null;
            }

        } else
            return null;

    }
    
    /**
     * Find game players by game id.
     *
     * @param id the id
     * @return the Json string of players
     */
    @GetMapping(path = "/findGameUsersByGameId/{id}")
    public @ResponseBody
    String findGamePlayersById(@PathVariable Integer id) {
        Optional<Game> game = gameRepo.findById(id);
        if (game.isPresent()) {
            try {
                return game.get().getPlayers().toString();
            } catch (Exception e) {
                logger.debug(e.getMessage());
                return "[]";
            }

        } else
            return "[]";

    }
    
    /**
     * Find all games by game location id.
     *
     * @param id the id
     * @return the json object string with list of all games at location
     */
    @GetMapping(path = "/findAllGamesByGameLocationId/{id}") //find By gameLocationId
    public @ResponseBody
    String findByGameLocationId(@PathVariable Integer id) {
        try {
            List<Game> gameList = gameRepo.findByGameLocation_Id(id, Sort.by(Sort.Direction.ASC, "date").and(Sort.by(Sort.Direction.ASC, "time")));

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

            String jsonObj = gson.toJson(gameList);

            String gameObj = "{\"games\": \n" + jsonObj + "}\n"; //returns json array in json object

            return gameObj;

        } catch (Exception e) {
            logger.debug(e.toString());
            return "debug: " + e.toString();
        }
    }
    
    /**
     * Gets all games.
     *
     * @return the json object string of all games
     */
    @GetMapping(path = "/all")
    public @ResponseBody
    String getAllGames() {
        try {
            List<Game> gameList = gameRepo.findAll(Sort.by(Sort.Direction.ASC, "date").and(Sort.by(Sort.Direction.ASC, "time")));
           
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
            Gson gson = new GsonBuilder()
            .addSerializationExclusionStrategy(strategy)
            .create();

            String jsonObj = gson.toJson(gameList);
            
            String gameObj = "{\"games\": \n" + jsonObj + "}\n"; //returns json array in json object
            return gameObj;
        } catch (Exception e) {
            logger.debug(e.toString());
            return null; //"debug: " + e.toString();
        }
    }


    /**
    *  All Upcoming games within a radius of location.
    *
    * @param id the id
     * @return the json object string with list of all games at location
    */
   @GetMapping(path = "/all/guest/")
   public @ResponseBody 
   String findAllUpcomingGames(@RequestParam(name="lat") Double lat, @RequestParam(name="longt") Double longt, @RequestParam(name="minDate") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date minDate, @RequestParam(name="maxDate") @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") Date maxDate) {
        try {

            //GeoLocation info
            double dist = 10; //search radius
            if(lat == 0 | longt == 0){ //default MU "42.023573,-93.646282"
                lat = 42.023573;
                longt = -93.646282;
            }
            logger.debug(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new java.util.Date()) + 
            " filter dates " + minDate + " --> " + maxDate +"\n lat&longt:" + lat +";"+longt);
            // params(afterDate, Lat, Long, radius Distance(mi), sort)
            List<Game> gameList = gameRepo.findByDateBetweenAndRadiusDistance(minDate, maxDate, lat, longt, dist
               ); 

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

            String jsonObj = gson.toJson(gameList);

            String gameObj = "{\"games\": \n" + jsonObj + "}\n"; //returns json array in json object

            return gameObj;

        } catch (Exception e) {
            logger.debug(e.toString());
            return "debug: " + e.toString();
        }
    }
    
    
    /**
     * Count.
     *
     * @return the string with counted games
     */
    @GetMapping(path = "/count")
    public @ResponseBody
    String count() {
        return "game games count: " + gameRepo.count();
    }

}