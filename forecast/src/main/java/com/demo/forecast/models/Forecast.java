package com.demo.forecast.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

//import javax.sql.DataSource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Forecast {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime updated;
    //private Instant created2;
    //private Instant updated2;
    private float longitude;
    private float latitude;
    private LocalDate predictionDate;
    private Instant predictionDatum2;
    private int predictionHour; //8
    private float predictionTemperature;
    private boolean rainOrSnow;
    private DataSource dataSource; // API provider

    public Forecast(UUID id) {
        this.id = id;
        this.created = LocalDateTime.now();
        //this.created2 = Instant.now();
    }

    public Forecast() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    /*
    public Instant getCreated2() {
        return created2;
    }

    public void setCreated2(Instant created2) {
        this.created2 = created2;
    }

    public Instant getUpdated2() {
        return updated2;
    }

    public void setUpdated2(Instant updated2) {
        this.updated2 = updated2;
    }

     */

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public LocalDate getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDate predictionDatum) {
        this.predictionDate = predictionDatum;
    }

    /*

    public Instant getPredictionDatum2() {
        return predictionDatum2;
    }

    public void setPredictionDatum2(Instant predictionDatum2) {
        this.predictionDatum2 = predictionDatum2;
    }

     */

    public int getPredictionHour() {
        return predictionHour;
    }

    public void setPredictionHour(int predictionHour) {
        this.predictionHour = predictionHour;
    }

    public float getPredictionTemperature() {
        return predictionTemperature;
    }

    public void setPredictionTemperature(float predictionTemperature) {
        this.predictionTemperature = predictionTemperature;
    }

    public boolean isRainOrSnow() {
        return rainOrSnow;
    }

    public void setRainOrSnow(boolean rainOrSnow) {
        this.rainOrSnow = rainOrSnow;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }






    /*

    // old - pre db

    private UUID id;
    private int date; // yy mm dd
    private int hour;
    private float temperature;
    private String lastModifiedBy; // "Fredrik Nordfors

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

     */
}
