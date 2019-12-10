package pickmeup.userLocations;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pickmeup.Location;

/**
 * The type User locations.
 */
@Entity
@Table(name = "UserLocations")
@Getter @Setter @NoArgsConstructor 
public class UserLocations extends Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer UserLocationId;

    private Integer userId;

    
	public String toString() {
		return "UserLocations [UserLocationId=" + UserLocationId + ", userId=" + userId + ", lat=" + super.getLat() + ", long=" + super.getLongt() + ", name=" + super.getName() + "]";
	
    }


}