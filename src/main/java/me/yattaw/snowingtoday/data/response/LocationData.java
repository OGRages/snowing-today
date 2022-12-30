package me.yattaw.snowingtoday.data.response;

import com.fasterxml.jackson.databind.JsonNode;
import me.yattaw.snowingtoday.data.SnowData;

public class LocationData {

    private String postalCode;
    private String country;
    private String district;
    private String city;
    private float latitude;
    private float longitude;

    private float snowDayProbability;
    private SnowData snowData;

    private LocationData(float latitude, float longitude, String postalCode, String country, String district, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalCode = postalCode;
        this.country = country;
        this.district = district;
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

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getDistrict() {
        return district;
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
                "zipcode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", snowDayProbability=" + snowDayProbability +
                '}';
    }
}
