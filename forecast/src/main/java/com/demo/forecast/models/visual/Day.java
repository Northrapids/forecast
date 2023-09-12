package com.demo.forecast.models.visual;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Day {
    public String datetime;
    public int datetimeEpoch;
    public double temp;
    public int precip;
    public double precipprob;
    public int precipcover;
    public ArrayList<String> preciptype;
    public int snow;
    public int snowdepth;
    public ArrayList<Hour> hours;

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

    public int getPrecip() {
        return precip;
    }

    public void setPrecip(int precip) {
        this.precip = precip;
    }

    public double getPrecipprob() {
        return precipprob;
    }

    public void setPrecipprob(double precipprob) {
        this.precipprob = precipprob;
    }

    public int getPrecipcover() {
        return precipcover;
    }

    public void setPrecipcover(int precipcover) {
        this.precipcover = precipcover;
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

    public int getSnowdepth() {
        return snowdepth;
    }

    public void setSnowdepth(int snowdepth) {
        this.snowdepth = snowdepth;
    }

    public ArrayList<Hour> getHours() {
        return hours;
    }

    public void setHours(ArrayList<Hour> hours) {
        this.hours = hours;
    }
}
