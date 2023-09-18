package com.demo.forecast.services;

import com.demo.forecast.models.DataSource;
import com.demo.forecast.models.Forecast;
import com.demo.forecast.repositories.ForecastRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ForecastService {

    private static List<Forecast> allForecasts;

    @Autowired
    private ForecastRepository forecastRepository;
    //private static List<Forecast> forecasts = new ArrayList<>();



    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }

    public ForecastService(){

    }

    /*
    public List<Forecast> Search(LocalDate day, int fromHour, int toHour) {
        return allForecasts
                .stream()
                .filter(forecast ->  forecast.getPredictionDate() == day && forecast.getPredictionHour() >= fromHour && forecast.getPredictionHour() <= toHour )
                .sorted(Forecast::SORT_HOUR)
                .toList();
    }

     */

    // new
    public List<Forecast> getForecasts(){
        return forecastRepository.findAll();
    }

    /*
    public List<Forecast> getForecastsByDataSource(String dataSource) {
        return forecastRepository.findByDataSource(dataSource);
        //return forecastRepository.findAll();
    }

     */
    public Forecast create(Forecast forecast) {
        //forecast.setUpdated();
        forecastRepository.save(forecast);
        return forecast;
    }


    public Forecast getByIndex(int i) {
        return forecastRepository.findAll().get(i);
    }

    public void update(Forecast forecast) throws IOException {
        forecast.setUpdated(LocalDateTime.now());
        forecastRepository.save(forecast);
    }



    public void delete(UUID id) {
        forecastRepository.deleteById(id);
        System.out.println("Deleted");
    }

    public Optional<Forecast> get(UUID id) {
        return forecastRepository.findById(id);
        //return getForecasts().stream().filter(forecast -> forecast.getId().equals(id))
         //       .findFirst();
    }

    /*
    public List<AverageForecastDTO> calculateAverage(LocalDate day) {
        var averageList = new ArrayList<AverageForecastDTO>();

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //String formatDateTime = day.format(formatter);
        //int dateAsInt = Integer.parseInt(formatDateTime);

        //var allForecastsForDay = forecastRepository.findAllByPredictionDate(dateAsInt);

        var allForecastsForDay = forecastRepository.findAllByPredictionDate(day);

        for (int hour = 0; hour <= 23; hour++) {
            var averageDto = new AverageForecastDTO();
            averageDto.setHour(hour);
            averageDto.setDate(day);
            float amount = 0;
            float sum =  0;
            for (Forecast forecast : allForecastsForDay) {
                if(forecast.getPredictionHour() == hour){
                    amount++;
                    sum += forecast.getPredictionTemperature();
                }
            }
            if (amount >0) {
                averageDto.setAverage(sum / amount);
                averageList.add(averageDto);
            }
        }
        return averageList;
    }

     */

}
