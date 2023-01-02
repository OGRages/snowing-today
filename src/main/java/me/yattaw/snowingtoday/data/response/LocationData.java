package me.yattaw.snowingtoday.data.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.yattaw.snowingtoday.data.SnowData;

@Data
@AllArgsConstructor
public class LocationData {

    private String postalCode;
    private String country;
    private String district;
    private String city;
    private float latitude;
    private float longitude;

    private float snowDayProbability;
    private SnowData snowData;

    public static LocationData create(String postalCode, String country, String district, String city, float latitude, float longitude) {
        return new LocationData(postalCode, country, district, city, latitude, longitude, 0.0f, null);
    }

    public static LocationData createFromJson(JsonNode node) {
        return create(
                node.get("zip").asText(),
                node.get("country").asText(),
                node.get("regionName").asText(),
                node.get("city").asText(),
                node.get("lat").floatValue(),
                node.get("lon").floatValue()
        );
    }

    public float getSnowDayProbability() {
        return (float) Math.round(snowDayProbability * 100) / 100;
    }
}