package com.demo.forecast;

import com.demo.forecast.dto.AverageForecastDTO;
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
			System.out.println("5. Auto generate forecasts");
			System.out.println("6. fetch and save SMHI data to the database");
			System.out.println("7. fetch and save Visual data to the database");
			System.out.println("20. EXIT");
			System.out.println("*********************************************");
			System.out.print("Action:\t");


			//System.out.println("8. Delete all forecasts form database!");
			//System.out.println("9. Auto generate forecasts");
			//System.out.println("10. fetch and save VISUAL data to the database");
			//System.out.println("20. Exit");
			//System.out.println("*********************************************");
			//System.out.print("Action:\t");

			int sel = scan.nextInt();
			if(sel == 1){
				listAllForecasts();
			} else if(sel == 2){
				addForecast(scan);
			}else if(sel == 3){
				updateForecast(scan);
			}else if(sel == 5){
				//smhiData();
				generateForecasts();
			}else if(sel == 6){
				//visualData();
				fetchAndSaveSmhiDataToDB();
			}else if(sel == 7){
				// smhiService.fetchAndSaveToDB();
				//fetchAndSaveSmhiDataToDB();
				fetchAndSaveVisualDataToDB();
			}else if(sel == 8){
				deleteAllForecasts();
			}else if(sel == 9){
				//addGeneratedPredictions(forecastService);
				generateForecasts();
			}else if(sel == 10){
				//fetchVisualAndSaveToDB();
			}else if(sel == 11){
				calculateAverage();
			} else if(sel == 20){
				break;
			}
		}
	}

