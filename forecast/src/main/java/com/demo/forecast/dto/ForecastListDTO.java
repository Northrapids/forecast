package com.demo.forecast.dto;

import com.demo.forecast.models.DataSource;

import java.time.LocalDate;
import java.util.UUID;

public class ForecastListDTO {
    public UUID Id;
    public LocalDate Date; //20230821
    public int Hour;
    public double Temperature;
    public DataSource dataSource; // API provider

}
