package me.yattaw.snowingtoday.service;

import me.yattaw.snowingtoday.data.LocationData;
import me.yattaw.snowingtoday.util.JsonParser;

public class IPApiService {

    private static final String IP_API_URL = "http://ip-api.com/json/%s";

    /**
     * Create UserRegion data from IP
     *
     * @param ip the users ip address
     * @return UserRegion using ip-api to fetch users location from IP
     */
    public static LocationData getUserRegionFromIP(String ip) {
        String json = JsonParser.getJsonFromUrl(String.format(IP_API_URL, ip));
        LocationData locationData = LocationData.createFromJson(JsonParser.parseJsonNode(json));
        WeatherApiService.addSnowData(locationData);
        return locationData;
    }

}
