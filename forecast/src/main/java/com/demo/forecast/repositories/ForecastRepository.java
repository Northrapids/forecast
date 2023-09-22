package com.demo.forecast.repositories;

import com.demo.forecast.models.DataSource;
import com.demo.forecast.models.Forecast;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ForecastRepository extends CrudRepository<Forecast, UUID> {
    @Override
    Optional<Forecast> findById(UUID id);

    List<Forecast>findAllByPredictionDate(LocalDate predictionDate);

    @Override
    List<Forecast> findAll();

    //@Override
    //List<Forecast> findByDataSource(String dataSource);

    @Query("SELECT f.predictionDate, f.predictionHour, AVG(f.predictionTemperature) " +
            "FROM Forecast f " +
            "WHERE (f.predictionDate = :predictionDate AND (f.predictionHour <= :currentHour OR :currentHour < 23)) " +
            "AND f.dataSource = :providerName " +
            "GROUP BY f.predictionDate, f.predictionHour " +
            "ORDER BY " +
            "   CASE " +
            "      WHEN f.predictionDate = :predictionDate AND f.predictionHour = :currentHour THEN 0 " +
            "      ELSE 1 " +
            "   END, " +
            "   f.predictionDate ASC, " +
            "   f.predictionHour ASC")
    List<Object[]> findAllByAverageTempByProvider(
            @Param("predictionDate") LocalDate date,
            @Param("currentHour") int currentHour,
            @Param("providerName") DataSource providerName);


}
