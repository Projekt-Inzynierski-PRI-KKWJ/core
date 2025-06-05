package pl.edu.amu.wmi.dto;

import lombok.Data;
import pl.edu.amu.wmi.enumerations.Semester;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
public class CriteriaProjectDTO
{
    private Long idProject;

    private String criterium;

    private Integer levelOfRealization;

    private Semester semester;

    private Long projectId;

    private Long userId;
}
