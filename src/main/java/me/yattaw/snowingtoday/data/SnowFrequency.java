package me.yattaw.snowingtoday.data;

public enum SnowFrequency {

    ALWAYS("Snows all year", 12),
    SEASONAL("Snows during winter", 8),
    NEVER("Almost never Snows Here", 4);

    private String description;
    private int inches;

    SnowFrequency(String description, int inches) {
        this.description = description;
        this.inches = inches;
    }

    public String getDescription() {
        return description;
    }

    public int getInches() {
        return inches;
    }
}
