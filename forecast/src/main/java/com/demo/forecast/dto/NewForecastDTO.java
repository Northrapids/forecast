package com.demo.forecast.dto;

import java.time.LocalDate;

public class NewForecastDTO { // Data transfer object

    private LocalDate date; //20230821
    private int hour;

    private float temperature;
    public LocalDate getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = LocalDate.ofEpochDay(date);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
