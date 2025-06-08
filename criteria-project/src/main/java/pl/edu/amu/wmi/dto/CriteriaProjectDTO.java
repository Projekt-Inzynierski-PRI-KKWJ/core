package pl.edu.amu.wmi.dto;

import lombok.Data;
import pl.edu.amu.wmi.enumerations.LevelOfRealization;
import pl.edu.amu.wmi.enumerations.Semester;
import pl.edu.amu.wmi.enumerations.TypeOfCriterium;


@Data
public class CriteriaProjectDTO
{
    //private Long idProject;

    private String criterium;

    private LevelOfRealization levelOfRealization;  //mapped to string

    private Semester semester; //mapped to string

    private Long projectId;

    private Long userId;

    private String comment;

    private TypeOfCriterium type; //mapped to string

    private Boolean enableForModification;
}
