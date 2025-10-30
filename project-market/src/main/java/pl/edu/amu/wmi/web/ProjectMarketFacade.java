package pl.edu.amu.wmi.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.dao.StudyYearDAO;
import pl.edu.amu.wmi.service.ProjectMarketService;
import pl.edu.amu.wmi.service.ProjectService;
import pl.edu.amu.wmi.web.mapper.ProjectMarketMapper;
import pl.edu.amu.wmi.web.mapper.ProjectRequestMapper;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;

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
    public void createMarket(ProjectCreateRequestDTO request, String indexNumber) {
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        var studyYear = studyYearDAO.findByStudyYear(request.getStudyYear());

        var project = projectService.createProject(projectRequestMapper.fromDto(request, studyYear, student));
        projectMarketService.publishMarket(projectMarketMapper.toPublishRequest(request, project));
    }

    public Page<ProjectMarketDTO> getAllActiveProjectMarkets(Pageable pageable) {
        return projectMarketMapper.toProjectMarketDTOList(projectMarketService.listActiveMarkets(pageable));
    }
}
