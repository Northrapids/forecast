package com.demo.forecast;

import com.demo.forecast.models.DataSource;
import com.demo.forecast.models.Forecast;
import com.demo.forecast.models.smhi.Geometry;
import com.demo.forecast.models.smhi.Parameter;
import com.demo.forecast.models.smhi.SmhiRoot;
import com.demo.forecast.models.smhi.TimeSeries;
import com.demo.forecast.models.visual.Day;
import com.demo.forecast.models.visual.Hour;
import com.demo.forecast.models.visual.VisualRoot;
import com.demo.forecast.repositories.ForecastRepository;
import com.demo.forecast.services.ForecastService;
import com.demo.forecast.services.SmhiService;
import com.demo.forecast.services.VisualService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;



@SpringBootApplication
public class ForecastApplication  implements CommandLineRunner {

	@Autowired
	private ForecastService forecastService;
	@Autowired
	private SmhiService smhiService;
	@Autowired
	private VisualService visualService;
	@Autowired
	private ForecastRepository forecastRepository;

	public static void main(String[] args) {
		SpringApplication.run(ForecastApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//var objectMapper = new ObjectMapper();

		var scan = new Scanner(System.in);

		while(true){
			System.out.println("******************* MENU *******************");
			System.out.println(" 1. List all forecasts");
			System.out.println(" 2. Create a new forecast");
			System.out.println(" 3. Update a forecast");
			System.out.println(" 4. Delete a forecast");
			System.out.println(" 5. Auto generate forecasts");
			System.out.println(" 6. fetch and save SMHI data to the database");
			System.out.println(" 7. fetch and save Visual data to the database");
			System.out.println("20. EXIT");
			System.out.println("*********************************************");
			System.out.print("Action:\t");

			int sel = scan.nextInt();
			if(sel == 1){
				listAllForecasts();
			} else if(sel == 2){
				createForecast(scan);
			} else if(sel == 3){
				updateForecast(scan);
			} else if(sel == 4){
				deleteAllForecasts(scan);
			} else if(sel == 5){
				generateForecasts();
			} else if(sel == 6){
				smhiService.fetchAndSaveSmhiDataToDB();
			} else if(sel == 7) {
				visualService.fetchAndSaveVisualDataToDB();
			} else if(sel == 20){
				break;
			} else if(sel == 9){
				smhiData();
			}
		}
	}

	private void smhiData() throws IOException {

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

		// Get the current time in milliseconds
		long currentTime = System.currentTimeMillis();

		// Calculate the duration of 24 hours in milliseconds
		long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;

		// Iterate through the list of time series data
		for (TimeSeries time : smhiRoot.getTimeSeries()) {
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


	private void updateForecast(Scanner scan) throws IOException {
		if (forecastService.getForecasts().isEmpty()) {
			System.out.println("\nThere are no forecasts to update!\n");
			return;
		}

		listAllForecasts();
		System.out.println("\n------------------------------");
		System.out.printf("Enter which one you want to update:\t");
		int num = scan.nextInt() ;
		var forecast = forecastService.getByIndex(num-1);
		System.out.println("-------------------------------------------------");
		System.out.printf("INDEX: %d DATE: %s HOUR: %d CURRENT TEMP: %f %n",
				num,
				forecast.getPredictionDate(),
				forecast.getPredictionHour(),
				forecast.getPredictionTemperature()
		);
		System.out.println("-------------------------------------------------");
		System.out.printf("Enter a new temperature:\t");
		double temp = scan.nextDouble() ;
		forecast.setPredictionTemperature(temp);
		forecastService.update(forecast);
	}


	public  static String longitude=" 18.02151508449004";
	public static  String latitude ="59.30996552541549";


	private void createForecast(Scanner scan) throws IOException {

		System.out.println("*** CREATE FORECAST PREDICTION ***");

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate date = null;
		int hour = 0;
		double temp = 0;
		boolean isRainOrSnow = false;

		boolean validInput = false;

		while (!validInput) {
			try {
				System.out.println("Date (yyyy-MM-dd):\t");
				String dateInput = scan.next();
				date = LocalDate.parse(dateInput,dateFormatter);

				System.out.print("Hour:\t");
				hour = scan.nextInt();

				System.out.print("Temperature:\t");
				temp = scan.nextDouble();

				System.out.println("Is it raining or snowing (true/false");
				isRainOrSnow = scan.nextBoolean();

				validInput = true;
			} catch (Exception e) {
				System.out.println("Invalid input!");
			}
		}

		var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setCreated(LocalDateTime.now());
		forecast.setPredictionDate(LocalDate.from(date.atStartOfDay()));
		forecast.setPredictionHour(hour);
		forecast.setPredictionTemperature(temp);
		forecast.setDataSource(DataSource.Console);
		forecast.setLatitude(Float.parseFloat(latitude));
		forecast.setLongitude(Float.parseFloat(longitude));
		forecast.setRainOrSnow(isRainOrSnow);
		forecastService.create(forecast);

		System.out.println("*** FORECAST PREDICTION CREATED! ***");

	}

	private void generateForecasts() {
		System.out.println("*** GENERATE FORECAST PREDICTIONS ***");

		// Get the current date and time
		LocalDateTime currentDateTime = LocalDateTime.now();

		Random random = new Random();

		// Generate and save predictions 24 hours ahead
		for (int i = 0; i < 25; i++) {
			LocalDateTime predictionDate = currentDateTime.plusHours(i);
			int predictionHour = predictionDate.getHour(); // Hour of the prediction
			double predictionTemp = random.nextDouble(16) + 10; // Generate a random temperature between 10 and 25 C

			// Simulate a 15% to 35% chance of rain or snow
			boolean rainOrSnow = random.nextDouble() <= 0.35 && random.nextDouble() >= 0.15;

			var forecast = new Forecast();
			forecast.setId(UUID.randomUUID());
			forecast.setCreated(LocalDateTime.now());
			forecast.setPredictionDate(predictionDate.toLocalDate());
			forecast.setPredictionHour(predictionHour);
			forecast.setPredictionTemperature(predictionTemp);
			forecast.setLatitude(Float.parseFloat(latitude));
			forecast.setLongitude(Float.parseFloat(longitude));
			forecast.setRainOrSnow(rainOrSnow);
			forecast.setDataSource(DataSource.ConsoleAutoGenerated);
			forecastService.create(forecast);
		}
		System.out.println("*** GENERATED FORECAST PREDICTIONS CREATED! ***");
	}

	private void listAllForecasts() {

		List<Forecast> forecasts = forecastService.getForecasts();

		if (forecasts.isEmpty()) {
			System.out.println("\nList is empty!\n");
			return;
		}

		int num = 1;

		for (var forecast : forecasts) {

			System.out.printf("(%d) Created: %s, Updated: %s, Longitude: %f, Latitude: %f, Prediction Date: %s, Prediction Hour: %d, Temperature: %f, Rain/Snow: %b, Data Source: %s%n",
					num,
					forecast.getCreated(),
					forecast.getUpdated(),
					forecast.getLongitude(),
					forecast.getLatitude(),
					forecast.getPredictionDate(),
					forecast.getPredictionHour(),
					forecast.getPredictionTemperature(),
					forecast.isRainOrSnow(),
					forecast.getDataSource()
			);
			num++;
		}

	}

	private void deleteAllForecasts(Scanner scan){
		//forecastRepository.deleteAll();
		System.out.println("Are you sure you want to delete all forecasts? (Y/N)");
		System.out.printf("Action:\t");
		String confirmation = scan.next().toLowerCase();

		if (confirmation.equals("y")) {
			forecastRepository.deleteAll();
			System.out.println("All forecasts have been deleted.");
		} else if (confirmation.equals("n")) {
			System.out.println("Deletion canceled. No forecasts were deleted.");
		} else {
			System.out.println("Invalid input. Deletion canceled. No forecasts were deleted.");
		}
	}

}
