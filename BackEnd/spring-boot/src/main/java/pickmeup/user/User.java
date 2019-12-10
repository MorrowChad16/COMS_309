package pickmeup.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;
import pickmeup.game.Game;
import pickmeup.userLocations.UserLocations;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type User.
 */
@Entity // This tells Hibernate to make a table out of this class
@Table(name = "User")
@DynamicUpdate
@Getter @Setter
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //leaves id handling to database
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    //@Column maps the variable to the name as typed in name="some_field_name", mysql queries are case-sensitive
  
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    @ColumnTransformer//@columnTransformer formats the query input & output, in this case it handles encryption  
            (read = "password", write = "SHA2(?,256)")//sets format to encrpyt
    private String password;

    @Column(name = "profileId")
    private Integer profileId;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "streetAddress")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "userId")
    private List<UserLocations> userLocations = new ArrayList<>();

    private Integer gid;

    //end private variables

    @JsonIgnore
    @ManyToMany(mappedBy = "players")
    private List<Game> games = new ArrayList<>();
    
    /**
     * This constructor copies the User given while removing whitespace and lowercasing username,firstname, lastname, and email. It also defaults the profileId to 1.
     *
     * @param otherUser User object.
     */
    public User(User otherUser) {
        this.username = otherUser.username;
        this.firstname = otherUser.firstname;
        this.lastname = otherUser.lastname;
        this.email = otherUser.email;
        this.profileId = 1;
        this.streetAddress = otherUser.streetAddress;
        this.city = otherUser.city;
        this.state = otherUser.state;
        this.zipcode = otherUser.zipcode;
        this.password = otherUser.password;
        this.phoneNumber = otherUser.phoneNumber;
        this.gid = otherUser.gid;
    }
    
    
    /**
     * Instantiates a new User.
     *
     * @param id the id
     */
    public User(Integer id) {
        this();
        this.id = id;

    }
    
    /**
     * Instantiates a new User.
     *
     * @param id       the id
     * @param username the username
     */
    public User(Integer id, String username){
        this(id);
        this.username = username;
    }

    //public get and set methods to edit tables, queries automated by Hibernate
    
    /**
     * Instantiates a new User.
     */
    public User() {
        this.username = null;
        this.firstname = null;
        this.lastname = null;
        this.email = null;
        this.profileId = 1;
        this.streetAddress = null;
        this.city = null;
        this.state = null;
        this.zipcode = null;
        this.password = null;
        this.phoneNumber = null;
        this.gid = 1;

    }
    
    
    /**
     * Add game.
     *
     * @param game the game
     */
    public void addGame(Game game){
        games.add(game);
    }
    
    /**
     * Remove game.
     *
     * @param game the game
     */
    public void removeGame(Game game){
        games.remove(game);
    }
    
    /**
     * Add user location.
     *
     * @param userLocation the user location
     */
    public void addUserLocation(UserLocations userLocation){
        userLocations.add(userLocation);
    }
    
    /**
     * Remove user location.
     *
     * @param userLocation the user location
     */
    public void removeUserLocation(UserLocations userLocation){
        userLocations.remove(userLocation);
    }

    /**
     * this method overrides to the toString() method of User to only output as formatted.
     * Used by findAll which calls toString() by iteration of List.
     */
    @Override
    public String toString() {
        return String.format(
                "{\"id\":\"%d\", \"username\":\"%s\"}",
                id, username, email);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User otherUser = (User) o;
            return Objects.equals(otherUser.id, this.id);
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    
public void  updateUser(User otherUser) {
    this.firstname = otherUser.firstname;
    this.lastname = otherUser.lastname;
    this.email = otherUser.email;
    this.profileId = 1;
    this.streetAddress = otherUser.streetAddress;
    this.city = otherUser.city;
    this.state = otherUser.state;
    this.zipcode = otherUser.zipcode;
    this.phoneNumber = otherUser.phoneNumber;
    this.gid = otherUser.gid;
}

}

