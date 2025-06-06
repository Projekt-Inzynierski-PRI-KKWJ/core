package pl.edu.amu.wmi.enumerations;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LevelOfRealization {
    IN_PROGRESS("To do"),PARTIALLY_COMPLETED("Partially Completed"),COMPLETED("Done");

    private final String label;

    LevelOfRealization(String lable) {
        this.label = lable;
    }


}
