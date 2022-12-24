package me.yattaw.snowingtoday.data;

public class SnowData {

    private float totalSnow;
    private float snowingHours;

    private String prediction;

    private final Long lastTimeChecked; //TODO: use for optimization caching later

    private SnowData() {
        this.lastTimeChecked = System.currentTimeMillis();
    }

    public static SnowData create() {
        return new SnowData();
    }

    public void addSnowVolume(float volume) { // add inches of snow
        this.totalSnow += volume;
        if (volume > 0.0) {
            this.snowingHours++;
        }
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

    public String getPrediction() {
        return prediction;
    }
}