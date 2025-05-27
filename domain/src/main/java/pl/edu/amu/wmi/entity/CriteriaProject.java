package pl.edu.amu.wmi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.amu.wmi.enumerations.Semester;

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

    private Integer levelOfRealization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    private Project project;
}
