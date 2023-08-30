package com.demo.forecast;

import com.demo.forecast.models.Forecast;
import com.demo.forecast.models.smhi.Parameter;
import com.demo.forecast.models.smhi.Smhi;
import com.demo.forecast.models.smhi.TimeSeries;
import com.demo.forecast.services.ForecastService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

@SpringBootApplication
public class ForecastApplication  implements CommandLineRunner {

	@Autowired
	private ForecastService forecastService;

	public static void main(String[] args) {
		SpringApplication.run(ForecastApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		var objectMapper = new ObjectMapper();

		var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setTemperature(12f);
		forecast.setDate(20230101);
		forecast.setHour(12);


		String json = objectMapper.writeValueAsString(forecast);
		System.out.println(json);


		Forecast forecast2 = objectMapper.readValue(json,Forecast.class);


		var scan = new Scanner(System.in);

		while(true){
			System.out.println("1. List all");
			System.out.println("2. Create");
			System.out.println("3. Update");
			System.out.println("4. Delete");
			System.out.println("5. Smhi");
			System.out.println("6. openweathermap");
			System.out.println("9. Exit");
			System.out.print("Action:");
			int sel = scan.nextInt();
			if(sel == 1){
				listPredictions();
			} else if(sel == 2){
				addPrediction(scan);
			}else if(sel == 3){
				updatePrediction(scan);
			}else if(sel == 5){
				smhiData();
			}
			else if(sel == 9){
				break;
			}
		}
	}

	private void smhiData() throws IOException {

		var objectMapper = new ObjectMapper();

		// Fetch weather forecast data from the SMHI API
		Smhi smhi = objectMapper.readValue(new URL
						("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/18/lat/59/data.json"),
				Smhi.class);

		System.out.println("+------------------------------------------------------------------------+");
		System.out.println("approvedTime " + smhi.getApprovedTime());
		System.out.println("referenceTime " + smhi.getReferenceTime());
		System.out.println("Location: " + smhi.getGeometry());
		System.out.println("+------------------------------------------------------------------------+");

		// Get the current time in milliseconds
		long currentTime = System.currentTimeMillis();

		// Calculate the duration of 24 hours in milliseconds
		long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;

		// Iterate through the list of time series data
		for (TimeSeries time : smhi.getTimeSeries()) {
			long forecastTime = time.getValidTime().toInstant().toEpochMilli();

			// Check if the forecast time is within the next 24 hours from the current time
			if (forecastTime > currentTime && forecastTime <= currentTime + twentyFourHoursInMillis) {
				System.out.println("validtime: " + time.getValidTime() + "\n");

				// Iterate through parameter data for the current time series
				for (Parameter parameter : time.getParameters()) {
					String paramName = parameter.getName();
					if (paramName.equals("t") || paramName.equals("pcat")) {
						System.out.println("name: " + paramName);
						System.out.println("level type: " + parameter.getLevelType());
						System.out.println("level: " + parameter.getLevel());
						System.out.println("unit:" + parameter.getUnit());
						System.out.println("values: " + parameter.getValues());

					}

				}
				System.out.println("+----------------------------------------------------+");

			}

			// Break the loop once data for the next 24 hours has been processed
			if (forecastTime > currentTime + twentyFourHoursInMillis) {
				break;
			}
		}
	}

	private void openweathermapData() throws IOException {

	}

	private void updatePrediction(Scanner scan) throws IOException {
		listPredictions();
		System.out.printf("Ange vilken du vill uppdatera:");
		int num = scan.nextInt() ;
		var forecast = forecastService.getByIndex(num-1);
		System.out.printf("%d %d CURRENT: %f %n",
				forecast.getDate(),
				forecast.getHour(),
				forecast.getTemperature()
		);
		System.out.printf("Ange ny temp:");
		float temp = scan.nextFloat() ;
		forecast.setTemperature(temp);
		forecastService.update(forecast);
	}

	private void addPrediction(Scanner scan) throws IOException {
		//Input på dag, hour, temp
		//Anropa services - Save
		System.out.println("*** CREATE FORECAST PREDICTION ***");
		System.out.printf("Date (yy mm dd):");
		int date = scan.nextInt();
		System.out.print("Hour:");
		int hour = scan.nextInt();
		System.out.print("Temperature:");
		float temp = scan.nextFloat();

		var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setDate(date);
		forecast.setHour(hour);
		forecast.setTemperature(temp);

		forecastService.add(forecast);
	}

	private void listPredictions() {
		int num = 1;
		for(var forecast : forecastService.getForecasts()){

			System.out.printf("(%d) %d %d %f %n",
					num,
					forecast.getDate(),
					forecast.getHour(),
					forecast.getTemperature());
			num++;
		}
	}

}
