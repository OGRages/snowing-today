package me.yattaw.snowingtoday.data;

import com.fasterxml.jackson.databind.JsonNode;

public class UserRegion {

    private String zipcode;
    private String country;
    private String region;
    private String city;

    private float latitude;
    private float longitude;

    private boolean snowing;
    private float snowDayProbability = 0.0f;

    private UserRegion(float latitude, float longitude, String zipcode, String country, String region, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zipcode = zipcode;
        this.country = country;
        this.region = region;
        this.city = city;
    }


    public static UserRegion create(float latitude, float longitude, String zipcode, String country, String region, String city) {
        return new UserRegion(latitude, longitude, zipcode, country, region, city);
    }

    public static UserRegion createFromJson(JsonNode node) {
        return new UserRegion(
                node.get("lat").floatValue(),
                node.get("lon").floatValue(),
                node.get("zip").asText(),
                node.get("country").asText(),
                node.get("regionName").asText(),
                node.get("city").asText()
        );
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setSnowing(boolean snowing) {
        this.snowing = snowing;
    }

    public void setSnowDayProbability(float snowDayProbability) {
        this.snowDayProbability = snowDayProbability;
    }

    @Override
    public String toString() {
        return "UserRegion{" +
                "zipcode='" + zipcode + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", snowDayProbability=" + snowDayProbability +
                '}';
    }
}
