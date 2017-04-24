package com.cosicervin.administration.domain;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ervincosic on 4/7/16.
 */
public class Ride {
    String date,time,plz,dir,addr,flightNr,arrival,name,email,phone,persons,lugage;
    int id,price;
    boolean hasdriver;
    boolean childseat;

    public Ride(String date, String time, String plz, String dir, String addr, int price) {
        this.date = date;
        this.time = time;
        this.plz = plz;
        this.dir = dir;
        this.addr = addr;
        this.price = price;
    }

    public Ride() {
    }

    public void setHasdriver(boolean hasdriver) {
        this.hasdriver = hasdriver;
    }

    public void setLugage(String lugage) {
        this.lugage = lugage;
    }

    public void setPersons(String persons) {
        this.persons = persons;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public void setFlightNr(String flightNr) {
        this.flightNr = flightNr;
    }

    public boolean isHasdriver() {

        return hasdriver;
    }

    public String getLugage() {
        return lugage;
    }

    public String  getPersons() {
        return persons;
    }

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getArrival() {
        return arrival;
    }

    public String getFlightNr() {
        return flightNr;
    }

    public boolean isChildseat() {
        return childseat;
    }

    public void setChildseat(boolean childseat) {
        this.childseat = childseat;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDate(String date) {

        this.date = date;
    }

    public String getDate() {
        Date d;
        String toReturn = null;
        try {
           d = new SimpleDateFormat("yyyy-mm-dd").parse(this.date);
            toReturn = new SimpleDateFormat("dd.mm.yyyy").format(d);
        } catch (ParseException e) {
            Log.e("Date Error",e.toString());
        }

        return toReturn;
    }

    public String getTime() {
        return time;
    }

    public String getPlz() {
        return plz;
    }

    public String getDir() {
        return dir;
    }

    public String getAddr() {
        return addr;
    }

    public int getPrice() {
        return price;
    }
}
