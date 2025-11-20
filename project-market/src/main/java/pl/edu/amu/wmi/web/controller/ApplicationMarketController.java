package pl.edu.amu.wmi.web.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.web.model.ProjectApplicationDTO;
import pl.edu.amu.wmi.web.model.ApplyToProjectRequestDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-market/application")
public class ApplicationMarketController {

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
            return ResponseEntity.ok(projectMarketFacade.getProjectApplicationByMarketId(marketId));
        }catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }
}
