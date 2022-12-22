package me.yattaw.snowingtoday.data;

public enum SchoolPrediction {

    LIMITED("Low chance of school being delayed or canceled", 30),
    DELAY("Very likely for school to be delayed", 70),
    CANCELED("Very high chance of school being canceled", 100);

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

    public String getDescriptionFromChance(int chance) {
        for (SchoolPrediction prediction : SchoolPrediction.values()) {
            if (chance <= prediction.getChance()) return prediction.getDescription();
        }
        return "Failed to retrieve prediction data."; // should never hit this
    }

}
