package com.example.pickmeup.Data.model;
// https://stackoverflow.com/questions/48179838/how-to-map-a-json-to-a-java-model-class

import java.util.ArrayList;
import java.util.List;

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

    private Integer gid;

    private List<UserLocations> userLocations;

    //end private variables

    //public get and set methods to edit tables, queries automated by Hibernate

    /**
     * This constructor copies the User given.
     * @param otherUser User object.
     */
    public LoggedInUser(LoggedInUser otherUser){
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
        this.gid = otherUser.getGid();
        this.userLocations = otherUser.getUserLocations();
    }

    public LoggedInUser() {
        this.id = null;
        this.username = null;
        this.firstname = null;
        this.lastname = null;
        this.email  = null;
        this.profileId = null;
        this.streetAddress  = null;
        this.city  = null;
        this.state  = null;
        this.zipcode  = null;
        this.phoneNumber = null;
        this.gid = null;
        this.userLocations = new ArrayList<>();
    }

    public LoggedInUser(Integer id, String username) {
        this.id = id;
        this.username = username;
        this.firstname = username;
        this.lastname = "";
        this.email = "user@email.com";
        this.profileId = 1;
        this.streetAddress  = "lost ave";
        this.city  = "Ames";
        this.state  = "Iowa";
        this.zipcode  = "50011";
        this.phoneNumber = "8888888888";
        this.gid = 1;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getFirstname(){
        return firstname;
    }

    public void setFirstname(String firstname){
        this.firstname = firstname;
    }

    public String getLastname(){
        return lastname;
    }

    public void setLastname(String lastname){
        this.lastname = lastname;
    }

    public String getStreetAddress(){
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress){
        this.streetAddress = streetAddress;
    }

    public String getCity(){
        return city;
    }

    public void setCity(String city){
        this.city = city;
    }

    public String getState(){
        return state;
    }

    public void setState(String state){
        this.state = state;
    }

    public String getZipcode(){
        return zipcode;
    }

    public void setZipcode(String zipcode){
        this.zipcode = zipcode;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }


    public String getEmail(){
        return email;
    }


    public void setEmail(String email){
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername( String username) {
        this.username = username;
    }

    public void setProfileId(Integer profileId){
        this.profileId = profileId;
    }

    public Integer getProfileId(){
        return profileId;
    }

    /**
     * this method overrides to the toString() method of User to only output as formatted.
     * Used by findAll which calls toString() by iteration of List<User>.
     */
    @Override
    public String toString() {
        return String.format(
                "{\"id\":\"%d\", \"username\":\"%s\", \"email\":\"%s\"}",
                id, username, email);
    }

    @Override
    public Object clone(){
        LoggedInUser newUser = new LoggedInUser(this);
        return (Object) newUser;
    }


    /**
     * @param userLocations the userLocations to set
     */
    public void setUserLocations(List<UserLocations> userLocations) {
        this.userLocations = userLocations;
    }

    /**
     * return list of userLocations
     * @return array list of userLocations
     */
    public List<UserLocations> getUserLocations(){
        return new ArrayList<>(userLocations);
    }

    public void addUserLocation(UserLocations userLocation){
        this.userLocations.add(userLocation);
    }

    public void removeUserLocation(int location){
        this.userLocations.remove(location);
    }

}

