package me.yattaw.snowingtoday.service;

import com.fasterxml.jackson.databind.JsonNode;
import me.yattaw.snowingtoday.SnowingTodayApplication;
import me.yattaw.snowingtoday.data.LocationData;
import me.yattaw.snowingtoday.data.SchoolPrediction;
import me.yattaw.snowingtoday.data.SnowData;
import me.yattaw.snowingtoday.data.SnowFrequency;
import me.yattaw.snowingtoday.util.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WeatherApiService {

    private static final String WEATHER_API_URL = "https://api.weather.com/v3/wx/forecast/hourly/1day?apiKey=%s&geocode=%f%%2C%f&units=e&language=en-US&format=json";
    private static final String QUERY_WEATHER_URL = "https://api.weather.com/v3/location/search?query=%s&locationType=city&language=en-US&format=json&apiKey=%s";
    private static final String POSTAL_WEATHER_URL = "https://api.weather.com/v3/location/search?apiKey=%s&language=en-US&query=%s&locationType=postCode&format=json";

    /**
     * Get Best Locations Containing Query String or Postal Code
     *
     * @param query a string that contains location information, or postal code
     * @return a list of locations that fit the query / postal code search
     */
    public static List<LocationData> getLocations(String query) {

        // Initialize empty list to store Location Data objects
        List<LocationData> locationDataList = new ArrayList<>();
        String json;

        // Get JSON from either query string or postal code URL
        if (query.matches("\\d{5}")) {
            json = JsonParser.getJsonFromUrl(String.format(POSTAL_WEATHER_URL, SnowingTodayApplication.API_KEY, query));
        } else {
            try {
                json = JsonParser.getJsonFromUrl(String.format(QUERY_WEATHER_URL, URLEncoder.encode(query, "UTF-8"), SnowingTodayApplication.API_KEY));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        if (json == null) return null;

        JsonNode jsonNode = JsonParser.parseJsonNode(json).get("location");

        // Iterate through each location and create Location Data object
        for (int i = 0; i < jsonNode.get("address").size(); i++) {

            LocationData locationData = LocationData.create(jsonNode.get("latitude").get(i).floatValue(), jsonNode.get("longitude").get(i).floatValue(), jsonNode.get("postalCode").get(i).textValue(), jsonNode.get("country").get(i).textValue(), jsonNode.get("adminDistrict").get(i).textValue(), jsonNode.get("city").get(i).textValue());

            // Add snow data and add object to list
            addSnowData(locationData);
            locationDataList.add(locationData);
        }

        return locationDataList;
    }

    /**
     * Add Snow Data from the forecast to Location Data object
     *
     * @param locationData the locationData to add SnowData to
     * @return SnowData object that is used to predict snow day
     */
    public static SnowData addSnowData(LocationData locationData) {
        String json = JsonParser.getJsonFromUrl(String.format(WEATHER_API_URL, SnowingTodayApplication.API_KEY, locationData.getLatitude(), locationData.getLongitude()));
        JsonNode jsonNode = JsonParser.parseJsonNode(json);

        // Initialize snow data object
        SnowData weatherData = SnowData.create();

        // Iterate through each qpf snow value and add to total snow volume in Weather Data
        jsonNode.get("qpfSnow").elements().forEachRemaining(node -> weatherData.addSnowVolume(node.floatValue()));

        // If total snow is greater than or equal to seasonal inches, set probability to 100
        if (weatherData.getTotalSnow() >= SnowFrequency.SEASONAL.getInches()) {
            locationData.setSnowDayProbability(100);
        }

        // Iterate through each temperature value and add to Weather Data
        jsonNode.get("temperature").elements().forEachRemaining(node -> weatherData.processTemperature(node.floatValue()));

        // Calculate snow day probability from total snow volume
        locationData.setSnowDayProbability((weatherData.getTotalSnow() / (float) SnowFrequency.SEASONAL.getInches()) * 100f);

        // Calculate school prediction from snow day probability
        weatherData.setPrediction(SchoolPrediction.getDescriptionFromChance(Math.round(locationData.getSnowDayProbability())));

        // Add Weather Data object to Location Data
        locationData.setSnowData(weatherData);

        return weatherData;
    }
}