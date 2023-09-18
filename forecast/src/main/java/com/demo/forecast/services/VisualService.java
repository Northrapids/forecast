package com.demo.forecast.services;

import com.demo.forecast.models.DataSource;
import com.demo.forecast.models.Forecast;
import com.demo.forecast.models.visual.Day;
import com.demo.forecast.models.visual.Hour;
import com.demo.forecast.models.visual.VisualRoot;
import com.demo.forecast.repositories.ForecastRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Service
public class VisualService {


    @Autowired
    private ForecastRepository forecastRepository;

    public void fetchAndSaveVisualDataToDB() throws IOException {
        var objectMapper = new ObjectMapper();

        // Fetch weather forecast data from the visual API

        VisualRoot visualRoot = objectMapper.readValue(new URL
                        ("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/liljeholmen/next24hours?unitGroup=metric&elements=datetime%2CdatetimeEpoch%2Ctemp%2Cprecipprob%2Cpreciptype%2Csnow&key=NV2YVV3CH289TE5AKK9MECDUY&contentType=json"),
                VisualRoot.class);

        long currentTimestamp = System.currentTimeMillis() / 1000;

        for (Day day : visualRoot.getDays()) {


            for (Hour time : day.getHours()) {


                long hourDatetimeEpoch = time.getDatetimeEpoch();

                // Check if the timestamp is within the next 24 hours
                if (hourDatetimeEpoch >= currentTimestamp && hourDatetimeEpoch <= currentTimestamp + 25 * 3600) {
                    String hourDatetime = time.getDatetime();

                    double hourTemp = time.getTemp();
                    System.out.println("-----------------------------");

                    System.out.println("Date:\t" + day.getDatetime());
                    System.out.println("Address:\t" + visualRoot.getAddress());
                    System.out.println("Timezone:\t" + visualRoot.getTimezone());
                    System.out.println("Hour:\t" + hourDatetime);
                    System.out.println("Temp:\t" + hourTemp);
                    //System.out.println("Rain:\t" + day.getPrecip());
                    //System.out.println("Snow:\t" + day.getSnow());


                    Forecast visualForecast = new Forecast();


                    //Calendar calendar = Calendar.getInstance();

                    String datetimeString = time.getDatetime();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                    try {
                        Date datetime = sdf.parse(datetimeString); // Parse the datetime string
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(datetime); // Set the calendar to the parsed datetime
                        int hour = calendar.get(Calendar.HOUR_OF_DAY); // Get the hour

                        // Set the hour in your visualPredictions or update it in your database
                        visualForecast.setPredictionHour(hour);
                    } catch (Exception e) {
                        // Handle parsing or other exceptions
                        e.printStackTrace();
                    }

                    //int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    //visualForecast.setPredictionHour(hour); // set hour in database
                    // Now, you can use `hour` as an integer if needed elsewhere

                    //int hour = LocalDateTime.parse(hourDatetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).getHour();
                    //visualForecast.setPredictionHour(hour); // set hour based on hourDatetime


                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust the pattern as needed
                    LocalDate parsedDate = LocalDate.parse(day.getDatetime(), dateFormatter);

                    double visualLatitude =  visualRoot.getLatitude();
                    double visualLongitude=  visualRoot.getLongitude();

                    if (time.getPrecip() > 0) {
                        visualForecast.setRainOrSnow(true); // You can customize this based on your requirements
                        //System.out.println("Saved - Precipitation: Rain");
                        System.out.println("Precipitation: Rain");
                    } else if (time.getSnow() > 0) {
                        visualForecast.setRainOrSnow(true); // You can customize this based on your requirements
                        //System.out.println("Saved - Precipitation: Snow");
                        System.out.println("Precipitation: Snow");
                    } else {
                        visualForecast.setRainOrSnow(false); // No precipitation
                        //System.out.println("Saved - Precipitation: None");
                        System.out.println("Precipitation: None");
                    }

                    visualForecast.setPredictionDate(parsedDate);
                    //visualForecast.setPredictionHour(hour);
                    visualForecast.setPredictionTemperature(hourTemp);
                    visualForecast.setCreated(LocalDateTime.now());
                    visualForecast.setLongitude(visualLongitude);
                    visualForecast.setLatitude(visualLatitude);
                    visualForecast.setDataSource(DataSource.Visual);
                    //visualForecast.setDataSource(DataSource.Visual);


                    forecastRepository.save(visualForecast);

                }
            }
        }
        System.out.println("\n**************** COMPLETED! ****************\n");

    }

}
