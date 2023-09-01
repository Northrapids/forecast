package com.demo.forecast.models.visual;

import java.util.ArrayList;

public class Day {
    private String datetime;
    private int datetimeEpoch;
    private double temp;
    private ArrayList<String> preciptype;
    private int snow;
    private Normal normal;
    private ArrayList<Hour> hours;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getDatetimeEpoch() {
        return datetimeEpoch;
    }

    public void setDatetimeEpoch(int datetimeEpoch) {
        this.datetimeEpoch = datetimeEpoch;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public ArrayList<String> getPreciptype() {
        return preciptype;
    }

    public void setPreciptype(ArrayList<String> preciptype) {
        this.preciptype = preciptype;
    }

    public int getSnow() {
        return snow;
    }

    public void setSnow(int snow) {
        this.snow = snow;
    }

    public Normal getNormal() {
        return normal;
    }

    public void setNormal(Normal normal) {
        this.normal = normal;
    }

    public ArrayList<Hour> getHours() {
        return hours;
    }

    public void setHours(ArrayList<Hour> hours) {
        this.hours = hours;
    }
}
