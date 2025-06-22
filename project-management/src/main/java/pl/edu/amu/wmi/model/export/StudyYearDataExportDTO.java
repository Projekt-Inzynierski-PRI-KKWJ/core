package pl.edu.amu.wmi.model.export;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.amu.wmi.model.project.ProjectDetailsDTO;

import java.util.List;

@Data
@NoArgsConstructor
public class StudyYearDataExportDTO {
    private String studyYear;
    private List<ProjectDetailsDTO> projects;
    private List<StudentExportDTO> students;
    private List<SupervisorExportDTO> supervisors;
    private ExportMetadata metadata;
    
    @Data
    @NoArgsConstructor
    public static class StudentExportDTO {
        private String indexNumber;
        private String firstName;
        private String lastName;
        private String email;
        private String studyYear;
        private boolean accepted;
        private boolean projectAdmin;
        private String confirmedProjectId;
        private String role; // Student role in project (FRONTEND, BACKEND, etc.)
    }
    
    @Data
    @NoArgsConstructor
    public static class SupervisorExportDTO {
        private String indexNumber;
        private String firstName;
        private String lastName;
        private String email;
        private String studyYear;
        private int maxProjects;
        private int currentProjects;
        private boolean available;
    }
    
    @Data
    @NoArgsConstructor
    public static class ExportMetadata {
        private String exportDate;
        private String exportedBy;
        private int totalProjects;
        private int totalStudents;
        private int totalSupervisors;
        private int acceptedProjects;
        private int confirmedProjects;
    }
}
