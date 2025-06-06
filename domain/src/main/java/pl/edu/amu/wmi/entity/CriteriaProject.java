package pl.edu.amu.wmi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import pl.edu.amu.wmi.enumerations.LevelOfRealization;
import pl.edu.amu.wmi.enumerations.Semester;
import pl.edu.amu.wmi.enumerations.TypeOfCriterium;

@Getter
@Setter
@Entity
@Table(name = "CRITERIA_PROJECT")
public class CriteriaProject extends AbstractEntity {

    private String criterium;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_DATA_ID", nullable = false)
    private UserData userThatAddedTheCriterium;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @Enumerated(EnumType.STRING)
    private LevelOfRealization levelOfRealization;

    private String comment;

    @Enumerated(EnumType.STRING)
    private TypeOfCriterium type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    private Project project;

    private Boolean enableForModification;
}
