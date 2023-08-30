package com.demo.forecast.controllers;

import com.demo.forecast.models.Forecast;
import com.demo.forecast.services.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ForecastController {

    @Autowired
    private ForecastService forecastService;
    @GetMapping("/api/forecasts")
    public ResponseEntity<List<Forecast>> getAll(){
        return new ResponseEntity<>(forecastService.getForecasts(), HttpStatus.OK);
    }

    @GetMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Get(@PathVariable UUID id){
        Optional<Forecast> forecast = forecastService.get(id);
        if(forecast.isPresent()) return ResponseEntity.ok(forecast.get());
        return  ResponseEntity.notFound().build();
    }

    @PutMapping("/api/forecasts/{id}")
    public ResponseEntity<Forecast> Update(@PathVariable UUID id, @RequestBody Forecast forecast) throws IOException {
        forecastService.update(forecast);
        return ResponseEntity.ok(forecast);
    }

    @PostMapping("/api/forecasts")
    public ResponseEntity<Forecast> add( @RequestBody Forecast forecast) throws IOException{
        forecast.setId(UUID.randomUUID());
        forecastService.add(forecast);
        return ResponseEntity.ok(forecast); // mer REST ful = created (204) samt url till produkten
    }

    @DeleteMapping("/api/forecasts/{id}")
    public ResponseEntity<String> Delete(@PathVariable UUID id ) throws IOException {
        forecastService.delete(id);
        return ResponseEntity.ok("Deleted");
    }






}
