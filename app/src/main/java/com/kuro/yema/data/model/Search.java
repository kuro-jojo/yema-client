package com.kuro.yema.data.model;

import java.io.Serializable;

public class Search implements Serializable {
    private String location;
    private String price;
    private Filter filter;
    private String queryFilter;

    public Search(String location, String price, Filter filter) {
        this.location = location;
        this.price = price;
        this.filter = filter;
    }

    public Search(String location, String price) {
        this.location = location;
        this.price = price;
    }

    public Search(Filter filter) {
        this.filter = filter;
    }

    public String getQueryFilter() {
        return queryFilter;
    }

    public void setQueryFilter(String queryFilter) {
        this.queryFilter = queryFilter;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "Search{" +
                "location='" + location + '\'' +
                ", price='" + price + '\'' +
                ", filter=" + filter +
                ", queryFilter='" + queryFilter + '\'' +
                '}';
    }
}
