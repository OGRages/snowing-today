package me.yattaw.snowingtoday.data;

import lombok.Data;

@Data
public class SnowData {

    private String prediction;
    private float totalSnow;
    private float snowingHours;
    private float minTemperature = Float.MAX_VALUE; // Max value will be removed after temperatures are initialized
    private float maxTemperature;                   // Min value will be removed after temperatures are initialized

    public static SnowData create() {
        return new SnowData();
    }

    public void processTemperature(float temperature) {
        minTemperature = Math.min(minTemperature, temperature);
        maxTemperature = Math.max(maxTemperature, temperature);
    }

    public void addSnowVolume(float volume) { // add inches of snow
        this.totalSnow += volume;
        if (volume > 0.0) {
            this.snowingHours++;
        }
    }

    public float getAvgTemperature() {
        return (minTemperature + maxTemperature) / 2;
    }
}