package com.example.pickmeup.Login.GuestScreen;

public class GuestGame {
    private int image;
    private String courtName;
    private String date;
    private String team1score;
    private String team2score;

    /**
     * @param image int value of the image
     * @param courtName string value of the court name
     * @param date of the game
     * @param team1score of the game
     * @param team2score of the game
     */
    GuestGame(int image, String courtName, String date, String team1score, String team2score) {
        this.image = image;
        this.courtName = courtName;
        this.date = date;
        this.team1score = team1score;
        this.team2score = team2score;
    }

    /**
     * @return image int value of front end value
     */
    public int getImage() {
        return image;
    }

    /**
     * @return court name where the game was
     */
    public String getCourtName() {
        return courtName;
    }

    /**
     * @return date of the game
     */
    public String getDate() {
        return date;
    }

    /**
     * @return team 1 score of the game
     */
    String getTeam1score() {
        return team1score;
    }

    /**
     * @return team 2 score of the game
     */
    String getTeam2score() {
        return team2score;
    }
}
