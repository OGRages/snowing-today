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

    private Map<String, UserRegion> cachedRegions = new HashMap<>();

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        String address = System.getenv("ADDRESS");
        UserRegion userRegion = cachedRegions.get(address);
        if (userRegion == null) {
            userRegion = parseUserRegion(address);

            // calculate snow day probability
            parseWeatherData(59.5765F, 133.7017F, System.getenv("WEATHER_API_KEY"));

            cachedRegions.put(address, userRegion);
        }
        return request.getRemoteAddr();
    }

    private UserRegion parseUserRegion(String ip) {
        String json = "";
        try {
            URLConnection connection = new URL(String.format(IP_API_URL, ip)).openConnection();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                json = in.lines().collect(Collectors.joining());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(json);
            return UserRegion.createFromJson(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private WeatherData parseWeatherData(float latitude, float longitude, String apiKey) {
        String json = "";
        try {
            URLConnection connection = new URL(String.format(WEAHTER_API_URL, latitude, longitude, apiKey)).openConnection();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                json = in.lines().collect(Collectors.joining());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(json);

            JsonNode weatherList = node.get("list");
            WeatherData weatherData = WeatherData.create();
            for (int i = 0; i < 4; i++) { // check for the next 12 hours
                JsonNode weatherNode = weatherList.get(i);
                if (weatherNode.has("snow")) {
                    weatherData.addSnowVolume(weatherNode.get("snow").get("3h").floatValue());
                }
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
