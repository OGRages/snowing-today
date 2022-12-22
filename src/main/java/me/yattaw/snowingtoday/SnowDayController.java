package me.yattaw.snowingtoday;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.stream.Collectors;

@RestController
public class SnowDayController {

    private static final String IP_API_URL = "http://ip-api.com/json/%s";
    private static final String WEATHER_API_URL = "https://api.weather.com/v3/wx/forecast/hourly/1day?apiKey=%s&geocode=%f%%2C%f&units=e&language=en-US&format=json";

    @GetMapping("/api")
    public ResponseEntity<LocationData> getResponseEntity(HttpServletRequest request, @RequestParam(required = false) String lat, @RequestParam(required = false) String lon) {
        LocationData locationData = null;
        SnowFrequency frequency = SnowFrequency.SEASONAL; //TODO: allow users to change frequency

        if (lat == null || lon == null) {
            String address = System.getenv("ADDRESS"); // change later to requested users IP "request.getRemoteAddr();"
            if (locationData == null) {
                locationData = getUserRegionFromIP(address);

                // calculate snow day probability
                SnowData data = getSnowData(locationData.getLatitude(), locationData.getLongitude(), System.getenv("WEATHER_API_KEY"));

                if (data.getTotalSnow() >= frequency.getInches()) {
                    locationData.setSnowDayProbability(100);
                }

                locationData.setSnowDayProbability((data.getTotalSnow() / (float) frequency.getInches()) * 100f);
                locationData.setSnowData(data);
            }

        } else {
            // create userRegion from latitude and longitude

        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(locationData);
    }

    /**
     * Create UserRegion data from IP
     *
     * @param ip the users ip address
     * @return UserRegion using ip-api to fetch users location from IP
     */
    private LocationData getUserRegionFromIP(String ip) {
        String json = getJsonFromUrl(String.format(IP_API_URL, ip));
        return LocationData.createFromJson(parseJsonNode(json));
    }

    /**
     * Get Snow Data for the next 24 hours
     *
     * @param latitude  the users latitude
     * @param longitude the users longitude
     * @param apiKey    weather api key
     * @return SnowData object that is used to predict snow day
     */
    private SnowData getSnowData(float latitude, float longitude, String apiKey) {
        String json = getJsonFromUrl(String.format(WEATHER_API_URL, apiKey, latitude, longitude));
        JsonNode jsonNode = parseJsonNode(json);
        SnowData weatherData = SnowData.create();
        jsonNode.get("qpfSnow").elements().forEachRemaining(node -> weatherData.addSnowVolume(node.floatValue()));
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
