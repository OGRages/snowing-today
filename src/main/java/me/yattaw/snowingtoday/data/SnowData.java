package me.yattaw.snowingtoday.data;

public class SnowData {

    private String prediction;
    private float totalSnow;
    private float snowingHours;
    private float minTemperature = Float.MAX_VALUE; // Max value will be removed after temperatures are initialized
    private float maxTemperature;
    private float temperatureSum;

    private final Long lastTimeChecked; //TODO: use for optimization caching later

    private SnowData() {
        this.lastTimeChecked = System.currentTimeMillis();
    }

    public static SnowData create() {
        return new SnowData();
    }

    public void processTemperature(float temperature) {
        temperatureSum += temperature;
        minTemperature = Math.min(minTemperature, temperature);
        maxTemperature = Math.max(maxTemperature, temperature);
    }

    public void addSnowVolume(float volume) { // add inches of snow
        this.totalSnow += volume;
        if (volume > 0.0) {
            this.snowingHours++;
        }
    }

    public String getPrediction() {
        return prediction;
    }

    public float getTotalSnow() {
        return totalSnow;
    }

    public float getSnowingHours() {
        return snowingHours;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public float getAvgTemperature() {
        return temperatureSum / 24;
    }

}