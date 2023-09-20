package com.demo.forecast.services;

import com.demo.forecast.models.DataSource;
import com.demo.forecast.models.Forecast;
import com.demo.forecast.models.smhi.Geometry;
import com.demo.forecast.models.smhi.Parameter;
import com.demo.forecast.models.smhi.SmhiRoot;
import com.demo.forecast.models.smhi.TimeSeries;
import com.demo.forecast.repositories.ForecastRepository;
import com.fasterxml.jackson.databind.JsonNode;
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

        ArrayList<TimeSeries> timeseriesList = smhiRoot.getTimeSeries();
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR_OF_DAY, 25);
        Date tomorrow = calendar.getTime();

        for (TimeSeries timeSeries : timeseriesList) {
            List<Float> values;
            Date validTime = timeSeries.getValidTime();

            LocalDate validLocalDate = validTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


            calendar.setTime(validTime);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);


            var forecastFromSmhi = new Forecast();


            if (validTime.after(currentTime) && validTime.before(tomorrow) &&
                    hour == currentHour) {

                for (Parameter parameter : timeSeries.getParameters()) {

                    float latitude = 1.0f;
                    float longitude = 1.0f;
                    String paramName = parameter.getName();
                    values = parameter.getValues();

                    if ("t".equals(paramName)) {
                        Geometry geometry = smhiRoot.getGeometry();
                        ArrayList<ArrayList<Float>> coordinates = geometry.getCoordinates();
                        for (Float paramValue : values) {
                            for (ArrayList<Float> coordinate : coordinates) {
                                latitude = coordinate.get(1);
                                longitude = coordinate.get(0);
                            }
                            System.out.println("----------------------------");
                            forecastFromSmhi.setId(UUID.randomUUID());
                            System.out.println("id:\t" + UUID.randomUUID());
                            forecastFromSmhi.setPredictionDate(validLocalDate);
                            System.out.println("date:\t" + validLocalDate);
                            forecastFromSmhi.setPredictionHour(hour);
                            System.out.println("hour:\t" + hour);
                            forecastFromSmhi.setPredictionTemperature(paramValue);
                            System.out.println("temp:\t" + paramValue);
                            forecastFromSmhi.setLatitude(latitude);
                            System.out.println("latitude:\t" + latitude);
                            forecastFromSmhi.setLongitude(longitude);
                            System.out.println("longitude:\t" + longitude);
                            forecastFromSmhi.setCreated(LocalDateTime.now());
                            System.out.println("created:\t" + forecastFromSmhi.getCreated());
                            forecastFromSmhi.setDataSource(DataSource.Smhi);
                            System.out.println("datasource:\t" + DataSource.Smhi);

                        }
                    } else if ("pcat".equals(paramName)) {
                        boolean rainOrSnow = false;
                        for (Float paramValue : values) {
                            if (paramValue == 0.0) {
                                System.out.println("values:\t" + paramValue);
                                System.out.println("no rain or snow");
                            } else if (paramValue == 3.0) {
                                rainOrSnow = true;
                                System.out.println("values:\t" + paramValue);
                                System.out.println("raining");
                            } else if (paramValue == 1.0) {
                                rainOrSnow = true;
                                System.out.println("values:\t" + paramValue);
                                System.out.println("snowing");
                            }
                        }

                        forecastFromSmhi.setRainOrSnow(rainOrSnow);
                        System.out.println("----------------------------");

                    }
                }
                forecastRepository.save(forecastFromSmhi);
            }
        }
    }
}


