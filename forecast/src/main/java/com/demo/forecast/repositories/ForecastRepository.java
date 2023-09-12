package com.demo.forecast.repositories;

import com.demo.forecast.models.Forecast;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForecastRepository extends CrudRepository<Forecast, UUID> {
    @Override
    Optional<Forecast> findById(UUID id);

    List<Forecast>findAllByPredictionDate(LocalDate predictionDate);

    @Override
    List<Forecast> findAll();
}
