package pickmeup.gameLocations;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import pickmeup.Location;

import javax.persistence.*;

/**
 * The type Game locations used as an Entity for Hibernate mapping of Table.
 */
@Entity
@Table(name = "GameLocations")
@AllArgsConstructor
@NoArgsConstructor
public class GameLocations extends Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // variables that need to have format forced
    @Column(name = "addInfo")
    private String info; // check table for char length of string

    // private verified tinyint in msql
    @Column(name = "verified", columnDefinition = "TINYINT(1)")
    private boolean verified;

    /**
     * Gets game location id.
     *
     * @return the game location id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set game location id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets info.
     *
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets info.
     *
     * @param info the info
     */
    public void setInfo(@Nullable String info) {
        if (info == null) {
            this.info = "*";
            return;
        }

        this.info = info;
    }

    /**
     * Gets verified.
     *
     * @return the verified
     */
    public boolean getVerified() {
        return verified;
    }

    /**
     * Sets verified.
     *
     * @param verified the verified
     */
    public void setVerified(@Nullable Boolean verified) {
        if (verified == null) {
            this.verified = false;
            return;
        }

        this.verified = verified;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        GameLocations clone = new GameLocations();
        clone.id =this.id;
        clone.info = this.info;
        clone.lat = this.lat;
        clone.longt = this .longt;
        clone.name = this.name;
        clone.verified = this.verified;
        return (Object) clone();
    }

}
