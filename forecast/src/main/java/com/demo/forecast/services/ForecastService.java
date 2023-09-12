package com.demo.forecast.services;

import com.demo.forecast.dto.AverageForecastDTO;
import com.demo.forecast.models.Forecast;
import com.demo.forecast.repositories.ForecastRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ForecastService {

    private static List<Forecast> allForecasts;

    @Autowired
    private ForecastRepository forecastRepository;
    //private static List<Forecast> forecasts = new ArrayList<>();

    /*

    public ForecastService(){
        try {
            forecasts = readFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     */
    /*
    private List<Forecast> readFromFile() throws IOException {
        if(!Files.exists(Path.of("predictions.json"))) return new ArrayList<Forecast>();
        ObjectMapper objectMapper = getObjectMapper();
        var jsonStr = Files.readString(Path.of("predictions.json"));
        return  new ArrayList(Arrays.asList(objectMapper.readValue(jsonStr, Forecast[].class ) ));
    }
    */

    /*

// kommer inte användas något mera?
    private void writeAllToFile(List<Forecast> weatherPredictions) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, weatherPredictions);

        Files.writeString(Path.of("predictions.json"), stringWriter.toString());

    }

     */


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
    public Forecast add(Forecast forecast) {
        forecastRepository.save(forecast);
        return forecast;
    }
    /*

    public void delete(UUID id) throws IOException{
        forecasts.removeIf(forecasts -> forecasts.getId().equals(id));
        writeAllToFile(forecasts);
    }

     */

    public Forecast getByIndex(int i) {
        //return null;
        ///return forecasts.get(i);
        return forecastRepository.findAll().get(i);
    }

    public void update(Forecast forecast) throws IOException {
        forecast.setUpdated(LocalDateTime.now());
        forecastRepository.save(forecast);
        /*
        var forecastInList = get(forecastFromUser.getId()).get();
        forecastInList.setTemperature(forecastFromUser.getTemperature());
        forecastInList.setDate(forecastFromUser.getDate());
        forecastInList.setHour(forecastFromUser.getHour());
        forecastInList.setLastModifiedBy(forecastFromUser.getLastModifiedBy());
        writeAllToFile(forecasts);

         */


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
    // old - pre db

    public List<Forecast> getForecasts(){
        return forecasts;
    }
    public Forecast add(Forecast forecast) throws IOException {
        forecast.setId(UUID.randomUUID());
        forecasts.add(forecast);
        writeAllToFile(forecasts);
        return forecast;
    }



    public void delete(UUID id) throws IOException{
        forecasts.removeIf(forecasts -> forecasts.getId().equals(id));
        writeAllToFile(forecasts);
    }

    public Forecast getByIndex(int i) {
        return forecasts.get(i);
    }

    public void update(Forecast forecastFromUser) throws IOException {
        //
        var forecastInList = get(forecastFromUser.getId()).get();
        forecastInList.setTemperature(forecastFromUser.getTemperature());
        forecastInList.setDate(forecastFromUser.getDate());
        forecastInList.setHour(forecastFromUser.getHour());
        forecastInList.setLastModifiedBy(forecastFromUser.getLastModifiedBy());
        writeAllToFile(forecasts);
    }

    public Optional<Forecast> get(UUID id) {
        return getForecasts().stream().filter(forecast -> forecast.getId().equals(id))
                .findFirst();
    }

 */

}
