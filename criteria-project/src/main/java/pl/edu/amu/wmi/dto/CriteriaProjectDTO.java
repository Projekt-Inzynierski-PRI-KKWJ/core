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

    @NotBlank(message = "Criterium cannot be empty")
    private String criterium;

    @NotNull(message = "Level of realization is required")
    private Integer levelOfRealization;

    @NotNull(message = "Semester is required")
    private Semester semester;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "User ID is required")
    private Long userId;
}
