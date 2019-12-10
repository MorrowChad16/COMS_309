package com.example.pickmeup.Data.model;

public class UserLocations {
    private Integer UserLocationId;
    private Integer userId;
    private Double longt;
    private Double lat;
    private String name;
    
    /**
     * Instantiates a new User locations.
     */
    public UserLocations() {
    }

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
        return temp + String.format("%s, LatLong %f : %f", name, lat, longt);
    }
    
    /**
     * Gets longt.
     *
     * @return the longt
     */
    public Double getLongt() {
        return this.longt;
    }
    
    /**
     * Gets lat.
     *
     * @return the lat
     */
    public Double getLat() {
        return this.lat;
    }
    
    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Sets longt.
     *
     * @param longt the longt
     */
    public void setLongt(final Double longt) {
        this.longt = longt;
    }
    
    /**
     * Sets lat.
     *
     * @param lat the lat
     */
    public void setLat(final Double lat) {
        this.lat = lat;
    }
    
    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
    }
    
    
    /**
     * Gets user location id.
     *
     * @return the user location id
     */
    public Integer getUserLocationId() {
        return this.UserLocationId;
    }
    
    
    /**
     * Gets user id.
     *
     * @return the user id
     */
    public Integer getUserId() {
        return this.userId;
    }
    
    
    /**
     * Sets user location id.
     *
     * @param UserLocationId the user location id
     */
    public void setUserLocationId(final Integer UserLocationId) {
        this.UserLocationId = UserLocationId;
    }
    
    
    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(final Integer userId) {
        this.userId = userId;
    }
    
}

