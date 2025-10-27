package pl.edu.amu.wmi.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.model.export.StudyYearDataExportDTO;
import pl.edu.amu.wmi.service.export.DataExportService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/export")
public class DataExportController {

    private final DataExportService dataExportService;

    public DataExportController(DataExportService dataExportService) {
        this.dataExportService = dataExportService;
    }

    @Secured({"COORDINATOR"})
    @GetMapping("/study-year")
    public ResponseEntity<List<String>> getAvailableStudyYears() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Available study years requested by user: {}", userDetails.getUsername());

        try {
            List<String> studyYears = dataExportService.getAvailableStudyYears();
            // Convert database format (FULL_TIME#2023) to URL-safe format (FULL_TIME-2023)
            List<String> urlSafeStudyYears = studyYears.stream()
                    .map(year -> year.replace("#", "-"))
                    .toList();
            log.info("Found {} available study years", urlSafeStudyYears.size());
            return ResponseEntity.ok(urlSafeStudyYears);

        } catch (Exception e) {
            log.error("Error retrieving available study years", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Secured({"COORDINATOR"})
    @GetMapping("/study-year/{studyYear}")
    public ResponseEntity<StudyYearDataExportDTO> exportStudyYearData(
            @PathVariable String studyYear) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Data export requested for study year: {} by user: {}", studyYear, userDetails.getUsername());

        // Convert URL-safe format (FULL_TIME-2023) back to database format (FULL_TIME#2023)
        String dbStudyYear = studyYear.replace("-", "#");
        log.info("Converted URL study year {} to database format: {}", studyYear, dbStudyYear);

        try {
            StudyYearDataExportDTO exportData = dataExportService.exportStudyYearData(dbStudyYear);
            exportData.getMetadata().setExportedBy(userDetails.getUsername());

            log.info("Data export successful for study year: {}", dbStudyYear);
            return ResponseEntity.ok(exportData);

        } catch (Exception e) {
            log.error("Error exporting data for study year: {}", dbStudyYear, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
