package pl.edu.amu.wmi.web.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketSupervisorDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-market/supervisor")
public class ProjectMarketSupervisorController {

    private final ProjectMarketFacade projectMarketFacade;

    @GetMapping("")
    public ResponseEntity<List<ProjectMarketSupervisorDTO>> getSupervisors(@RequestHeader("study-year") String studyYear) {
        try {
            return ResponseEntity.ok(projectMarketFacade.getSupervisors(studyYear));
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<Page<ProjectMarketDTO>> getProjectMarketsForSupervisor(Pageable pageable) {
        try {
            var markets = projectMarketFacade.getProjectMarketsForSupervisor(pageable);
            return ResponseEntity.ok(markets);
        } catch (Exception e) {
            return ResponseEntity.ok(Page.empty());
        }
    }

    @PatchMapping("/{projectMarketId}/approve")
    public ResponseEntity<Void> approveProjectAndCloseMarket(@PathVariable Long projectMarketId) {
        try {
            projectMarketFacade.approveProjectAndCloseMarket(projectMarketId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @PatchMapping("/{projectMarketId}/reject")
    public ResponseEntity<Void> rejectProjectAndCloseMarket(@PathVariable Long projectMarketId) {
        try {
            projectMarketFacade.rejectProjectAndCloseMarket(projectMarketId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
