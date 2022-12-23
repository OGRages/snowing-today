package me.yattaw.snowingtoday;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.yattaw.snowingtoday.data.LocationData;
import me.yattaw.snowingtoday.data.SnowData;
import me.yattaw.snowingtoday.data.SnowFrequency;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SnowDayController {

    private static final String IP_API_URL = "https://ip-api.com/json/%s";
    private static final String WEATHER_API_URL = "https://api.weather.com/v3/wx/forecast/hourly/1day?apiKey=%s&geocode=%f%%2C%f&units=e&language=en-US&format=json";
    private static final String QUERY_WEATHER_URL = "https://api.weather.com/v3/location/search?query=%s&locationType=city&language=en-US&format=json&apiKey=%s";

    @GetMapping("/api")
    public ResponseEntity<List<LocationData>> getResponseEntity(@RequestParam(required = false) String query, @RequestParam(required = false) String lat, @RequestParam(required = false) String lon) {
        SnowFrequency frequency = SnowFrequency.SEASONAL; //TODO: allow users to change frequency
        List<LocationData> locationDataList = null;

        if (query != null) {
            locationDataList = getLocationsFromString(query, System.getenv("WEATHER_API_KEY"));
        } else if (lat == null || lon == null) {
            String address = System.getenv("ADDRESS"); // change later to requested users IP "request.getRemoteAddr();"
            locationDataList = new ArrayList<>();
            locationDataList.add(getUserRegionFromIP(address));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(locationDataList);
    }

    /**
     * Get Best Locations Containing Query String
     *
     * @param query  a string that contains location information
     * @param apiKey api key for weather api
     * @return a list of locations that fit the query
     */
    private List<LocationData> getLocationsFromString(String query, String apiKey) {
        List<LocationData> locationDataList = new ArrayList<>();
        String json = getJsonFromUrl(String.format(QUERY_WEATHER_URL, query.replace(" ", "%20"), apiKey));
        JsonNode jsonNode = parseJsonNode(json).get("location");

        for (int i = 0; i < jsonNode.get("address").size(); i++) {

            LocationData locationData = LocationData.create(jsonNode.get("latitude").get(i).floatValue(), jsonNode.get("longitude").get(i).floatValue(), jsonNode.get("postalCode").get(i).textValue(), jsonNode.get("country").get(i).textValue(), jsonNode.get("adminDistrict").get(i).textValue(), jsonNode.get("city").get(i).textValue());

            addSnowData(locationData, System.getenv("WEATHER_API_KEY"));
            locationDataList.add(locationData);
        }
        return locationDataList;
    }


    /**
     * Create UserRegion data from IP
     *
     * @param ip the users ip address
     * @return UserRegion using ip-api to fetch users location from IP
     */
    private LocationData getUserRegionFromIP(String ip) {
        String json = getJsonFromUrl(String.format(IP_API_URL, ip));
        LocationData locationData = LocationData.createFromJson(parseJsonNode(json));
        addSnowData(locationData, System.getenv("WEATHER_API_KEY"));
        return locationData;
    }

    /**
     * Add Snow Data from the forecast
     *
     * @param locationData the locationData to add SnowData to
     * @param apiKey       weather api key
     * @return SnowData object that is used to predict snow day
     */
    private SnowData addSnowData(LocationData locationData, String apiKey) {
        String json = getJsonFromUrl(String.format(WEATHER_API_URL, apiKey, locationData.getLatitude(), locationData.getLongitude()));
        JsonNode jsonNode = parseJsonNode(json);
        SnowData weatherData = SnowData.create();
        jsonNode.get("qpfSnow").elements().forEachRemaining(node -> weatherData.addSnowVolume(node.floatValue()));

        if (weatherData.getTotalSnow() >= SnowFrequency.SEASONAL.getInches()) {
            locationData.setSnowDayProbability(100);
        }

        locationData.setSnowDayProbability((weatherData.getTotalSnow() / (float) SnowFrequency.SEASONAL.getInches()) * 100f);
        locationData.setSnowData(weatherData);
        return weatherData;
    }

    /**
     * Get Json From URL
     *
     * @param url the url in string format
     * @return json data from the url
     */
    private String getJsonFromUrl(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                return in.lines().collect(Collectors.joining());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Convert String to JsonNode
     *
     * @param json string data to parse
     * @return JsonNode from String
     */
    private JsonNode parseJsonNode(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
