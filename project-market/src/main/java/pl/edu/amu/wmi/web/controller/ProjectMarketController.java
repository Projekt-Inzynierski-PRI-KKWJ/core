package pl.edu.amu.wmi.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDetailsDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-market/market")
public class ProjectMarketController {

    private final ProjectMarketFacade projectMarketFacade;

    @GetMapping
    public ResponseEntity<Page<ProjectMarketDTO>> getAllActiveProjectMarkets(Pageable pageable) {
        try {
            return ResponseEntity.ok(projectMarketFacade.getAllActiveProjectMarkets(pageable));
        } catch (Exception e) {
            return ResponseEntity.ok(Page.empty(pageable));
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectMarketDetailsDTO> getProjectMarketById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(projectMarketFacade.getMarketDetailsById(id));
        }catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProjectMarketDTO>> searchByName(@RequestParam String name, Pageable pageable) {
        try {
            return ResponseEntity.ok(projectMarketFacade.searchProjectMarketsByNamePattern(name, pageable));
        }catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

}
