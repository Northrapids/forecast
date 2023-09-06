package com.demo.forecast.models.smhi;

import java.util.ArrayList;

public class Parameter {

    private String name;
    private String levelType;
    private int level;
    private String unit;
    private ArrayList<Float> values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ArrayList<Float> getValues() {
        return values;
    }

    public void setValues(ArrayList<Float> values) {
        this.values = values;
    }
}
