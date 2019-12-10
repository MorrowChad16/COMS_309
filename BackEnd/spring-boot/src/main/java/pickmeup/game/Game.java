package pickmeup.game;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;

import org.springframework.lang.Nullable;

import pickmeup.gameLocations.GameLocations;
import pickmeup.user.User;

/**
 * The type Game used as an Entity for Hibernate mapping of Table.
 */
@Entity
@Table(name = "Game")
public class Game {
    

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer gameId;    


    @Column(name = "chatId")
    private Integer chatId;
    
    
    @Column(name = "date")
    private Date date;
    
    

    @ManyToOne
    @JoinColumn(name="gameLocationId")
    private GameLocations gameLocation;
    
    
    @Column(name = "p10Id")
    private Integer p10Id;
    
    
    @Column(name = "p1Id")
    private Integer p1Id;
    
    
    @Column(name = "p2Id")
    private Integer p2Id;
    
    
    @Column(name = "p3Id")
    private Integer p3Id;
    
    
    @Column(name = "p4Id")
    private Integer p4Id;
    
    
    @Column(name = "p5Id")
    private Integer p5Id;
    
    
    @Column(name = "p6Id")
    private Integer p6Id;
    
    
    @Column(name = "p7Id")
    private Integer p7Id;
    
    
    @Column(name = "p8Id")
    private Integer p8Id;
    
    
    @Column(name = "p9Id")
    private Integer p9Id;
    
    
    @Column(name = "pMax")
    private Integer pMax;
    
    
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "Game_User", joinColumns = {@JoinColumn(name = "game_id")}, inverseJoinColumns = {
            @JoinColumn(name = "user_id")})
    @MapKey(name="id")
    private Map<Integer, User> players = new HashMap<Integer, User>();

    @Column(name = "score1")
    private String score1;
    
    
    @Column(name = "score2")
    private String score2;
    
    
    @Column(name = "sport")
    private String sport;
    
    
    @Column(name = "status")
    private Integer status;
    
    
    @Column(name = "team1Count")
    private Integer team1Count;
    
    
    @Column(name = "team2Count")
    private Integer team2Count;
    
    
    private Time time;
    
    
    /**
     * Gets game id.
     *
     * @return the game id
     */
    public Integer getGameId() {
        
        return gameId;
    }
    
    /**
     * Sets game id.
     *
     * @param gameId the game id
     */
    public void setGameId( Integer gameId ) {
        
        this.gameId = gameId;
    }
    
    /**
     * Add player.
     *
     * @param player the player
     */
    public void addPlayer( User player ) {
        if (players.containsValue(player) ) {
        //if ( players.contains(player) ) {
            throw new IllegalArgumentException("player exists");
        }
        //players.add(player);
        players.putIfAbsent(player.getId(), player);
        player.addGame(this);
    }
    
    /**
     * Add team 1 player.
     *
     * @param player the player
     */
    public void addTeam1Player( User player ) {
        
        if ( isTeam1Full() ) {
            throw new IllegalArgumentException();
        }
        
        if ( p1Id == null ) {
            p1Id = player.getId();
        } else if ( p2Id == null ) {
            p2Id = player.getId();
        } else if ( p3Id == null ) {
            p3Id = player.getId();
        } else if ( p4Id == null ) {
            p4Id = player.getId();
        } else {
            p5Id = player.getId();
        }
        
        addPlayer(player);
        team1Count++;
    }
    
    /**
     * Add team 2 player.
     *
     * @param player the player
     */
    public void addTeam2Player( User player ) {
        
        if ( isTeam2Full() ) {
            throw new IllegalArgumentException();
        }
        
        if ( p6Id == null ) {
            p6Id = player.getId();
        } else if ( p7Id == null ) {
            p7Id = player.getId();
        } else if ( p8Id == null ) {
            p8Id = player.getId();
        } else if ( p9Id == null ) {
            p9Id = player.getId();
        } else {
            p10Id = player.getId();
        }
        
        addPlayer(player);
        team2Count++;
    }
    
    /**
     * Gets chat id.
     *
     * @return the chat id
     */
    public Integer getChatId() {
        
        return chatId;
    }
    
    /**
     * Sets chat id.
     *
     * @param chatId the chat id
     */
    public void setChatId( Integer chatId ) {
        
        this.chatId = chatId;
    }
    
    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() {
        
        return date;
    }
    
    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate( Date date ) {
        
        this.date = date;
    }
    
    /**
     * Gets player 10 id.
     *
     * @return the player 10 id
     */
    public Integer getP10Id() {
        
        return p10Id;
    }
    
    /**
     * Sets player 10 id.
     *
     * @param p10Id the player 10 id
     */
    public void setP10Id( Integer p10Id ) {
        
        this.p10Id = p10Id;
    }
    
    /**
     * Gets player 1 id.
     *
     * @return the player 1 id
     */
    public Integer getP1Id() {
        
        return p1Id;
    }
    
    /**
     * Sets player 1 id.
     *
     * @param p1Id the player 1 id
     */
    public void setP1Id( Integer p1Id ) {
        
        this.p1Id = p1Id;
    }
    
    /**
     * Gets player 2 id.
     *
     * @return the player 2 id
     */
    public Integer getP2Id() {
        
        return p2Id;
    }
    
    /**
     * Sets player 2 id.
     *
     * @param p2Id the player 2 id
     */
    public void setP2Id( Integer p2Id ) {
        
        this.p2Id = p2Id;
    }
    
    /**
     * Gets player 3 id.
     *
     * @return the player 3 id
     */
    public Integer getP3Id() {
        
        return p3Id;
    }
    
    /**
     * Sets player 3 id.
     *
     * @param p3Id the player 3 id
     */
    public void setP3Id( Integer p3Id ) {
        
        this.p3Id = p3Id;
    }
    
    /**
     * Gets player 4 id.
     *
     * @return the player 4 id
     */
    public Integer getP4Id() {
        
        return p4Id;
    }
    
    /**
     * Sets player 4 id.
     *
     * @param p4Id the player 4 id
     */
    public void setP4Id( Integer p4Id ) {
        
        this.p4Id = p4Id;
    }
    
    /**
     * Gets player 5 id.
     *
     * @return the player 5 id
     */
    public Integer getP5Id() {
        
        return p5Id;
    }
    
    /**
     * Sets player 5 id.
     *
     * @param p5Id the player 5 id
     */
    public void setP5Id( Integer p5Id ) {
        
        this.p5Id = p5Id;
    }
    
    /**
     * Gets player 6 id.
     *
     * @return the player 6 id
     */
    public Integer getP6Id() {
        
        return p6Id;
    }
    
    /**
     * Sets player 6 id.
     *
     * @param p6Id the player 6 id
     */
    public void setP6Id( Integer p6Id ) {
        
        this.p6Id = p6Id;
    }
    
    /**
     * Gets player 7 id.
     *
     * @return the player 7 id
     */
    public Integer getP7Id() {
        
        return p7Id;
    }
    
    /**
     * Sets player 7 id.
     *
     * @param p7Id the player 7 id
     */
    public void setP7Id( Integer p7Id ) {
        
        this.p7Id = p7Id;
    }
    
    /**
     * Gets player 8 id.
     *
     * @return the player 8 id
     */
    public Integer getP8Id() {
        
        return p8Id;
    }
    
    /**
     * Sets player 8 id.
     *
     * @param p8Id the player 8 id
     */
    public void setP8Id( Integer p8Id ) {
        
        this.p8Id = p8Id;
    }
    
    /**
     * Gets player 9 id.
     *
     * @return the player 9 id
     */
    public Integer getP9Id() {
        
        return p9Id;
    }
    
    /**
     * Sets player 9 id.
     *
     * @param p9Id the player 9 id
     */
    public void setP9Id( Integer p9Id ) {
        
        this.p9Id = p9Id;
    }
    
    /**
     * Gets max amount of players.
     *
     * @return the player max
     */
    public Integer getPMax() {
        
        return pMax;
    }
    
    /**
     * Sets max amount of players.
     *
     * @param pMax the player max
     */
    public void setPMax( @Nullable Integer pMax ) {
        
        if ( pMax == null ) {
            this.pMax = 4; // default max players to 4 for 2v2
            return;
        }
        this.pMax = pMax;
    }
    
    /**
     * Gets players Set.
     *
     * @return the players
     */
    public Map< Integer, User> getPlayers() {
        
        return players;
    }
    
    /**
     * Sets players Set.
     *
     * @param players the players
     */
    public void setPlayers( Map<Integer,User> players ) {
        
        this.players = players;
    }
    
    /**
     * Gets score 1.
     *
     * @return the score 1
     */
    public String getScore1() {
        
        return score1;
    }
    
    /**
     * Sets score 1.
     *
     * @param score1 the score 1
     */
    public void setScore1( String score1 ) {
        
        this.score1 = score1;
    }
    
    /**
     * Gets score 2.
     *
     * @return the score 2
     */
    public String getScore2() {
        
        return score2;
    }
    
    /**
     * Sets score 2.
     *
     * @param score2 the score 2
     */
    public void setScore2( String score2 ) {
        
        this.score2 = score2;
    }
    
    /**
     * Gets sport.
     *
     * @return the sport
     */
    public String getSport() {
        
        return sport;
    }
    
    /**
     * Sets sport.
     *
     * @param sport the sport
     */
    public void setSport( String sport ) {
        
        this.sport = sport;
    }
    
    /**
     * Gets game status.
     *
     * @return the status
     */
    public Integer getStatus() {
        
        return status;
    }
    
    /**
     * Sets game status.
     *
     * @param status the status
     */
    public void setStatus( @Nullable Integer status ) {
        
        if ( status == null ) {
            this.status = 0;
            return;
        }
        this.status = status;
    }
    
    /**
     * Gets team 1 count.
     *
     * @return the team 1 count
     */
    public Integer getTeam1Count() {
        
        return team1Count;
    }
    
    /**
     * Sets team 1 count.
     *
     * @param team1Count the team 1 count
     */
    public void setTeam1Count( @Nullable Integer team1Count ) {
        
        if ( team2Count == null ) {
            this.team1Count = 0;
            return;
        }
        this.team1Count = team1Count;
    }
    
    /**
     * Gets team 2 count.
     *
     * @return the team 2 count
     */
    public Integer getTeam2Count() {
        
        return team2Count;
    }
    
    /**
     * Sets team 2 count.
     *
     * @param team2Count the team 2 count
     */
    public void setTeam2Count( @Nullable Integer team2Count ) {
        
        if ( team2Count == null ) {
            this.team2Count = 0;
            return;
        }
        this.team2Count = team2Count;
    }
    
    /**
     * Gets time.
     *
     * @return the time
     */
    public Time getTime() {
        
        return time;
    }
    
    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime( Time time ) {
        this.time = time;
    }
    
    /**
     * Gets max amount of players.
     *
     * @return the max
     */
    public Integer getpMax() {

        return pMax;
    }

    /**
     * Sets max amount of players.
     *
     * @param pMax the player max
     */
    public void setpMax( Integer pMax ) {

        this.pMax = pMax;
    }
    
    @Override
    public int hashCode() {
        
        return Objects.hash(gameId);
    }
    
    @Override
    public boolean equals( Object o ) {
        
        if ( o instanceof Game ) {
            Game otherGame = (Game) o;
            return Objects.equals(otherGame.gameId, this.gameId);
        } else {
            return false;
        }
        
    }
    
    @Override
    public String toString() {
        
        return "Game{" + "date = '" + date + '\'' + ",pMax = '" + pMax + '\'' + ",chatId = '" + chatId + '\''
                + ",score2 = '" + score2 + '\'' + ",p10Id = '" + p10Id + '\'' + ",p4Id = '" + p4Id + '\'' + ",p3Id = '"
                + p3Id + '\'' + ",p2Id = '" + p2Id + '\'' + ",p1Id = '" + p1Id + '\'' + ",p8Id = '" + p8Id + '\''
                + ",p7Id = '" + p7Id + '\'' + ",p6Id = '" + p6Id + '\'' + ",p5Id = '" + p5Id + '\''
                + ",p9Id = '" + p9Id + '\'' + ",id = '" + gameId + '\''
                + ",score1 = '" + score1 + '\'' + ",time = '" + time + '\'' + "}";
    }
    
    /**
     * Is team 1 full boolean.
     *
     * @return the boolean
     */
    public boolean isTeam1Full() {
        
        int teamSize = pMax / 2;
        
        return teamSize == team1Count;
    }
    
    /**
     * Is team 2 full boolean.
     *
     * @return the boolean
     */
    public boolean isTeam2Full() {
        
        int teamSize = pMax / 2;
        return teamSize == team2Count;
    }
    
   /**
     *  the game's time.
     *
     * @return the time
     */
    public Time isTime() {
        return time;
    }
    
    /**
     * Remove player from player list.
     *
     * @param player the player
     */
    public void removePlayer( User player ) {
        if  ( !players.containsValue(player)) {
      //  if ( !players.contains(player) ) {
            throw new IllegalArgumentException();
        }
        // players.remove(player);
        players.remove(player.getId());
        player.removeGame(this);
    }
    
    /**
     * Remove player id from associated team.
     *
     * @param player the player
     */
    public void removeTeamPlayer( User player ) {
        if  ( !players.containsValue(player)) {
        //if ( !players.contains(player) ) {
            throw new IllegalArgumentException();
            
        }
        
        if ( Objects.equals(p1Id, player.getId()) ) {
            p1Id = null;
            team1Count--;
        } else if ( Objects.equals(p2Id, player.getId()) ) {
            p2Id = null;
            team1Count--;
        } else if ( Objects.equals(p3Id, player.getId()) ) {
            p3Id = null;
            team1Count--;
        } else if ( Objects.equals(p4Id, player.getId()) ) {
            p4Id = null;
            team1Count--;
        } else if ( Objects.equals(p5Id, player.getId()) ) {
            p5Id = null;
            team1Count--;
        } else if ( Objects.equals(p6Id, player.getId()) ) {
            p6Id = null;
            team2Count--;
        } else if ( Objects.equals(p7Id, player.getId()) ) {
            p7Id = null;
            team2Count--;
        } else if ( Objects.equals(p8Id, player.getId()) ) {
            p8Id = null;
            team2Count--;
        } else if ( Objects.equals(p9Id, player.getId()) ) {
            p9Id = null;
            team2Count--;
        } else if ( Objects.equals(p10Id, player.getId()) ) {
            p10Id = null;
            team2Count--;
        }
        removePlayer(player);
        
    }
    
    
    /**
     * Gets game location.
     *
     * @return the game location
     */
    public GameLocations getGameLocation() {
        return gameLocation;
    }
    
    /**
     * Sets game location.
     *
     * @param gameLocation the game location
     */
    public void setGameLocation(GameLocations gameLocation) {
        this.gameLocation = gameLocation;
    }
    
    
    
}