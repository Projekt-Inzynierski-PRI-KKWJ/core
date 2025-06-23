package pl.edu.amu.wmi.service.export;

import pl.edu.amu.wmi.model.export.StudyYearDataExportDTO;
import java.util.List;

public interface DataExportService {
    
    /**
     * Exports all data for a specific study year including projects, students, 
     * supervisors, grades, external links, and their statuses.
     * 
     * @param studyYear The study year to export data for
     * @return Complete data export for the study year
     */
    StudyYearDataExportDTO exportStudyYearData(String studyYear);
    
    /**
     * Lists all available study years in the system.
     * 
     * @return List of study year strings
     */
    List<String> getAvailableStudyYears();
}
