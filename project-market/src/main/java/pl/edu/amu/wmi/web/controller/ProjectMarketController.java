package pl.edu.amu.wmi.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-market/market")
public class ProjectMarketController {

    private final ProjectMarketFacade projectMarketFacade;

    @GetMapping
    public ResponseEntity<Page<ProjectMarketDTO>> getAllActiveProjectMarkets(Pageable pageable) {
        try {
            Page<ProjectMarketDTO> projectMarkets = projectMarketFacade.getAllActiveProjectMarkets(pageable);
            return ResponseEntity.ok(projectMarkets);
        } catch (Exception e) {
            return ResponseEntity.ok(Page.empty(pageable));
        }
    }

}
