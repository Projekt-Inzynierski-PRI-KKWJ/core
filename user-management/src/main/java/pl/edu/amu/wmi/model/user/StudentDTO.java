package pl.edu.amu.wmi.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentDTO {

    private String name;

    private String email;

    private String indexNumber;

    private String role;

    private boolean accepted;

    // Project information
    private Long confirmedProjectId;
    private String confirmedProjectName;
    private List<Long> assignedProjectIds;
    private List<String> assignedProjectNames;

    // Grade information (from confirmed project's evaluation cards)
    private String firstSemesterGrade;   // e.g., "75.50%"
    private String secondSemesterGrade;  // e.g., "82.30%"
    private Double finalGrade;           // e.g., 4.5 (scale: 2.0-5.0)
    private Boolean isApprovedForDefense;

}
