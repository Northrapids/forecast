package com.demo.forecast.services;

import com.demo.forecast.models.DataSource;
import com.demo.forecast.models.Forecast;
import com.demo.forecast.models.smhi.Geometry;
import com.demo.forecast.models.smhi.Parameter;
import com.demo.forecast.models.smhi.SmhiRoot;
import com.demo.forecast.models.smhi.TimeSeries;
import com.demo.forecast.repositories.ForecastRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class SmhiService {

    @Autowired
    private ForecastRepository forecastRepository;

    public void fetchAndSaveSmhiDataToDB() throws IOException {
        var objectMapper = new ObjectMapper();

        // Fetch weather forecast data from the SMHI API
        SmhiRoot smhiRoot = objectMapper.readValue(new URL
                        ("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/18/lat/59/data.json"),
                SmhiRoot.class);

        System.out.println("+------------------------------------------------------------------------+");
        System.out.println("approvedTime " + smhiRoot.getApprovedTime());
        System.out.println("referenceTime " + smhiRoot.getReferenceTime());
        System.out.println("Location: " + smhiRoot.getGeometry());
        System.out.println("+------------------------------------------------------------------------+");

        List<TimeSeries> timeseriesList = smhiRoot.getTimeSeries();

        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR_OF_DAY, 25);
        Date tomorrow = calendar.getTime();
        for (TimeSeries timeSeries : timeseriesList) {
            Date validTime = timeSeries.getValidTime();
            calendar.setTime(validTime);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

            LocalDate validLocalDate = validTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (validTime.after(currentTime) && validTime.before(tomorrow) &&
                    hour == currentHour) {
                for (Parameter param : timeSeries.getParameters()) {
                    String paraName = param.getName();
                    var forecastFromSmhi = new Forecast();
                    ArrayList<Float> values = param.getValues();

                    Boolean rainOrSnow = false;

                    double latitude = 1.0d;
                    double longitude = 1.0d;


                    for (Float paramValue : values) {
                        if ("t".equals(paraName) || "pcat".equals(paraName)) {
                            if (paramValue == 3.0 || paramValue == 1) {
                                rainOrSnow = true;
                            }
                        }

                        Geometry geometry = smhiRoot.getGeometry();
                        ArrayList<ArrayList<Double>> coordinates = geometry.getCoordinates();
                        for(ArrayList<Double> coordinate : coordinates) {
                            latitude = coordinate.get(1);
                            longitude = coordinate.get(0);
                        }

                        if ("t".equals(paraName)) {

                            System.out.println("----------------------------");
                            System.out.println("date:\t" + validLocalDate);
                            System.out.println("hour:\t" + hour);
                            System.out.println("temp:\t" + paramValue);
                            System.out.println("rain or snow:\t" + rainOrSnow);

                            forecastFromSmhi.setId(UUID.randomUUID());
                            forecastFromSmhi.setRainOrSnow(rainOrSnow);
                            forecastFromSmhi.setPredictionTemperature(paramValue);
                            forecastFromSmhi.setPredictionDate(validLocalDate);
                            forecastFromSmhi.setPredictionHour(hour);
                            forecastFromSmhi.setDataSource(DataSource.Smhi);
                            forecastFromSmhi.setCreated(LocalDateTime.now());
                            forecastFromSmhi.setLatitude(latitude);
                            forecastFromSmhi.setLongitude(longitude);
                            forecastRepository.save(forecastFromSmhi);

                        }
                    }
                }
            }
        }
    }
}
