package com.cosicervin.administration.domain;

/**
 * Created by ervincosic on 04/05/2017.
 */

public class Place {
    String name;

    int carPrice;

    int vanPrice;

    int busPrice;

    int id;

    public Place() {
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public int getCarPrice() {
        return carPrice;
    }

    public int getVanPrice() {
        return vanPrice;
    }

    public int getBusPrice() {
        return busPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCarPrice(int carPrice) {
        this.carPrice = carPrice;
    }

    public void setVanPrice(int vanPrice) {
        this.vanPrice = vanPrice;
    }

    public void setBusPrice(int busPrice) {
        this.busPrice = busPrice;
    }

    public void setId(int id) {
        this.id = id;
    }
}
