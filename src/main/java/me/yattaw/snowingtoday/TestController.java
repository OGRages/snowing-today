package me.yattaw.snowingtoday;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import me.yattaw.snowingtoday.data.UserRegion;
import me.yattaw.snowingtoday.data.WeatherData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TestController {

    private static final String IP_API_URL = "http://ip-api.com/json/%s";
    private static final String WEAHTER_API_URL = "http://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&appid=%s";

    private static final int CANCEL_SNOW_AMOUNT = 127; // 127 millimeters or 5 inches to cancel school on average
    private static final int CONFIRM_CANCEL_AMOUNT = 200;

    private Map<String, UserRegion> cachedRegions = new HashMap<>();

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        String address = System.getenv("ADDRESS");
        UserRegion userRegion = cachedRegions.get(address);
        if (userRegion == null) {
            userRegion = getUserRegion(address);

            // calculate snow day probability
            WeatherData weatherData = getWeatherData(45.7833F, 108.5007F, System.getenv("WEATHER_API_KEY"));

            if (weatherData.getTotalSnow() >= CONFIRM_CANCEL_AMOUNT) {
                userRegion.setSnowDayProbability(100f);
            }

            userRegion.setSnowDayProbability((weatherData.getTotalSnow() / (float) CANCEL_SNOW_AMOUNT) * 100f);

            System.out.println("Snow Per Hour: " + weatherData.getSnowPerHour());

            cachedRegions.put(address, userRegion);
        }
        return String.format("Snow Day Chance: %.2f", userRegion.getSnowDayProbability());
    }

    /**
     * Create UserRegion data from IP
     *
     * @param ip the users ip address
     * @return UserRegion using ip-api to fetch users location from IP
     */
    private UserRegion getUserRegion(String ip) {
        String json = getJsonFromUrl(String.format(IP_API_URL, ip));
        return UserRegion.createFromJson(parseJsonNode(json));
    }

    /**
     * Get Weather Data for the next 12 hours
     *
     * @param latitude  the users latitude
     * @param longitude the users longitude
     * @param apiKey    openweathermap api key
     * @return WeatherData object that is used to predict snow day
     */
    private WeatherData getWeatherData(float latitude, float longitude, String apiKey) {
        String json = getJsonFromUrl(String.format(WEAHTER_API_URL, latitude, longitude, apiKey));

        JsonNode weatherList = parseJsonNode(json).get("list");

        WeatherData weatherData = WeatherData.create();

        for (int i = 0; i < 10; i++) { // check for the next 12 hours
            JsonNode weatherNode = weatherList.get(i);
            if (weatherNode.has("snow")) {
                weatherData.addSnowVolume(weatherNode.get("snow").get("3h").floatValue()); // float value is the amount of snowfall over a three-hour period in millimeters
            }
        }

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
