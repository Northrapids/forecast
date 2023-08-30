package com.demo.forecast.models.smhi;

import java.util.ArrayList;
import java.util.Date;

public class Smhi {

    public Date approvedTime;
    public Date referenceTime;
    public Geometry geometry;
    public ArrayList<TimeSeries> timeSeries;

    public Date getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(Date approvedTime) {
        this.approvedTime = approvedTime;
    }

    public Date getReferenceTime() {
        return referenceTime;
    }

    public void setReferenceTime(Date referenceTime) {
        this.referenceTime = referenceTime;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ArrayList<TimeSeries> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(ArrayList<TimeSeries> timeSeries) {
        this.timeSeries = timeSeries;
    }
}
