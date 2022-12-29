package me.yattaw.snowingtoday.data;

import java.util.Arrays;

public enum SchoolPrediction {

    NO_SNOW("No chance of school being cancelled from snow", 0),
    LIMITED("Low chance of school delay or cancellation", 30),
    DELAY("Possible chance of school delay or cancellation", 70),
    CANCELED("High chance of school cancellation", 100);

    private String description;
    private int chance;

    SchoolPrediction(String description, int chance) {
        this.description = description;
        this.chance = chance;
    }

    public int getChance() {
        return chance;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionFromChance(int chance) {
        return Arrays.stream(SchoolPrediction.values())
                .filter(prediction -> chance <= prediction.getChance())
                .findFirst()
                .map(SchoolPrediction::getDescription)
                .orElse("Failed to retrieve prediction data."); // Should never reach this line of code
    }

}
