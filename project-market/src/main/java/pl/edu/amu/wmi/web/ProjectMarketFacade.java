package pl.edu.amu.wmi.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.dao.StudyYearDAO;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;
import pl.edu.amu.wmi.enumerations.ProjectRole;
import pl.edu.amu.wmi.service.ProjectApplicationService;
import pl.edu.amu.wmi.service.ProjectMarketService;
import pl.edu.amu.wmi.service.ProjectService;
import pl.edu.amu.wmi.web.mapper.ApplyToProjectRequestMapper;
import pl.edu.amu.wmi.web.mapper.ProjectApplicationMapper;
import pl.edu.amu.wmi.web.mapper.ProjectMarketMapper;
import pl.edu.amu.wmi.web.mapper.ProjectMemberMapper;
import pl.edu.amu.wmi.web.mapper.ProjectRequestMapper;
import pl.edu.amu.wmi.web.model.ApplyToProjectRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectApplicationDTO;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDetailsDTO;
import pl.edu.amu.wmi.web.model.ProjectMembersDTO;
import pl.edu.amu.wmi.web.model.StudentProjectApplicationDTO;

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
    private final ProjectApplicationMapper projectApplicationMapper;
    private final ProjectDAO projectDAO;

    @Transactional
    public void createMarket(ProjectCreateRequestDTO request) {
        //add getIndexNumberFromContext
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
        //add getIndexNumberFromContext
        String indexNumber = "s485940";
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        var projectMarket = projectMarketService.getByProjectMarketId(marketId);
        if (ProjectMarketStatus.ACTIVE != projectMarket.getStatus()) {
            throw new IllegalStateException("Project market is not active.");
        }
        if (projectMarket.getMaxMembers() >= projectMarket.getMembers().size()) {
            throw new IllegalStateException("Project market reached max number of members.");
        }
        if (projectApplicationService.existsByStudentAndMProjectMarket(student, projectMarket)) {
            throw new IllegalStateException("Application already exists for this student and market.");
        }
        if (projectMarket.getMembers().stream().anyMatch(member -> member.getIndexNumber().equals(indexNumber))) {
            throw new IllegalStateException("Student already exists in project.");
        }

        projectApplicationService.applyToMarket(applyToProjectRequestMapper.fromDTO(request, student, projectMarket));
    }

    public List<ProjectApplicationDTO> getProjectApplicationByMarketIdInPendingStatus(Long marketId) {
        if (isOwnerByMarketId(marketId)) {
            var projectApplication = projectApplicationService.getApplicationForMarket(ProjectApplicationStatus.PENDING, marketId);
            return projectApplicationMapper.mapToProjectApplicationDTO(projectApplication);
        }
        throw new IllegalStateException("You are not allowed to perform this operation");
    }

    @Transactional
    public void approveCandidate(Long applicationId) {
        var application = checkAndGetProjectApplicationWithPendingStatus(applicationId);

        //add student to project
        var student = application.getStudent();
        var projectMarket = application.getProjectMarket();
        var project = projectMarket.getProject();

        project.addStudent(student, ProjectRole.NONE, false);
        projectDAO.save(project);

        //approve application
        projectApplicationService.accept(application);
    }

    public void rejectCandidate(Long applicationId) {
        var application = checkAndGetProjectApplicationWithPendingStatus(applicationId);

        projectApplicationService.reject(application);
    }

    public List<StudentProjectApplicationDTO> getApplicationsForStudent() {
        var applications = projectApplicationService.getApplicationForStudent(getStudentFromContext());
        return projectApplicationMapper.mapToStudentProjectApplicationDTO(applications);
    }


    private ProjectApplication checkAndGetProjectApplicationWithPendingStatus(Long applicationId) {
        var application = projectApplicationService.findProjectApplicationById(applicationId)
            .orElseThrow(() -> new IllegalStateException("Application with id " + applicationId + " not found"));
        if (!isOwnerByApplication(application)) {
            throw new IllegalStateException("You are not allowed to perform this operation");
        }

        if (ProjectApplicationStatus.PENDING != application.getStatus()) {
            throw new IllegalStateException("Application should be in PENDING state");
        }
        return application;
    }

    private boolean isOwnerByMarketId(Long marketId) {
        //add getIndexNumberFromContext
        var indexNumber = "s485953";
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        var projectMarket = projectMarketService.getByProjectMarketId(marketId);
        var owner = projectMarket.getProjectLeader();

        return owner != null && owner.getStudent().equals(student);
    }

    private boolean isOwnerByApplication(ProjectApplication application) {
        //add getIndexNumberFromContext
        var indexNumber = "s485953";
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        var owner = application.getProjectMarket().getProjectLeader();

        return owner != null && owner.getStudent().equals(student);
    }

    private Student getStudentFromContext() {
        //add getIndexNumberFromContext
        var indexNumber = "s485940";
        return studentDAO.findByUserData_IndexNumber(indexNumber);
    }

    //TODO use it when all will be ready
    private String getIndexNumberFromContext() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.valueOf(userDetails.getUsername());
    }
}
