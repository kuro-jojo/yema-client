package com.kuro.yema.data.enums;

public enum RentalTerm {
    MONTH("month"),
    YEAR("year");

    private String term;

    RentalTerm(String term) {
        this.term = term;
    }
}