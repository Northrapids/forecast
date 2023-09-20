package com.demo.forecast.models.smhi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Geometry {

    private String type;
    private ArrayList<ArrayList<Float>> coordinates;

    private String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public ArrayList<ArrayList<Float>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<ArrayList<Float>> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Geometry{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }


}
