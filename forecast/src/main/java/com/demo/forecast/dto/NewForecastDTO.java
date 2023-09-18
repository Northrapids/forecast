package com.demo.forecast.dto;

import java.time.LocalDate;

public class NewForecastDTO { // Data transfer object

    private LocalDate predictionDate; //20230821
    private int predictionHour;

    private double predictionTemperature;
    public LocalDate getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDate date) {
        this.predictionDate = (date);
    }

    public int getPredictionHour() {
        return predictionHour;
    }

    public void setPredictionHour(int hour) {
        this.predictionHour = hour;
    }

    public double getPredictionTemperature() {
        return predictionTemperature;
    }

    public void setPredictionTemperature(float temperature) {
        this.predictionTemperature = temperature;
    }
}
