package com.kuro.yema.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.Timestamp;
import com.google.firebase.database.Exclude;
import com.kuro.yema.data.enums.HouseType;
import com.kuro.yema.data.enums.ListingType;
import com.kuro.yema.data.enums.PriceCurrency;
import com.kuro.yema.data.enums.RentalTerm;

import java.io.Serializable;
import java.util.ArrayList;

public class House implements Serializable {
    @JsonProperty("objectID")
    private String houseId;
    private String title;
    private double price;
    private PriceCurrency priceCurrency;
    /**
     * Must not include the country where the house is located
     */
    private String location;
    private String description;
    private String requirements;
    @Exclude
    private HouseType houseType;
    private int numberBedrooms;
    private int numberBathrooms;
    private int numberParkingSpot;
    private float livingArea;
    private RentalTerm rentalTerm;
    private ListingType listingType;
    private transient Timestamp uploadedAt;
    private ArrayList<String> images;
    private boolean isInFavorite;

    public House() {

    }

    public House(String houseId) {
        this.houseId = houseId;
    }

    public House(String title, double price, PriceCurrency priceCurrency, String location, String description, String requirements, int numberBathrooms, float livingArea, RentalTerm rentalTerm, ListingType listingType, HouseType houseType, int numberBedrooms, int numberParkingSpot, Timestamp uploadedAt, ArrayList<String> images) {
        this.title = title;
        this.price = price;
        this.priceCurrency = priceCurrency;
        this.location = location;
        this.description = description;
        this.requirements = requirements;
        this.numberBathrooms = numberBathrooms;
        this.livingArea = livingArea;
        this.rentalTerm = rentalTerm;
        this.listingType = listingType;
        this.houseType = houseType;
        this.numberBedrooms = numberBedrooms;
        this.numberParkingSpot = numberParkingSpot;
        this.uploadedAt = uploadedAt;
        this.images = images;
    }

    public boolean isInFavorite() {
        return isInFavorite;
    }

    public void setInFavorite(boolean inFavorite) {
        isInFavorite = inFavorite;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PriceCurrency getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(PriceCurrency priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public RentalTerm getRentalTerm() {
        return rentalTerm;
    }

    public void setRentalTerm(RentalTerm rentalTerm) {
        this.rentalTerm = rentalTerm;
    }

    public ListingType getListingType() {
        return listingType;
    }

    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }


    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public int getNumberOfImages() {
        return images.size();
    }


    public int getNumberBedrooms() {
        return numberBedrooms;
    }

    public void setNumberBedrooms(int numberBedrooms) {
        this.numberBedrooms = numberBedrooms;
    }

    public int getNumberBathrooms() {
        return numberBathrooms;
    }

    public void setNumberBathrooms(int numberBathrooms) {
        this.numberBathrooms = numberBathrooms;
    }

    public int getNumberParkingSpot() {
        return numberParkingSpot;
    }

    public void setNumberParkingSpot(int numberParkingSpot) {
        this.numberParkingSpot = numberParkingSpot;
    }

    public float getLivingArea() {
        return livingArea;
    }

    public void setLivingArea(float livingArea) {
        this.livingArea = livingArea;
    }

    @Override
    public String toString() {
        return "House{" +
                "houseId='" + houseId + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", priceCurrency=" + priceCurrency +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", requirements='" + requirements + '\'' +
                ", houseType=" + houseType +
                ", numberBedrooms=" + numberBedrooms +
                ", numberBathrooms=" + numberBathrooms +
                ", numberParkingSpot=" + numberParkingSpot +
                ", livingArea=" + livingArea +
                ", rentalTerm=" + rentalTerm +
                ", listingType=" + listingType +
                ", uploadedAt=" + uploadedAt +
                ", images=" + images +
                '}';
    }
}
