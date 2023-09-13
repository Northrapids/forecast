package com.demo.forecast.dto;

import java.time.LocalDate;

public class AverageForecastDTO {
    //private LocalDate date;
    private int hour;
    private double average;

    public AverageForecastDTO(int hour, double average) {
        this.hour=hour;
        this.average=average;
    }

/*
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

 */

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
