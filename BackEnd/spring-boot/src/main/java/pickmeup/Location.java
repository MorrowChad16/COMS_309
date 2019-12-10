package pickmeup;

import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Location.
 */
@MappedSuperclass
@Getter @Setter @NoArgsConstructor
public class Location {
    
    //variables with exact name
    protected Double longt;
    protected Double lat;
    protected String name;
    
    /**
     * To string formatted string.
     *
     * @return the string
     */
    public String toStringFormatted() {
        String temp = "";
        if (name != null) {
            temp += name + ", ";
        }
        return temp + String.format(
                "%s, LatLong %f : %f",
                name, lat, longt);
    }

}