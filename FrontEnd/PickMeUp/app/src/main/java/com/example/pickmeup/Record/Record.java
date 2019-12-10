package com.example.pickmeup.Record;

import org.jetbrains.annotations.NotNull;

public class Record {
    private String score;
    private String address;
    private String courtName;
    private String date;
    private String time;
    private String winLoss;

    /**
     * @param score score of past game
     * @param address of past game
     * @param courtName of where the game was played
     * @param date of when the game was played
     * @param time of when the game was played
     * @param winLoss status of whether you won or lost that game
     */
    Record(String score, String address, String courtName, String date, String time, String winLoss) {
        this.score = score;
        this.address = address;
        this.courtName = courtName;
        this.date = date;
        this.time = time;
        this.winLoss = winLoss;
    }

    /**
     * @return score of game
     */
    String getScore() {
        return score;
    }

    /**
     * @return address of the court where the game was played
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return court name of where game is played
     */
    public String getCourtName() {
        return courtName;
    }

    /**
     * @return date of when the game was played
     */
    public String getDate() {
        return date;
    }

    /**
     * @return time of when the game was played
     */
    public String getTime() {
        return time;
    }

    /**
     * @return whether you won or lost it
     */
    String getWinLoss(){
        return winLoss;
    }

    /**
     * @return all the record info to string
     */
    @NotNull
    @Override //generated using right click generate -> toString()
    public String toString() {
        return "Record{" +
                "score='" + score + '\'' +
                ", address='" + address + '\'' +
                ", courtName='" + courtName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", winLoss='" + winLoss + '\'' +
                '}';
    }
}
