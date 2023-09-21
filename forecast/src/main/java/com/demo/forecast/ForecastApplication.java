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
			menu();

			//int sel = scan.nextInt();

			int sel;
			try {
				sel = scan.nextInt();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Invalid input. Please enter a valid number.");
				scan.nextLine(); // Clear the input buffer
				continue; // Restart the loop
			}

			if(sel == 1){
				listAllForecasts();
			} else if(sel == 2){
				createForecast(scan);
			} else if(sel == 3){
				updateForecast(scan);
			} else if(sel == 4){
				deleteForecastById(scan);
			} else if(sel == 5){
				generateForecasts();
			} else if(sel == 6){
				smhiService.fetchAndSaveSmhiDataToDB();
			} else if(sel == 7) {
				visualService.fetchAndSaveVisualDataToDB();
			} else if (sel == 8) {
				deleteAllForecasts(scan);
			} else if(sel == 20){
				break;
			} else {
				System.out.println("Invalid input!");
			}
		}
	}

	private void menu() {
		System.out.println("******************* MENU *******************");
		System.out.println(" 1. List all forecasts");
		System.out.println(" 2. Create a new forecast");
		System.out.println(" 3. Update a forecast");
		System.out.println(" 4. Delete a forecast");
		System.out.println(" 5. Auto generate forecasts");
		System.out.println(" 6. fetch and save SMHI data to the database");
		System.out.println(" 7. fetch and save Visual data to the database");
		System.out.println(" 8. Delete all forecasts");
		System.out.println("20. EXIT");
		System.out.println("*********************************************");
		System.out.print("Action:\t");
	}

	private void updateForecast(Scanner scan) throws IOException {
		if (forecastService.getForecasts().isEmpty()) {
			System.out.println("\nThere are no forecasts to update!\n");
			return;
		}

		listAllForecasts();
		System.out.println("\n------------------------------");
		System.out.printf("Enter which one you want to update:\t");

		int num;

		try {
			num = scan.nextInt();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Invalid input! Please enter a valid number.");
			scan.nextLine(); // Clear the input buffer
			return;
		}

		try {
			var forecast = forecastService.getByIndex(num - 1);
			System.out.println("-------------------------------------------------");
			System.out.printf("INDEX: %d ID:%s DATE: %s HOUR: %d CURRENT TEMP: %f %n",
					num,
					forecast.getId(),
					forecast.getPredictionDate(),
					forecast.getPredictionHour(),
					forecast.getPredictionTemperature()
			);
			System.out.println("-------------------------------------------------");
			System.out.printf("Enter a new temperature:\t");

			double temp = scan.nextDouble();
			forecast.setPredictionTemperature(temp);
			forecastService.update(forecast);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Invalid index! Please select a valid forecast to update.");
		} catch (java.util.InputMismatchException e) {
			System.out.println("Invalid input! Please enter a valid temperature.");
		}
	}


	private void deleteForecastById(Scanner scan) {
		listAllForecasts();
		System.out.print("Enter the ID of the forecast to delete: ");

		UUID id;

		try {
			id = UUID.fromString(scan.next());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid ID format! Please enter a valid UUID.");
			return;
		}

		try {
			forecastService.delete(id);
			System.out.println("Forecast with ID " + id + " has been deleted.");
		} catch (Exception e) {
			System.out.println("An error occurred while deleting the forecast.");
		}
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

			System.out.printf("(%d) Id:%s Created: %s, Updated: %s, Longitude: %f, Latitude: %f, Prediction Date: %s, Prediction Hour: %d, Temperature: %f, Rain/Snow: %b, Data Source: %s%n",
					num,
					forecast.getId(),
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


	private void deleteAllForecasts(Scanner scan) {
		System.out.println("Are you sure you want to delete all forecasts? (Y/N)");
		System.out.printf("Action:\t");
		String confirmation;

		try {
			confirmation = scan.next().toLowerCase();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Invalid input format! Please enter 'Y' or 'N'.");
			scan.nextLine(); // Clear the input buffer
			return;
		}

		if (confirmation.equals("y")) {
			// Add try-catch block here to handle potential exceptions when deleting forecasts
			try {
				forecastRepository.deleteAll();
				System.out.println("All forecasts have been deleted.");
			} catch (Exception e) {
				System.out.println("An error occurred while deleting forecasts.");
			}
		} else if (confirmation.equals("n")) {
			System.out.println("Deletion canceled. No forecasts were deleted.");
		} else {
			System.out.println("Invalid input. Deletion canceled. No forecasts were deleted.");
		}
	}

}
