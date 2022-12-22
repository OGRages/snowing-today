package me.yattaw.snowingtoday.data;

import com.fasterxml.jackson.databind.JsonNode;

public class LocationData {

    private String zipcode;
    private String country;
    private String region;
    private String city;
    private float latitude;
    private float longitude;

    private float snowDayProbability;
    private SnowData snowData;

    private LocationData(float latitude, float longitude, String zipcode, String country, String region, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zipcode = zipcode;
        this.country = country;
        this.region = region;
        this.city = city;
    }

    public static LocationData create(float latitude, float longitude, String zipcode, String country, String region, String city) {
        return new LocationData(latitude, longitude, zipcode, country, region, city);
    }

    public static LocationData createFromJson(JsonNode node) {
        return new LocationData(
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

    public String getZipcode() {
        return zipcode;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }


    public void setSnowDayProbability(float snowDayProbability) {
        this.snowDayProbability = snowDayProbability;
    }

    public float getSnowDayProbability() {
        return (float) Math.round(snowDayProbability * 100) / 100;
    }

    public void setSnowData(SnowData snowData) {
        this.snowData = snowData;
    }

    public SnowData getSnowData() {
        return snowData;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "zipcode='" + zipcode + '\'' +
                ", country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", snowDayProbability=" + snowDayProbability +
                '}';
    }
}
