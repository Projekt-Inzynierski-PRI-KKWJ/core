package pl.edu.amu.wmi.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.dao.StudyYearDAO;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.service.ProjectMarketService;
import pl.edu.amu.wmi.service.ProjectService;
import pl.edu.amu.wmi.web.mapper.ProjectMarketMapper;
import pl.edu.amu.wmi.web.mapper.ProjectRequestMapper;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;

@Component
@RequiredArgsConstructor
public class ProjectMarketFacade {

    private final ProjectMarketService projectMarketService;
    private final ProjectService projectService;
    private final StudentDAO studentDAO;
    private final StudyYearDAO studyYearDAO;
    private final ProjectRequestMapper projectRequestMapper;
    private final ProjectMarketMapper projectMarketMapper;

    @Transactional
    public ProjectMarket createMarket(ProjectCreateRequestDTO request, String indexNumber) {
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        var studyYear = studyYearDAO.findByStudyYear(request.getStudyYear());

        var project = projectService.createProject(projectRequestMapper.fromDto(request, studyYear, student));
        return projectMarketService.publishMarket(projectMarketMapper.toPublishRequest(request, project));
    }
}
