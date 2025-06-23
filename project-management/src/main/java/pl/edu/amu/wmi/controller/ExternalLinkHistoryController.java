package pl.edu.amu.wmi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.model.externallink.ExternalLinkHistoryDTO;
import pl.edu.amu.wmi.service.externallink.ExternalLinkHistoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/project/external-link/history")
public class ExternalLinkHistoryController {

    private final ExternalLinkHistoryService externalLinkHistoryService;

    @Autowired
    public ExternalLinkHistoryController(ExternalLinkHistoryService externalLinkHistoryService) {
        this.externalLinkHistoryService = externalLinkHistoryService;
    }

    /**
     * Get history for a specific external link
     * @param externalLinkId ID of the external link
     * @return List of history records for the external link
     */
    @Secured({"PROJECT_ADMIN", "COORDINATOR", "STUDENT", "SUPERVISOR"})
    @GetMapping("/{externalLinkId}")
    public ResponseEntity<List<ExternalLinkHistoryDTO>> getExternalLinkHistory(
            @PathVariable Long externalLinkId) {
        
        log.info("Getting history for external link with ID: {}", externalLinkId);
        List<ExternalLinkHistoryDTO> history = externalLinkHistoryService.getHistoryByExternalLinkId(externalLinkId);
        
        log.info("Found {} history records for external link {}", history.size(), externalLinkId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get all external link history for a study year
     * @param studyYear Study year to filter by
     * @return List of all history records for the study year
     */
    @Secured({"COORDINATOR", "SUPERVISOR"})
    @GetMapping("/study-year")
    public ResponseEntity<List<ExternalLinkHistoryDTO>> getAllExternalLinkHistoryByStudyYear(
            @RequestHeader("study-year") String studyYear) {
        
        log.info("Getting all external link history for study year: {}", studyYear);
        List<ExternalLinkHistoryDTO> history = externalLinkHistoryService.getAllHistoryByStudyYear(studyYear);
        
        log.info("Found {} total history records for study year {}", history.size(), studyYear);
        return ResponseEntity.ok(history);
    }

}
