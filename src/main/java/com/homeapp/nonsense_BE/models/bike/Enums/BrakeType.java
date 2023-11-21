package com.homeapp.nonsense_BE.models.bike.Enums;

public enum BrakeType {

    MECHANICAL_DISC("Mechanical Disc"), HYDRAULIC_DISC("Hydraulic Disc"), RIM("Rim"), NOT_REQUIRED("Not Required"), NO_SELECTION("No Selection");

    private String name;

    private BrakeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BrakeType fromName(String name) {
        return switch (name) {
            case "Hydraulic Disc" -> HYDRAULIC_DISC;
            case "Rim" -> RIM;
            case "Mechanical Disc" -> MECHANICAL_DISC;
            case "Not Required" -> NOT_REQUIRED;
            default -> NO_SELECTION;
        };
    }

}