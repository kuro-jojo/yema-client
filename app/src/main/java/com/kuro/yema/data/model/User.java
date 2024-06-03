package com.kuro.yema.data.model;

import java.util.ArrayList;

public class User {
    private String uid;
    private String phoneNumber;
    private String paymentPhoneNumber;
    private ArrayList<String> listOfWishlistHouses = new ArrayList<>();

    public User() {

    }

    public User(String uid, String phoneNumber) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
    }

    public User(String uid, String phoneNumber, String paymentPhoneNumber) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.paymentPhoneNumber = paymentPhoneNumber;
    }


    public User(String uid) {
        this.uid = uid;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<String> getListOfWishlistHouses() {
        return listOfWishlistHouses;
    }

    /**
     * Sets list of wishlist houses with houseIDs.
     *
     * @param listOfWishlistHouses the list of wishlist houses
     */
    public void setListOfWishlistHouses(ArrayList<String> listOfWishlistHouses) {
        this.listOfWishlistHouses = listOfWishlistHouses;
    }

    public void addHouseToWishlist(String houseId) {
        if (!listOfWishlistHouses.contains(houseId)) {
            this.listOfWishlistHouses.add(houseId);
        }
    }

    public void removeFromWishlist(String homeId) {
        this.listOfWishlistHouses.remove(homeId);
    }

    public String getPaymentPhoneNumber() {
        return paymentPhoneNumber;
    }

    public void setPaymentPhoneNumber(String paymentPhoneNumber) {
        this.paymentPhoneNumber = paymentPhoneNumber;
    }
}
