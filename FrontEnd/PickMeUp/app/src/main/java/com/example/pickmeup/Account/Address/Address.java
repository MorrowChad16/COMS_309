package com.example.pickmeup.Account.Address;

public class Address {
    private String addressName;
    private String addressLocation;

    public Address(String addressName, String addressLocation) {
        this.addressName = addressName;
        this.addressLocation = addressLocation;
    }

    /**
     * @return user specified name of a saved address
     */
    String getAddressName() {
        return addressName;
    }

    /**
     * @return user specified address of a saved address
     */
    String getAddressLocation() {
        return addressLocation;
    }
}
