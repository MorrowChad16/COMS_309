package pickmeup.game;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


/**
 * The interface Game repository.
 */
public interface GameRepository extends JpaRepository<Game, Integer> {

    /**
     * Find by sport list.
     *
     * @param sport the sport
     * @return the list games with the specified sport
     */
    List<Game> findBySportAndGameLocation_Id(String sport, Integer id, Sort sort);
    
    /**
     * Find by game location id list.
     *
     * @param id the id
     * @return the list games at game location
     */
    List<Game> findByGameLocation_Id(Integer id, Sort sort);

    List<Game> findByGameLocation_IdAndDateAfter(Integer id, Date afterDate, Sort sort);

    List<Game> findByGameLocation_IdAndDateAfterAndSport(Integer id, Date afterDate, String sport, Sort sort);
    
    List<Game> findByDateAfter(Date afterDate,  Sort sort);
   // @Query("SELECT id, (6371 * acos (cos(radians(:latitude)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:longitude))  + sin(radians(:latitude)) * sin(radians(latitude)))) AS distance FROM game HAVING distance < :distance ORDER BY distance)")
    
    @Query(value = "Select * from Game left outer join(" + 
        "select GameLocations.id," +
          "(6371 * " + 
          "acos ( " +
          " cos(radians(:latitude)) * " + 
         " cos(radians(GameLocations.lat)) * " +
        " cos( " +
        "radians(GameLocations.longt) - radians(:longitude) " +
        ")  + " +
        "sin(radians(:latitude)) * " +
        "sin(radians(GameLocations.lat)) " +
        ") " +
        " ) 'distance' FROM GameLocations HAVING 'distance' < :distance ORDER BY 'distance') GameLocations " +
        "on (GameLocations.id = Game.gameLocationId) where Game.date > :date", nativeQuery = true)  
    List<Game> findByDateAfterAndRadiusDistance(@Param("date") Date dateAfter, @Param("latitude") double lat, @Param("longitude") double longt, @Param("distance") double dist);
    
    @Query(value = "Select * from Game left outer join(" + 
    "select GameLocations.id," +
      "(6371 * " + 
      "acos ( " +
      " cos(radians(:latitude)) * " + 
     " cos(radians(GameLocations.lat)) * " +
    " cos( " +
    "radians(GameLocations.longt) - radians(:longitude) " +
    ")  + " +
    "sin(radians(:latitude)) * " +
    "sin(radians(GameLocations.lat)) " +
    ") " +
    " ) 'distance' FROM GameLocations HAVING 'distance' < :distance ORDER BY 'distance') GameLocations " +
    "on (GameLocations.id = Game.gameLocationId) where Game.date > :dateafter and Game.date < :datebefore", nativeQuery = true)  
List<Game> findByDateBetweenAndRadiusDistance(@Param("dateafter") Date dateAfter, @Param("datebefore") Date dateBefore, @Param("latitude") double lat, @Param("longitude") double longt, @Param("distance") double dist);


}