/*
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

 */

	public void fetchAndSaveSmhiDataToDB() throws IOException {
		var objectMapper = new ObjectMapper();

		// Fetch weather forecast data from the SMHI API
		SmhiRoot smhiRoot = objectMapper.readValue(new URL
						("https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/18/lat/59/data.json"),
				SmhiRoot.class);
		List<TimeSeries> timeseriesList = smhiRoot.getTimeSeries();

		Date currentTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentTime);
		calendar.add(Calendar.HOUR_OF_DAY, 25);
		Date tomorrow = calendar.getTime();
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

							System.out.println("----------------------------");
							System.out.println("date:\t" + validLocalDate);
							System.out.println("hour:\t" + hour);
							System.out.println("temp:\t" + paramValue);
							System.out.println("rain or snow:\t" + rainOrSnow);

							forecastFromSmhi.setId(UUID.randomUUID());
							forecastFromSmhi.setRainOrSnow(rainOrSnow);
							forecastFromSmhi.setPredictionTemperature(paramValue);
							forecastFromSmhi.setPredictionDate(validLocalDate);
							forecastFromSmhi.setPredictionHour(hour);
							forecastFromSmhi.setDataSource(DataSource.Smhi);
							forecastFromSmhi.setCreated(LocalDateTime.now());
							forecastFromSmhi.setLatitude(latitude);
							forecastFromSmhi.setLongitude(longitude);
							forecastRepository.save(forecastFromSmhi);

						}
					}
				}
			}
		}
	}


	private void fetchAndSaveVisualDataToDB() throws IOException {
		var objectMapper = new ObjectMapper();

		// Fetch weather forecast data from the visual API
        /*
		VisualRoot visualRoot = objectMapper.readValue(new URL
						("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Liljeholmen/next24hours?unitGroup=metric&elements=datetime%2CdatetimeEpoch%2Cname%2Caddress%2CresolvedAddress%2Clatitude%2Clongitude%2Ctemp%2Cprecip%2Cprecipprob%2Cprecipcover%2Cpreciptype%2Csnow%2Csnowdepth&key=NV2YVV3CH289TE5AKK9MECDUY&contentType=json"),
				VisualRoot.class);

         */
        VisualRoot visualRoot = objectMapper.readValue(new URL
                ("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/liljeholmen/next24hours?unitGroup=metric&elements=datetime%2CdatetimeEpoch%2Ctemp%2Cprecipprob%2Cpreciptype%2Csnow&key=NV2YVV3CH289TE5AKK9MECDUY&contentType=json"),
				VisualRoot.class);

		long currentTimestamp = System.currentTimeMillis() / 1000;

		for (Day day : visualRoot.getDays()) {


			for (Hour time : day.getHours()) {


				long hourDatetimeEpoch = time.getDatetimeEpoch();

				// Check if the timestamp is within the next 24 hours
				if (hourDatetimeEpoch >= currentTimestamp && hourDatetimeEpoch <= currentTimestamp + 25 * 3600) {
					String hourDatetime = time.getDatetime();
					double hourTemp = time.getTemp();
					System.out.println("-----------------------------");

					System.out.println("Date:\t" + day.getDatetime());
					System.out.println("Address:\t" + visualRoot.getAddress());
					System.out.println("Timezone:\t" + visualRoot.getTimezone());
					System.out.println("Hour:\t" + hourDatetime);
					System.out.println("Temp:\t" + hourTemp);
					//System.out.println("Rain:\t" + day.getPrecip());
					//System.out.println("Snow:\t" + day.getSnow());


					Forecast visualForecast = new Forecast();


					Calendar calendar = Calendar.getInstance();

					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					visualForecast.setPredictionHour(hour); // set hour in database
					// Now, you can use `hour` as an integer if needed elsewhere

					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Adjust the pattern as needed
					LocalDate parsedDate = LocalDate.parse(day.getDatetime(), dateFormatter);

					double visualLatitude =  visualRoot.getLatitude();
					double visualLongitude=  visualRoot.getLongitude();

					if (time.getPrecip() > 0) {
						visualForecast.setRainOrSnow(true); // You can customize this based on your requirements
						//System.out.println("Saved - Precipitation: Rain");
						System.out.println("Precipitation: Rain");
					} else if (time.getSnow() > 0) {
						visualForecast.setRainOrSnow(true); // You can customize this based on your requirements
						//System.out.println("Saved - Precipitation: Snow");
						System.out.println("Precipitation: Snow");
					} else {
						visualForecast.setRainOrSnow(false); // No precipitation
						//System.out.println("Saved - Precipitation: None");
						System.out.println("Precipitation: None");
					}

					visualForecast.setPredictionDate(parsedDate);
					visualForecast.setPredictionTemperature(hourTemp);
					visualForecast.setCreated(LocalDateTime.now());
					visualForecast.setLongitude(visualLongitude);
					visualForecast.setLatitude(visualLatitude);
                    visualForecast.setDataSource(DataSource.Visual);
					//visualForecast.setDataSource(DataSource.Visual);


					forecastRepository.save(visualForecast);

				}
			}
		}
		System.out.println("\n**************** COMPLETED! ****************\n");


        /*

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

					//Instant dayInstant = Instant.ofEpochSecond(day.getDatetimeEpoch());

					Forecast visualForecast = new Forecast();

					Calendar calendar = Calendar.getInstance();

					int hour = calendar.get(Calendar.HOUR_OF_DAY);

					visualForecast.setPredictionHour(hour);

					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDate paresdate = LocalDate.parse(day.getDat)
				}
			}
		}
		*/

	}

    /*
    public void fetchVisualAndSaveToDB() throws IOException {
        var objectMapper = new ObjectMapper();

        // Fetch weather forecast data from the Visual Crossing API
        VisualRoot visualRoot = objectMapper.readValue(new URL(
                        "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Liljeholmstorget%207%2C%20117%2063%20Stockholm/2023-08-31/2024-08-31?unitGroup=metric&elements=datetime%2CdatetimeEpoch%2Ctemp%2Cpreciptype%2Csnow&key=NV2YVV3CH289TE5AKK9MECDUY&contentType=json"),
                VisualRoot.class);

        long currentTimeMillis = System.currentTimeMillis();
        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;

        for (Day day : visualRoot.getDays()) {
            Instant dayInstant = Instant.ofEpochSecond(day.getDatetimeEpoch());

            if (dayInstant.toEpochMilli() >= currentTimeMillis && dayInstant.toEpochMilli() <= currentTimeMillis + twentyFourHoursInMillis) {
                for (Hour hour : day.getHours()) {
                    Instant hourInstant = Instant.ofEpochSecond(hour.getDatetimeEpoch());
                    LocalDateTime dayDateTime = LocalDateTime.parse(day.getDatetime());
                    LocalDateTime hourDateTime = LocalDateTime.parse(hour.getDatetime());

                    if (hourInstant.toEpochMilli() >= currentTimeMillis && hourInstant.toEpochMilli() <= currentTimeMillis + twentyFourHoursInMillis) {
                        // Create a new forecast and populate it with data from the Visual Crossing API
                        Forecast forecastFromVisual = new Forecast();
                        forecastFromVisual.setId(UUID.randomUUID());
                        forecastFromVisual.setPredictionTemperature(hour.getTemp());
                        //forecastFromVisual.setRainOrSnow("Snow".equalsIgnoreCase(hour.getPreciptype()));
                        forecastFromVisual.setPredictionDate(dayDateTime.toLocalDate());
                        forecastFromVisual.setPredictionHour(hourDateTime.getHour());
                        forecastFromVisual.setDataSource(DataSource.Visual);
                        forecastFromVisual.setCreated(LocalDateTime.now());
                        forecastFromVisual.setLatitude(visualRoot.getLatitude());
                        forecastFromVisual.setLongitude(visualRoot.getLongitude());

                        // Save the forecast to the database
                        forecastRepository.save(forecastFromVisual);

                        // Print forecast details (optional)
                        //System.out.println("Date: " + day.getDatetime());
                        //System.out.println("Hour: " + hour.getDatetime());
                        //System.out.println("Temperature: " + hour.getTemp());
                        //System.out.println("Precipitation Type: " + hour.getPreciptype());
                        //System.out.println("Snow: " + hour.getSnow());
                        //System.out.println("+----------------------------------------------------+");
                    }
                }
            }
        }
    }

     */
    /*
	public void fetchVisualAndSaveToDB() throws IOException {
		var objectMapper = new ObjectMapper();

		// Fetch weather forecast data from the Visual Crossing API
		VisualRoot visualRoot = objectMapper.readValue(new URL(
						"https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Liljeholmstorget%207%2C%20117%2063%20Stockholm/2023-08-31/2024-08-31?unitGroup=metric&elements=datetime%2CdatetimeEpoch%2Ctemp%2Cpreciptype%2Csnow&key=NV2YVV3CH289TE5AKK9MECDUY&contentType=json"),
				VisualRoot.class);

		System.out.println("+------------------------------------------------------------------------+");
		System.out.println("Latitude: " + visualRoot.getLatitude());
		System.out.println("Longitude: " + visualRoot.getLongitude());
		System.out.println("Address: " + visualRoot.getAddress());
		System.out.println("+------------------------------------------------------------------------+");

		long currentTimeMillis = System.currentTimeMillis();
		long twentyFourHoursInMillis = 24 * 60 * 60 * 1000;

		for (Day day : visualRoot.getDays()) {

			for (Hour hour : day.getHours()) {
				long
				if (day.toEpochMilli() >= currentTimeMillis && dayInstant.toEpochMilli() <= currentTimeMillis + twentyFourHoursInMillis) {
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
			Instant dayInstant = Instant.ofEpochSecond(day.getDatetimeEpoch());

			Forecast visualForecast = new Forecast();

			Calendar calendar = Calendar.getInstance();

			int hour = calendar.get(Calendar.HOUR_OF_DAY);

			visualForecast.setPredictionHour(hour);

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate paresdate = LocalDate.parse(day.getDat)

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

	 */


	// Create a Calendar instance and set it to the current time
        /*
		Calendar calendar = Calendar.getInstance();
		long currentTimeMillis = System.currentTimeMillis();
		calendar.setTimeInMillis(currentTimeMillis);

		// Calculate tomorrow's date and time using Calendar
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		long tomorrowMillis = calendar.getTimeInMillis();
		*/

    /*

		// Iterate over the data and save forecasts
		for (Day day : visualRoot.getDays()) {
			Instant dayInstant = Instant.ofEpochSecond(day.getDatetimeEpoch());


			if (dayInstant.toEpochMilli() >= currentTimeMillis && dayInstant.toEpochMilli() <= tomorrowMillis) {
				for (Hour hour : day.getHours()) {
					Instant hourInstant = Instant.ofEpochSecond(hour.getDatetimeEpoch());
					LocalDateTime dayDateTime = LocalDateTime.parse(day.getDatetime());
					String dateStr = day.getDatetime();
					LocalDate validLocalDate = LocalDate.parse(dateStr);
					//LocalDateTime hourDateTime = LocalDateTime.parse(hour.getDatetime());

					if (hourInstant.toEpochMilli() >= currentTimeMillis && hourInstant.toEpochMilli() <= tomorrowMillis) {
						// Create a new forecast and populate it with data from the Visual Crossing API
						Forecast forecastFromVisual = new Forecast();
						forecastFromVisual.setId(UUID.randomUUID());
						forecastFromVisual.setPredictionTemperature(hour.getTemp());
						//forecastFromVisual.setRainOrSnow("Snow".equalsIgnoreCase(hour.getPreciptype()));
						//forecastFromVisual.setPredictionDate(day.getDatetime().toLocalDate());
						forecastFromVisual.setPredictionDate(validLocalDate);
						forecastFromVisual.setPredictionHour(hourInstant.atZone(ZoneId.systemDefault()).toLocalTime().getHour());
						forecastFromVisual.setDataSource(DataSource.Visual);
						forecastFromVisual.setCreated(LocalDateTime.now());
						forecastFromVisual.setLatitude(visualRoot.getLatitude());
						forecastFromVisual.setLongitude(visualRoot.getLongitude());

						// Save the forecast to the database
						forecastRepository.save(forecastFromVisual);

					}
				}
			}
		}
	}

	 */

	private void updateForecast(Scanner scan) throws IOException {
		listAllForecasts();
		System.out.println("\n------------------------------");
		System.out.printf("Ange vilken du vill uppdatera:\t");
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
		System.out.printf("Ange ny temp:\t");
		int temp = scan.nextInt() ;
		forecast.setPredictionTemperature(temp);
		forecastService.update(forecast);
	}

	private void addForecast(Scanner scan) throws IOException {
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

	private void generateForecasts() {
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

	private void listAllForecasts() {
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

	private void calculateAverage() {
		//var day = LocalDate.now();
		//List<AverageForecastDTO> dtos = forecastService.calculateAverage(day);
	}

	public void deleteAllForecasts(){
		forecastRepository.deleteAll();
	}

}
