package pl.edu.amu.wmi.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-markets/project")
public class ProjectDataController {

    private final ProjectMarketFacade projectMarketFacade;

    @PostMapping
    public ResponseEntity<Void> createProjectAndPublishOnMarket(@Valid @RequestBody ProjectCreateRequestDTO request) {
        //UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            projectMarketFacade.createMarket(request, "s485953");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
