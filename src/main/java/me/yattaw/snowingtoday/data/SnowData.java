package me.yattaw.snowingtoday.data;

public class SnowData {

    private float totalSnow;
    private float snowingHours;

    private final Long lastTimeChecked; //TODO: use for optimization caching later

    private SnowData() {
        this.lastTimeChecked = System.currentTimeMillis();
    }

    public static SnowData create() {
        return new SnowData();
    }

    public void addSnowVolume(float volume) { // add inches of snow
        this.totalSnow += volume;
        this.snowingHours++;
    }

    public float getSnowPerHour() {
        return totalSnow / snowingHours;
    }

    public float getTotalSnow() {
        return totalSnow;
    }

}