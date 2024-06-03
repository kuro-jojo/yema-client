package com.kuro.yema.data.enums;

public enum ListingType {
    SALE("sale"),
    RENT("rent");

    public final String type;


    ListingType(String type) {
        this.type = type;
    }
}
