package pl.edu.amu.wmi.enumerations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeOfCriterium
{
    REQUIRED, EXPECTED, MEASURABLE_IMPLEMENTATION_INDICATORS;

//    private final String label;
//
//    TypeOfCriterium(String label)
//    {
//        this.label=label;
//    }
//    @JsonValue
//    public String getLabel() {
//        return label;
//    }
//
//
//    @JsonCreator
//    public static TypeOfCriterium fromLabel(String label) {
//        for (TypeOfCriterium value : TypeOfCriterium.values()) {
//            if (value.label.equalsIgnoreCase(label)) {
//                return value;
//            }
//        }
//        throw new IllegalArgumentException("Unknown type: " + label);
//    }
}
