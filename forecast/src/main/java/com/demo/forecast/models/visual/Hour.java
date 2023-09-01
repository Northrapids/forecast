package com.demo.forecast.models.visual;

import java.util.ArrayList;

public class Hour {
    private String datetime;
    private int datetimeEpoch;
    private double temp;
    private int snow;
    private ArrayList<String> preciptype;

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

    public int getSnow() {
        return snow;
    }

    public void setSnow(int snow) {
        this.snow = snow;
    }

    public ArrayList<String> getPreciptype() {
        return preciptype;
    }

    public void setPreciptype(ArrayList<String> preciptype) {
        this.preciptype = preciptype;
    }
}
