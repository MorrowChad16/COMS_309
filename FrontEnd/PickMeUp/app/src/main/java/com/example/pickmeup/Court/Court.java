package com.example.pickmeup.Court;

import java.util.Comparator;

public class Court {
    private int id;
    private int locationID;
    private String courtName;
    private double milesAway;
    private int image;
    private double latitude;
    private double longitude;
    private String gameStatus;

    public Court(int id, int locationID, String courtName, double milesAway, int image, double latitude, double longitude, String gameStatus) {
        this.id = id;
        this.locationID = locationID;
        this.courtName = courtName;
        this.milesAway = milesAway;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gameStatus = gameStatus;
    }

    /**
     * @return court id
     */
    public int getId() {
        return id;
    }

    /**
     * @return court name
     */
    String getCourtName() {
        return courtName;
    }

    /**
     * @return number of miles away from user specified location of searching
     */
    double getMilesAway() {
        return milesAway;
    }

    /**
     * @return image id for the court
     */
    int getImage() {
        return image;
    }

    /**
     * @return id for the court
     */
    int getLocationID(){
        return locationID;
    }

    /**
     * @return latitude for the court
     */
    double getLatitude(){
        return latitude;
    }

    /**
     * @return longitude for the court
     */
    double getLongitude(){
        return longitude;
    }

    /**
     * @return string of game types available at the court (weekday, weekend, etc.) to give the user a better idea what they will see
     */
    String getGameStatus(){
        return gameStatus;
    }

    /*Comparator for sorting the list by roll no*/
    public static Comparator<Court> sortByDist = (c1, c2) -> {

        double distNo1 = c1.getMilesAway();
        double distNo2 = c2.getMilesAway();

        /*For ascending order*/
        return (int) (distNo1-distNo2);
    };
}
