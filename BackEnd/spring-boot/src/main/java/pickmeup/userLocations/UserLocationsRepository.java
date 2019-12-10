package pickmeup.userLocations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The interface User locations repository.
 */
public interface UserLocationsRepository extends JpaRepository<UserLocations, Integer> {

    List<UserLocations> findAll();
    
    /**
     * Find by name and user id user locations.
     *
     * @param name   the name
     * @param userId the user id
     * @return the user locations
     */
    UserLocations findByNameAndUserId(String name, Integer userId);

}