package pickmeup.gameLocations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The interface Game locations repository.
 */
public interface GameLocationsRepository extends JpaRepository<GameLocations, Integer> {
    
    /**
     * Finda all game locations.
     * @return game locations
     */
    List<GameLocations> findAll();
    GameLocations findByLatAndLongt(Double lat, Double longt);

}