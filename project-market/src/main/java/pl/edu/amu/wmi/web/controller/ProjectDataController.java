package pl.edu.amu.wmi.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.ProjectMarketFacade;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.wmi.web.mapper.ProjectMarketDTOMapper;
import pl.edu.amu.wmi.web.mapper.ProjectRequestMapper;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDto;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-markets/project")
public class ProjectDataController {

    private final ProjectMarketFacade projectMarketFacade;
    private final ProjectRequestMapper projectRequestMapper;
    private final ProjectMarketDTOMapper projectMarketDTOMapper;

    @PostMapping("")
    public ResponseEntity<ProjectMarketDTO> createProjectAndPublishOnMarket(@RequestBody ProjectCreateRequestDto request) {
        try {
            var projectMarket = projectMarketFacade.createMarket(projectRequestMapper.fromDto(request));
            return ResponseEntity.ok(projectMarketDTOMapper.toDto(projectMarket));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
