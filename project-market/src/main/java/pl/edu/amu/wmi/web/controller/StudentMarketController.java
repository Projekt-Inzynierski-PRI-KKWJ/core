package pl.edu.amu.wmi.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.web.model.ProjectMembersDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project-market/student")
public class StudentMarketController {

    private final ProjectMarketFacade projectMarketFacade;

    @GetMapping("/{projectMarketId}/members")
    public ResponseEntity<ProjectMembersDTO> getProjectMembers(@PathVariable Long projectMarketId) {
        try {
            return ResponseEntity.ok(projectMarketFacade.getProjectMembersByMarketId(projectMarketId));
        } catch(Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
