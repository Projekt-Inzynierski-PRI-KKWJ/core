package pl.edu.amu.wmi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.model.ProjectCreateRequest;
import pl.edu.amu.wmi.service.ProjectMarketService;
import pl.edu.amu.wmi.service.ProjectService;

@Component
@RequiredArgsConstructor
public class ProjectMarketFacade {

    private final ProjectMarketService projectMarketService;
    private final ProjectService projectService;

    public ProjectMarket createMarket(ProjectCreateRequest request) {
        var project = projectService.createProject(request);
        return projectMarketService.publishMarket(project);
    }
}
