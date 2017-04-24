package com.cosicervin.administration.domain;

/**
 * Created by ervincosic on 4/14/16.
 */
public class Sum {
    String driver;
    int sum;

    public Sum(String driver, int sum) {
        this.driver = driver;
        this.sum = sum;
    }

    public void setDriver(String driver) {

        this.driver = driver;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getDriver() {

        return driver;
    }

    public int getSum() {
        return sum;
    }
}
