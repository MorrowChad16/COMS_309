package com.example.pickmeup.Account.Admin;

public class AdminCourt {
    private String courtName;
    private String courtAddress;
    private String id;
    private String info;
    private String longt;
    private String lat;

    AdminCourt(String courtName, String courtAddress, String id, String info, String longt, String lat) {
        this.courtName = courtName;
        this.courtAddress = courtAddress;
        this.id = id;
        this.info = info;
        this.longt = longt;
        this.lat = lat;
    }

    public String getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public String getLongt() {
        return longt;
    }

    public String getLat() {
        return lat;
    }

    /**
     * @return user specified court name
     */
    public String getCourtName() {
        return courtName;
    }

    /**
     * @return user specified court address
     */
    String getCourtAddress() {
        return courtAddress;
    }
}
