package pl.edu.amu.wmi.enumerations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LevelOfRealization {
    IN_PROGRESS("To do"),PARTIALLY_COMPLETED("Partially Completed"),COMPLETED("Done");

    private final String label;

    LevelOfRealization(String lable) {
        this.label = lable;
    }


    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static LevelOfRealization fromLabel(String label) {
        for (LevelOfRealization value : LevelOfRealization.values()) {
            if (value.label.equalsIgnoreCase(label)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }

}
