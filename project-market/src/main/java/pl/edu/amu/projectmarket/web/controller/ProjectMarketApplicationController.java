package pl.edu.amu.projectmarket.web.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.projectmarket.web.ProjectMarketFacade;
import pl.edu.amu.projectmarket.web.model.ProjectApplicationDTO;
import pl.edu.amu.projectmarket.web.model.ApplyToProjectRequestDTO;
import pl.edu.amu.projectmarket.web.model.StudentProjectApplicationDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-market/application")
public class ProjectMarketApplicationController {

    private final ProjectMarketFacade projectMarketFacade;

    @PostMapping("/{marketId}/apply")
    public ResponseEntity<Void> applyToProject(@PathVariable Long marketId, @RequestBody ApplyToProjectRequestDTO request) {
        try {
            projectMarketFacade.applyToProject(marketId, request);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    @GetMapping("/{marketId}")
    public ResponseEntity<List<ProjectApplicationDTO>> getApplicationsForOwner(@PathVariable Long marketId) {
        try {
            return ResponseEntity.ok(projectMarketFacade.getProjectApplicationByMarketIdInPendingStatus(marketId));
        }catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    @PatchMapping("/{applicationId}/approve")
    public ResponseEntity<Void> approveApplication(@PathVariable Long applicationId) {
        try {
            projectMarketFacade.approveCandidate(applicationId);
            return ResponseEntity.ok().build();
        }catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> rejectApplication(@PathVariable Long id) {
        try {
            projectMarketFacade.rejectCandidate(id);
            return ResponseEntity.ok().build();
        }catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdrawApplication(@PathVariable Long id) {
        try {
            projectMarketFacade.withdrawApplication(id);
            return ResponseEntity.ok().build();
        }catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    @GetMapping("/student")
    public ResponseEntity<List<StudentProjectApplicationDTO>> getApplicationsForStudent() {
        try {
            var applications = projectMarketFacade.getApplicationsForStudent();
            return ResponseEntity.ok(applications);
        }catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

}
