package com.kuro.yema.data.enums;

public enum HouseType {
    DUPLEX("Duplexes"),
    STUDIO("Studios"),
    APARTMENT("Apartment"),
    FLAT("Flats"),
    VILLA("Villas"),
    SEMI_DETACHED("Semi-detached houses");

    public String type;
    public String typeLowerCase;

    HouseType(String type) {
        this.type = type;
        this.typeLowerCase = type.toLowerCase();
    }
}
