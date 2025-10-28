package pl.edu.amu.wmi.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.wmi.web.mapper.ProjectMarketMapper;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-markets/project")
public class ProjectDataController {

    private final ProjectMarketFacade projectMarketFacade;
    private final ProjectMarketMapper projectMarketMapper;

    @PostMapping
    public ResponseEntity<ProjectMarketDTO> createProjectAndPublishOnMarket(@Valid @RequestBody ProjectCreateRequestDTO request) {
        //UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            var projectMarket = projectMarketFacade.createMarket(request, "s485953");
            return ResponseEntity.ok(projectMarketMapper.toDto(projectMarket));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
