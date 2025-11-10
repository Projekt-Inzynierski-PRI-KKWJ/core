package pl.edu.amu.wmi.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.dao.StudyYearDAO;
import pl.edu.amu.wmi.service.ProjectApplicationService;
import pl.edu.amu.wmi.service.ProjectMarketService;
import pl.edu.amu.wmi.service.ProjectService;
import pl.edu.amu.wmi.web.mapper.ApplyToProjectRequestMapper;
import pl.edu.amu.wmi.web.mapper.ProjectMarketMapper;
import pl.edu.amu.wmi.web.mapper.ProjectMemberMapper;
import pl.edu.amu.wmi.web.mapper.ProjectRequestMapper;
import pl.edu.amu.wmi.web.model.ApplyToProjectRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDetailsDTO;
import pl.edu.amu.wmi.web.model.ProjectMembersDTO;

@Component
@RequiredArgsConstructor
public class ProjectMarketFacade {

    private final ProjectApplicationService projectApplicationService;
    private final ProjectMarketService projectMarketService;
    private final ProjectService projectService;
    private final StudentDAO studentDAO;
    private final StudyYearDAO studyYearDAO;
    private final ProjectRequestMapper projectRequestMapper;
    private final ProjectMarketMapper projectMarketMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final ApplyToProjectRequestMapper applyToProjectRequestMapper;

    @Transactional
    public void createMarket(ProjectCreateRequestDTO request) {
        var indexNumber = "s485953";
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        var studyYear = studyYearDAO.findByStudyYear(request.getStudyYear());

        var project = projectService.createProject(projectRequestMapper.fromDto(request, studyYear, student));
        projectMarketService.publishMarket(projectMarketMapper.toPublishRequest(request, project));
    }

    public Page<ProjectMarketDTO> getAllActiveProjectMarkets(Pageable pageable) {
        return projectMarketMapper.toProjectMarketDTOList(projectMarketService.listActiveMarkets(pageable));
    }

    public ProjectMarketDetailsDTO getMarketDetailsById(Long id) {
        return projectMarketMapper.toProjectMarketDetailsDTO(projectMarketService.getByProjectMarketId(id));
    }

    public Page<ProjectMarketDTO> searchProjectMarketsByNamePattern(String name, Pageable pageable) {
        return projectMarketMapper.toProjectMarketDTOList(projectMarketService.searchActiveMarketsByNamePattern(name, pageable));
    }

    public ProjectMembersDTO getProjectMembersByMarketId(Long marketId) {
        var projectMarket = projectMarketService.getByProjectMarketId(marketId);
        return projectMemberMapper.fromProjectMarket(projectMarket);
    }

    @Transactional
    public void applyToProject(Long marketId, ApplyToProjectRequestDTO request) {
        String indexNumber = "s485954";
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        var projectMarket = projectMarketService.getByProjectMarketId(marketId);
        projectApplicationService.applyToMarket(applyToProjectRequestMapper.fromDTO(request, student, projectMarket));
    }

    //TODO use it when all will be ready
    private String getIndexNumberFromContext() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.valueOf(userDetails.getUsername());
    }
}
