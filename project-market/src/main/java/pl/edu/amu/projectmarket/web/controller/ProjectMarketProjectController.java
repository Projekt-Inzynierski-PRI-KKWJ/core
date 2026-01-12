package pl.edu.amu.projectmarket.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.projectmarket.web.ProjectMarketFacade;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.projectmarket.web.model.ProjectCreateRequestDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-market/project")
public class ProjectMarketProjectController {

    private final ProjectMarketFacade projectMarketFacade;

    @PostMapping
    public ResponseEntity<Void> createProjectAndPublishOnMarket(@Valid @RequestBody ProjectCreateRequestDTO request) {
        try {
            projectMarketFacade.createMarket(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @PutMapping("/{projectMarketId}")
    public ResponseEntity<Void> updateProject(@PathVariable Long projectMarketId, @Valid @RequestBody ProjectCreateRequestDTO request) {
        try {
            projectMarketFacade.updateProject(projectMarketId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
