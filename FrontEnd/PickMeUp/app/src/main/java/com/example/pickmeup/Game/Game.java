package com.example.pickmeup.Game;

public class Game {
    private int gameId;
    private int locationID;
    private String courtName;
    private int image;
    private String latitude;
    private String longitude;
    private String dateTime;

    public Game(int id, int locationID, String courtName, int image, String latitude, String longitude, String dateTime) {
        this.gameId = id;
        this.locationID = locationID;
        this.courtName = courtName;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;
    }

    /**
     * @return game ID linked to the item
     */
    int getGameId() {
        return gameId;
    }

    /**
     * @return location ID linked to the game for the court info
     */
    int getLocationID() {
        return locationID;
    }

    /**
     * @return name of court the game is at
     */
    String getCourtName() {
        return courtName;
    }

    /**
     * @return int value of image value on front end
     */
    int getImage() {
        return image;
    }

    /**
     * @return latitude of court the game is at
     */
    String getLatitude() {
        return latitude;
    }

    /**
     * @return longitude of court the game is at
     */
    String getLongitude() {
        return longitude;
    }

    /**
     * @return date and time of game as one string value
     */
    String getDateTime(){
        return dateTime;
    }
}
