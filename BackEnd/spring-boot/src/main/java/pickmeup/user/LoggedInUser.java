package pickmeup.user;

import pickmeup.userLocations.UserLocations;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Logged in user.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoggedInUser {


    private Integer id;

    private String username;

    private String email;

    private Integer profileId;

    private String firstname;

    private String lastname;

    private String streetAddress;

    private String city;

    private String state;

    private String zipcode;

    private String phoneNumber;

    private List<UserLocations> userLocations = new ArrayList<>();

    private Integer gid;

    //end private variables

    //public get and set methods to edit tables, queries automated by Hibernate
    
    /**
     * This constructor copies the User given. It also defaults the profileId to 1.
     *
     * @param otherUser User object.
     */
    public LoggedInUser(User otherUser) {
        this.id = otherUser.getId();
        this.username = otherUser.getUsername();
        this.firstname = otherUser.getFirstname();
        this.lastname = otherUser.getLastname();
        this.email = otherUser.getEmail();
        this.profileId = otherUser.getProfileId();
        this.streetAddress = otherUser.getStreetAddress();
        this.city = otherUser.getCity();
        this.state = otherUser.getState();
        this.zipcode = otherUser.getZipcode();
        this.phoneNumber = otherUser.getPhoneNumber();
        this.userLocations = new ArrayList<>(otherUser.getUserLocations());
        this.gid = otherUser.getGid(); 
    }

    /**
     * this method overrides to the toString() method of User to only output as formatted.
     * @return id, username, emailin JSON format
     */
    @Override
    public String toString() {
        return String.format(
                "{\"id\":\"%d\", \"username\":\"%s\", \"email\":\"%s\"}",
                id, username, email);
    }

}

