package me.yattaw.snowingtoday.data;

public class WeatherData {

    private float totalSnow;
    private float snowingHours;

    private final Long lastTimeChecked;

    private WeatherData() {
        this.lastTimeChecked = System.currentTimeMillis();
    }

    public static WeatherData create() {
        return new WeatherData();
    }

    public void addSnowVolume(float volume) {
        this.totalSnow += volume;
        this.snowingHours += 3; // snow forecast reads in 3 hour intervals.
        System.out.println("added snow volume, new total: " + totalSnow);
    }

    public float getSnowPerHour() {
        return totalSnow / snowingHours;
    }

    public boolean isUpdated() {
        return (System.currentTimeMillis() - lastTimeChecked) < 1800000; // last check was less than 30 minutes ago
    }

}
