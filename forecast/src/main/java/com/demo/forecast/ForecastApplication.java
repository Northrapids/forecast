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
import com.demo.forecast.services.SMHIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
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
	private SMHIService smhiService;
	@Autowired
	private ForecastRepository forecastRepository;

	public static void main(String[] args) {
		SpringApplication.run(ForecastApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		var objectMapper = new ObjectMapper();

        /*

		var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setPredictionTemperature(12);
		forecast.setPredictionDatum(LocalDate.now());
		forecast.setPredictionDatum2(Instant.now());
		forecast.setPredictionHour(12);
		forecast.setDataSource(DataSource.Console);




		String json = objectMapper.writeValueAsString(forecast);
		System.out.println(json);

		 */


		// Forecast forecast2 = objectMapper.readValue(json,Forecast.class);


		var scan = new Scanner(System.in);

		while(true){
			System.out.println("******************* MENU *******************");
			System.out.println("1. List all forecasts");
			System.out.println("2. Create a new forecast");
			System.out.println("3. Update a forecast");
			System.out.println("4. Delete a forecast");
			System.out.println("5. View SMHI data");
			System.out.println("6. View Visual data");
			System.out.println("7. fetch and save SMHI data to the database");
			System.out.println("8. Delete all forecasts form database!");
			System.out.println("9. Auto generate forecasts");
			System.out.println("10. Exit");
			System.out.println("*********************************************");
			System.out.print("Action:\t");

			int sel = scan.nextInt();
			if(sel == 1){
				listPredictions();
			} else if(sel == 2){
				addPrediction(scan);
			}else if(sel == 3){
				updatePrediction(scan);
			}else if(sel == 5){
				smhiData();
			}else if(sel == 6){
				//visualData();
			}else if(sel == 7){
				// smhiService.fetchAndSaveToDB();
				fetchAndSaveToDB();
			}else if(sel == 8){
				deleteAll();
			}else if(sel == 9){
				//addGeneratedPredictions(forecastService);
				generatePredictions();
			} else if(sel == 10){
				break;
			}
		}
	}

    /*

	// old - pre db

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
			System.out.println("6. Visual");
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
			}else if(sel == 6){
				visualData();
			}
			else if(sel == 9){
				break;
			}
		}
	}

	*/


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

	public void fetchAndSaveToDB() throws IOException {
		var objectMapper = new ObjectMapper();

		// Fetch weather forecast data from the SMHI API
		SmhiRoot smhiRoot = objectMapper.readValue(new URL
						("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/18/lat/59/data.json"),
				SmhiRoot.class);
		List<TimeSeries> timeseriesList = smhiRoot.getTimeSeries();

		java.util.Date currentTime = new java.util.Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentTime);
		calendar.add(Calendar.HOUR_OF_DAY, 25);
		java.util.Date tomorrow = calendar.getTime();
		for (TimeSeries timeSeries : timeseriesList) {
			Date validTime = timeSeries.getValidTime();
			calendar.setTime(validTime);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

			LocalDate validLocalDate = validTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			if (validTime.after(currentTime) && validTime.before(tomorrow) &&
					hour == currentHour) {
				for (Parameter param : timeSeries.getParameters()) {
					String paraName = param.getName();
					var forecastFromSmhi = new Forecast();
					ArrayList<Float> values = param.getValues();

					Boolean rainOrSnow = false;

					double latitude = 1.0d;
					double longitude = 1.0d;


					for (Float paramValue : values) {
						if ("t".equals(paraName) || "pcat".equals(paraName)) {
							if (paramValue == 3.0 || paramValue == 1) {
								rainOrSnow = true;
							}
						}

                        Geometry geometry = smhiRoot.getGeometry();
                        ArrayList<ArrayList<Double>> coordinates = geometry.getCoordinates();
                        for(ArrayList<Double> coordinate : coordinates) {
                            latitude = coordinate.get(1);
                            longitude = coordinate.get(0);
                        }

						if ("t".equals(paraName)) {

							System.out.println("tid: " + hour);
							System.out.println("temp: " + paramValue);
							System.out.println("tid: " + validLocalDate);

							forecastFromSmhi.setId(UUID.randomUUID());
							forecastFromSmhi.setRainOrSnow(rainOrSnow);
							forecastFromSmhi.setPredictionTemperature(paramValue);
							forecastFromSmhi.setPredictionDate(validLocalDate);
							forecastFromSmhi.setPredictionHour(hour);
							forecastFromSmhi.setDataSource(DataSource.Smhi);
							forecastFromSmhi.setCreated(LocalDateTime.now());
							forecastFromSmhi.setLatitude(latitude);
							//forecastFromSmhi.setLatitude(59.3154f);
							forecastFromSmhi.setLongitude(longitude);
							//forecastFromSmhi.setLongitude(18.0382f);
							forecastRepository.save(forecastFromSmhi);

						}
					}
				}
			}
		}
	}

    /*
	public void fetchAndSaveToDB() throws IOException {
		var objectMapper = new ObjectMapper();

		// Fetch weather forecast data from the SMHI API
		SmhiRoot smhiRoot = objectMapper.readValue(new URL
						("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/18/lat/59/data.json"),
				SmhiRoot.class);
		List<TimeSeries> timeseriesList = SmhiRoot.getTimeSeries(); // getTimeSeries är static i SmhiRoot för att denna ska funka

		java.util.Date currentTime = new java.util.Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentTime);
		calendar.add(Calendar.HOUR_OF_DAY, 25);
		java.util.Date tomorrow = calendar.getTime();
		for (TimeSeries timeSeries : timeseriesList) {
			Date validTime = timeSeries.getValidTime();
			calendar.setTime(validTime);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

			LocalDate validLocalDate = validTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			if(validTime.after(currentTime) && validTime.before(tomorrow) &&
					hour == currentHour) {
				for(Parameter param : timeSeries.getParameters()) {
					String paraName = param.getName();
					var forecastFromSmhi = new Forecast();
					ArrayList<Double> values = param.getValues();

					Boolean rainOrSnow = false;

					for(Double paramValue :values) {
						if("t".equals(paraName) || "pcat".equals(paraName))
						{
							if(paramValue == 3.0 && paramValue == 1) {
								rainOrSnow = true;
							}
						}

						if ("t".equals(paraName)) {

							System.out.println("tid: " + hour);
							System.out.println("temp: " + paramValue);
							System.out.println("tid: " + validLocalDate);

							forecastFromSmhi.setId(UUID.randomUUID());
							forecastFromSmhi.setRainOrSnow(rainOrSnow);
							//forecastFromSmhi.setPredictionTemperature(paramValue);
							forecastFromSmhi.setPredictionDate(validLocalDate);
							forecastFromSmhi.setPredictionHour(hour);
							forecastFromSmhi.setDataSource(DataSource.Smhi);
							forecastRepository.save(forecastFromSmhi);

						}
					}
				}
			}
		}
	}

	 */


	private void visualData() throws IOException {
		var objectMapper = new ObjectMapper();

		// Fetch weather forecast data from the visual API
		VisualRoot visualRoot = objectMapper.readValue(new URL
						("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Liljeholmstorget%207%2C%20117%2063%20Stockholm/2023-08-31/2024-08-31?unitGroup=metric&elements=datetime%2CdatetimeEpoch%2Ctemp%2Cpreciptype%2Csnow&key=NV2YVV3CH289TE5AKK9MECDUY&contentType=json"),
				VisualRoot.class);

		System.out.println("+------------------------------------------------------------------------+");
		System.out.println("Latitude: " + visualRoot.getLatitude());
		System.out.println("Longitude: " + visualRoot.getLongitude());
		System.out.println("Address: " + visualRoot.getAddress());
		System.out.println("+------------------------------------------------------------------------+");

		long currentTimeMillis = System.currentTimeMillis();
		long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;

		for (Day day : visualRoot.getDays()) {
			Instant dayInstant = Instant.ofEpochSecond(day.getDatetimeEpoch());

			if (dayInstant.toEpochMilli() >= currentTimeMillis && dayInstant.toEpochMilli() <= currentTimeMillis + twentyFourHoursInMillis) {
				System.out.println("Date: " + day.getDatetime());
				System.out.println("Temp: " + day.getTemp());
				System.out.println("Snow: " + day.getSnow());
				System.out.println("Precipitation Types: " + day.getPreciptype());
				System.out.println("+----------------------------------------------------+");

				for (Hour hour : day.getHours()) {
					Instant hourInstant = Instant.ofEpochSecond(hour.getDatetimeEpoch());

					if (hourInstant.toEpochMilli() >= currentTimeMillis && hourInstant.toEpochMilli() <= currentTimeMillis + twentyFourHoursInMillis) {
						System.out.println("Hour: " + hour.getDatetime());
						System.out.println("Temp: " + hour.getTemp());
						System.out.println("Snow: " + hour.getSnow());
						System.out.println("Precipitation Types: " + hour.getPreciptype());
						System.out.println("+----------------------------------------------------+");
					}
				}
			}
		}
	}

	private void updatePrediction(Scanner scan) throws IOException {
		listPredictions();
		System.out.printf("Ange vilken du vill uppdatera:");
		int num = scan.nextInt() ;
		var forecast = forecastService.getByIndex(num-1);
		System.out.printf("%d %d CURRENT: %f %n",
				forecast.getPredictionDate(),
				forecast.getPredictionHour(),
				forecast.getPredictionTemperature()
		);
		System.out.printf("Ange ny temp:");
		int temp = scan.nextInt() ;
		forecast.setPredictionTemperature(temp);
		forecastService.update(forecast);
	}

	private void addPrediction(Scanner scan) throws IOException {
		//Input på dag, hour, temp
		//Anropa services - Save
		System.out.println("*** CREATE FORECAST PREDICTION ***");

		System.out.printf("Date (yyyy-mm-dd):\t");
		String date = scan.next();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dateFormatted = LocalDate.parse(date,dateFormatter);

		System.out.print("Hour:\t");
		int hour = scan.nextInt();

		System.out.print("Temperature:\t");
		Double temp = scan.nextDouble();

		var forecast = new Forecast();
		forecast.setId(UUID.randomUUID());
		forecast.setCreated(LocalDateTime.now());
		forecast.setPredictionDate(LocalDate.from(dateFormatted.atStartOfDay()));
		forecast.setPredictionHour(hour);
		forecast.setPredictionTemperature(temp);
		forecast.setDataSource(DataSource.Console);
		forecastService.add(forecast);

		System.out.println("*** FORECAST PREDICTION CREATED! ***");

	}

	private void generatePredictions() {
		System.out.println("*** GENERATE FORECAST PREDICTIONS ***");

		// Get the current date and time
		LocalDateTime currentDateTime = LocalDateTime.now();

		Random random = new Random();

		// Generate and save predictions 24 hours ahead
		for (int i = 0; i < 25; i++) {
			LocalDateTime predictionDate = currentDateTime.plusHours(i);
			int predictionHour = predictionDate.getHour(); // Hour of the prediction
			double predictionTemp = random.nextDouble(16) + 10; // Generate a random temperature between 10 and 25 Celsius

			var forecast = new Forecast();
			forecast.setId(UUID.randomUUID());
			forecast.setCreated(LocalDateTime.now());
			forecast.setPredictionDate(predictionDate.toLocalDate());
			forecast.setPredictionHour(predictionHour);
			forecast.setPredictionTemperature(predictionTemp);
			forecast.setDataSource(DataSource.ConsoleAutoGenerated);
			forecastService.add(forecast);

			System.out.println("*** GENERATED FORECAST PREDICTIONS CREATED! ***");

		}
	}

	private void listPredictions() {
		int num = 1;

		for(var forecast : forecastService.getForecasts()){

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

	public void deleteAll(){
		forecastRepository.deleteAll();
	}

}
