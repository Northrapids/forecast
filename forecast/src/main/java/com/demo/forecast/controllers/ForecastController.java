package com.demo.forecast.controllers;

import com.demo.forecast.dto.AverageForecastDTO;
import com.demo.forecast.dto.ForecastListDTO;
import com.demo.forecast.dto.NewForecastDTO;
import com.demo.forecast.models.DataSource;
import com.demo.forecast.models.Forecast;
import com.demo.forecast.repositories.ForecastRepository;
import com.demo.forecast.services.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ForecastController {

    @Autowired
    private ForecastService forecastService;
    @Autowired
    private ForecastRepository forecastRepository;

    @GetMapping("/api/forecasts")
    public ResponseEntity<List<ForecastListDTO>> getAll() {
        return new ResponseEntity<List<ForecastListDTO>>(forecastService.getForecasts().stream().map(forecast -> {
            var forecastListDTO = new ForecastListDTO();
            forecastListDTO.Id = forecast.getId();
            forecastListDTO.Date = forecast.getPredictionDate();
            forecastListDTO.Hour = forecast.getPredictionHour();
            forecastListDTO.Temperature = forecast.getPredictionTemperature();
            forecastListDTO.dataSource = forecast.getDataSource();
            return forecastListDTO;
        }).collect(Collectors.toList()), HttpStatus.OK);

    }

    @GetMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Get(@PathVariable UUID id) {
        Optional<Forecast> forecast = forecastService.get(id);
        if (forecast.isPresent()) return ResponseEntity.ok(forecast.get());
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/api/forecasts/{id}")
    public ResponseEntity<NewForecastDTO> update(@PathVariable UUID id, @RequestBody NewForecastDTO newForecastDTO) throws IOException {

        // mappa från dto -> entitet
        var forecast = new Forecast();
        forecast.setId(id);
        forecast.setPredictionDate(newForecastDTO.getDate());
        forecast.setPredictionHour(newForecastDTO.getHour());
        forecast.setPredictionTemperature(newForecastDTO.getTemperature());
        //forecast.setLastModifiedBy("Fredrik Nordfors");
        forecastService.update(forecast);
        return ResponseEntity.ok(newForecastDTO);
    }

    /*
    @PostMapping("/api/forecasts")
    public ResponseEntity<Forecast> add( @RequestBody Forecast forecast) throws IOException{
        forecast.setId(UUID.randomUUID());
        forecastService.add(forecast);
        return ResponseEntity.ok(forecast); // mer REST ful = created (204) samt url till produkten
    }
     */

    @PostMapping("/api/forecasts")
    public ResponseEntity<Forecast> newForecast(@RequestBody Forecast forecast) throws IOException {
        var newCreated = forecastService.add(forecast);
        return ResponseEntity.ok(newCreated);
    }


    @DeleteMapping("/api/forecasts/{id}")
    public ResponseEntity<String> Delete(@PathVariable UUID id) throws IOException {
        forecastService.delete(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/api/forecasts/averageTemp/{date}")
    public ResponseEntity<List<AverageForecastDTO>> overallAverageTemperatureByDate(@PathVariable String date) {
        List<Forecast> forecasts = forecastService.getForecasts();
        List<Forecast> averageList = forecasts.stream()
                .filter(forecast -> forecast.getPredictionDate()
                        .toString().equals(date))
                .collect(Collectors.toList());
        if (averageList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Map<Integer, Double> sumOfAverageList = new HashMap<>(); // än
        Map<Integer, Integer> hourCount = new HashMap<>();
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        for (Forecast forecast : averageList) {
            int hour = forecast.getPredictionHour();
            double temp = forecast.getPredictionTemperature();
            decimalFormat.format(forecast.getPredictionTemperature());

            sumOfAverageList.put(hour, sumOfAverageList.getOrDefault(hour, 0.0) + temp);
            hourCount.put(hour, hourCount.getOrDefault(hour, 0) + 1);

        }
        List<AverageForecastDTO> hourlyAverageTemp = new ArrayList<>();

        for (int hour : sumOfAverageList.keySet()) {
            double averageTemp = sumOfAverageList.get(hour) / hourCount.get(hour);
            hourlyAverageTemp.add(new AverageForecastDTO(hour, averageTemp));
        }
        return ResponseEntity.ok(hourlyAverageTemp);
    }


    @GetMapping("/api/forecasts/{dataSource}/averageTemp/{date}")
    public ResponseEntity<List<AverageForecastDTO>> getAverageTemperatureByDateAndDataSource(
            @PathVariable DataSource dataSource,
            @PathVariable String date) {
            //@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Forecast> forecasts = forecastService.getForecasts();

        // Filter forecasts by the specified data source and date
        List<Forecast> averageList= forecasts.stream()
                .filter(forecast -> forecast.getDataSource() == dataSource && forecast.getPredictionDate().toString().equals(date))
                .collect(Collectors.toList());

        if (averageList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        // Calculate the average temperature
        //double totalTemperature = filteredForecasts.stream()
                //.mapToDouble(Forecast::getPredictionTemperature)
                //.sum();

        //double averageTemperature = totalTemperature / filteredForecasts.size();

        //return ResponseEntity.ok(averageTemperature);

        Map<Integer, Double> sumOfAverageList = new HashMap<>(); // än
        Map<Integer, Integer> hourCount = new HashMap<>();
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        for (Forecast forecast : averageList) {
            int hour = forecast.getPredictionHour();
            double temp = forecast.getPredictionTemperature();
            decimalFormat.format(forecast.getPredictionTemperature());

            sumOfAverageList.put(hour, sumOfAverageList.getOrDefault(hour, 0.0) + temp);
            hourCount.put(hour, hourCount.getOrDefault(hour, 0) + 1);

        }
        List<AverageForecastDTO> hourlyAverageTemp = new ArrayList<>();

        for (int hour : sumOfAverageList.keySet()) {
            double averageTemp = sumOfAverageList.get(hour) / hourCount.get(hour);
            hourlyAverageTemp.add(new AverageForecastDTO(hour, averageTemp));
        }
        return ResponseEntity.ok(hourlyAverageTemp);


    }








    // GetMapping for Query
    @GetMapping("/api/forecasts/{provider}/avaragetemp/{predictionDate}")
    public ResponseEntity<List<HashMap<String, Object>>> getAverageTemperaturePerHour(
            @PathVariable("predictionDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PathVariable("provider") DataSource providerName) {

        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();

        List<Object[]> averageTemp = forecastRepository.findAllByAverageTempByProvider(date, currentHour, providerName);

        if (averageTemp.isEmpty()) {
            // Handle the case where no predictions are available for the given provider
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // You can choose an appropriate status code
        }

        // Convert List<Object[]> to List<HashMap<String, Object>>
        List<HashMap<String, Object>> result = new ArrayList<>();

        for (Object[] avarageTempbyprovider : averageTemp) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("date", avarageTempbyprovider[0]);
            map.put("hour", avarageTempbyprovider[1]);
            map.put("avgTemp", avarageTempbyprovider[2]);
            result.add(map);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}











