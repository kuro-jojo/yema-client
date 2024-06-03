package com.kuro.yema.data.model;

import com.kuro.yema.data.enums.HouseType;
import com.kuro.yema.data.enums.ListingType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Filter implements Serializable {

    private final ArrayList<HouseType> houseTypes;
    private ListingType listingType;
    private float priceMin;
    private float priceMax;
    private int numberBedrooms;
    private int numberBathrooms;
    private int numberParkingSpot;
    private float livingAreaMin;
    private float livingAreaMax;
    private String[] keywords;

    public Filter() {
        this.houseTypes = new ArrayList<>();
        this.listingType = null;
        this.numberBedrooms = -1;
        this.numberBathrooms = -1;
        this.numberParkingSpot = -1;
        this.livingAreaMin = -1;
        this.livingAreaMax = -1;
        this.keywords = new String[0];
    }

    public boolean isEmpty() {
        return listingType == null && houseTypes.isEmpty() && numberBedrooms <= 0 && numberBathrooms <= 0 && numberParkingSpot <= 0 && livingAreaMin <= 0.0 && livingAreaMax <= 0.0 && (keywords == null || keywords.length == 0);
    }


    public ListingType getListingType() {
        return listingType;
    }

    public void setListingType(ListingType listingType) {
        this.listingType = listingType;
    }

    public float getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(float priceMin) {
        this.priceMin = priceMin;
    }

    public float getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(float priceMax) {
        this.priceMax = priceMax;
    }

    public ArrayList<HouseType> getHouseTypes() {
        return houseTypes;
    }

    public void addHouseTypes(HouseType houseType) {
        this.houseTypes.add(houseType);
    }

    public void removeHouseType(HouseType houseType) {
        this.houseTypes.remove(houseType);
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

    public float getLivingAreaMin() {
        return livingAreaMin;
    }

    public void setLivingAreaMin(float livingAreaMin) {
        this.livingAreaMin = livingAreaMin;
    }

    public float getLivingAreaMax() {
        return livingAreaMax;
    }

    public void setLivingAreaMax(float livingAreaMax) {
        this.livingAreaMax = livingAreaMax;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "houseTypes=" + houseTypes +
                ", listingType=" + listingType +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", numberBedrooms=" + numberBedrooms +
                ", numberBathrooms=" + numberBathrooms +
                ", numberParkingSpot=" + numberParkingSpot +
                ", livingAreaMin=" + livingAreaMin +
                ", livingAreaMax=" + livingAreaMax +
                ", keywords=" + Arrays.toString(keywords) +
                '}';
    }
}
