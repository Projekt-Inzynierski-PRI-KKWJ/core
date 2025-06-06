package pl.edu.amu.wmi.enumerations;

public enum TypeOfCriterium
{
    REQUIRED("Required"),EXPECTED("Expected"),MEASURABLE_IMPLEMENTATION_INDICATORS("Implementation Indicator");

    private final String label;

    TypeOfCriterium(String label)
    {
        this.label=label;
    }

}
