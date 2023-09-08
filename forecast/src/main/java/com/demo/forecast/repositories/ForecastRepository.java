package com.demo.forecast.repositories;

import com.demo.forecast.models.Forecast;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ForecastRepository extends CrudRepository<Forecast, UUID> {
    @Override
    List<Forecast> findAll();
}
