package com.cosicervin.administration.domain;

/**
 * Created by ervincosic on 3/24/16.
 */
public class Driver {
    String name;
    String mail;
    String phone;
    String car;
    String id;
    public  Driver(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCar() {
        return car;
    }

    public String getName() {

        return name;
    }

    public String getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public Driver(String phone, String mail, String name) {

        this.phone = phone;
        this.mail = mail;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
