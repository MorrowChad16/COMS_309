package pickmeup.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * The interface User repository.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * Find by username ignore case user.
     *
     * @param username the username
     * @return the user
     */
    User findByUsernameIgnoreCase(String username);
    
    /**
     * Find by username or email all ignore case user.
     *
     * @param username the username
     * @param email    the email
     * @return the user
     */
    User findByUsernameOrEmailAllIgnoreCase(String username, String email);
    
    /**
     * Find by email ignore case user.
     *
     * @param email the email
     * @return the user
     */
    User findByEmailIgnoreCase(String email);

    List<User> findAll();

    User getOne(Integer id);
    
    /**
     * Find by id optional.
     *
     * @param <T>  the type parameter
     * @param id   the id
     * @param type the type
     * @return the optional
     */
    <T>  Optional<T> findById(Integer id, Class<T> type);
    
}

