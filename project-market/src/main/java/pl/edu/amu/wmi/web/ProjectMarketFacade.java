package pl.edu.amu.wmi.web;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.dao.StudyYearDAO;
import pl.edu.amu.wmi.dao.SupervisorDAO;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;
import pl.edu.amu.wmi.service.ProjectApplicationService;
import pl.edu.amu.wmi.service.ProjectMarketService;
import pl.edu.amu.wmi.service.ProjectService;
import pl.edu.amu.wmi.web.mapper.ApplyToProjectRequestMapper;
import pl.edu.amu.wmi.web.mapper.ProjectApplicationMapper;
import pl.edu.amu.wmi.web.mapper.ProjectMarketMapper;
import pl.edu.amu.wmi.web.mapper.ProjectMarketSupervisorMapper;
import pl.edu.amu.wmi.web.mapper.ProjectMemberMapper;
import pl.edu.amu.wmi.web.mapper.ProjectRequestMapper;
import pl.edu.amu.wmi.web.model.ApplyToProjectRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectApplicationDTO;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDetailsDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketSupervisorDTO;
import pl.edu.amu.wmi.web.model.ProjectMembersDTO;
import pl.edu.amu.wmi.web.model.StudentProjectApplicationDTO;


@Component
@RequiredArgsConstructor
public class ProjectMarketFacade {

    private static final List<ProjectMarketStatus> PROJECT_MARKET_STATUSES_AVAILABLE_TO_CLOSE =
        List.of(ProjectMarketStatus.ACTIVE, ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR);
    private static final String MARKET_IS_NOT_ACTIVE = "Market is not active.";

    private final ProjectApplicationService projectApplicationService;
    private final ProjectMarketService projectMarketService;
    private final ProjectService projectService;

    private final SupervisorDAO supervisorDAO;
    private final StudentDAO studentDAO;
    private final StudyYearDAO studyYearDAO;

    private final ProjectRequestMapper projectRequestMapper;
    private final ProjectMarketMapper projectMarketMapper;
    private final ProjectMemberMapper projectMemberMapper;
    private final ApplyToProjectRequestMapper applyToProjectRequestMapper;
    private final ProjectApplicationMapper projectApplicationMapper;
    private final ProjectMarketSupervisorMapper projectMarketSupervisorMapper;

    @Transactional
    public void createMarket(ProjectCreateRequestDTO request) {
        var student = getStudentFromContext();
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
        var indexNumber = getIndexNumberFromContext();
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        var projectMarket = projectMarketService.getByProjectMarketId(marketId);
        if (ProjectMarketStatus.ACTIVE != projectMarket.getStatus()) {
            throw new IllegalStateException(MARKET_IS_NOT_ACTIVE);
        }
        if (projectMarket.getMaxMembers() <= projectMarket.getMembers().size()) {
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
        manipulateProjectApplicationByOwner(applicationId, ProjectApplication::accept);
    }

    @Transactional
    public void rejectCandidate(Long applicationId) {
        manipulateProjectApplicationByOwner(applicationId, ProjectApplication::reject);
    }

    public List<StudentProjectApplicationDTO> getApplicationsForStudent() {
        var applications = projectApplicationService.getApplicationForStudent(getStudentFromContext());
        return projectApplicationMapper.mapToStudentProjectApplicationDTO(applications);
    }

    public List<ProjectMarketSupervisorDTO> getSupervisors(String studyYear) {
        var supervisors = supervisorDAO.findAllByStudyYear(studyYear);
        return projectMarketSupervisorMapper.map(supervisors);
    }

    @Transactional
    public void submitProjectMarketToSupervisor(Long marketId, Long supervisorId) {
        var market = projectMarketService.getByProjectMarketId(marketId);
        if (!isOwnerByMarketId(marketId)) {
            throw new IllegalStateException("Only project owner can submit");
        }
        if (market.getStatus() != ProjectMarketStatus.ACTIVE) {
            throw new IllegalStateException(MARKET_IS_NOT_ACTIVE);
        }

        var supervisor = supervisorDAO.getReferenceById(supervisorId);
        if (supervisor.getProjects().size() >= supervisor.getMaxNumberOfProjects()) {
            throw new IllegalStateException("Maximum number of projects reached by supervisor.");
        }

        market.submit(supervisor);
        projectMarketService.save(market);
    }

    @Transactional
    public void closeProjectMarketByOwner(Long marketId) {
        if (!isOwnerByMarketId(marketId)) {
            throw new IllegalStateException("Only project owner can close this project market.");
        }
        var market = projectMarketService.getByProjectMarketId(marketId);
        var status = market.getStatus();
        if (!PROJECT_MARKET_STATUSES_AVAILABLE_TO_CLOSE.contains(status)) {
            throw new IllegalStateException(
                "Project market is not available to close due to wrong status. Available statuses %s, current: %s".formatted(
                    PROJECT_MARKET_STATUSES_AVAILABLE_TO_CLOSE.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining()), status));
        }

        market.closeByOwner();
        projectMarketService.save(market);
    }

    public Page<ProjectMarketDTO> getProjectMarketsForSupervisor(Pageable pageable) {
        var currentLoggedSupervisor = getSupervisorFromContext();
        if (currentLoggedSupervisor == null) {
            throw new IllegalStateException("Could not get supervisor data");
        }

        var projectMarkets = projectMarketService.findByAssignedSupervisor(currentLoggedSupervisor, pageable);
        return projectMarketMapper.toProjectMarketDTOList(projectMarkets);
    }

    @Transactional
    public void approveProjectAndCloseMarket(Long marketId) {
        manipulateProjectMarketBySupervisor(marketId, ProjectMarket::approveBySupervisor);
    }

    @Transactional
    public void rejectProjectAndCloseMarket(Long marketId) {
        manipulateProjectMarketBySupervisor(marketId, ProjectMarket::rejectBySupervisor);
    }

    private void manipulateProjectMarketBySupervisor(Long marketId, Consumer<ProjectMarket> consumer) {
        var market = projectMarketService.getByProjectMarketId(marketId);
        checkIfIsPossibleToManipulateProjectDataBySupervisor(market);
        consumer.accept(market);
        projectMarketService.save(market);
    }

    private void checkIfIsPossibleToManipulateProjectDataBySupervisor(ProjectMarket market) {
        var supervisor = getSupervisorFromContext();
        if (supervisor == null || !Objects.equals(market.getProject().getSupervisor().getId(), supervisor.getId())) {
            throw new IllegalStateException("Supervisor not found or is not assigned to this project market");
        }
        if (market.getStatus() != ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR) {
            throw new IllegalStateException("Market status is not SENT_FOR_APPROVAL_TO_SUPERVISOR");
        }
    }

    private void manipulateProjectApplicationByOwner(Long applicationId, Consumer<ProjectApplication> consumer) {
        var application = checkAndGetProjectApplicationWithPendingStatus(applicationId);
        consumer.accept(application);
        projectApplicationService.save(application);
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
        var student = getStudentFromContext();
        var projectMarket = projectMarketService.getByProjectMarketId(marketId);
        var owner = projectMarket.getProjectLeader();
        return owner != null && owner.getStudent().equals(student);
    }

    private boolean isOwnerByApplication(ProjectApplication application) {
        var student = getStudentFromContext();
        var owner = application.getProjectMarket().getProjectLeader();
        return owner != null && owner.getStudent().equals(student);
    }

    private Student getStudentFromContext() {
        var indexNumber = getIndexNumberFromContext();
        return studentDAO.findByUserData_IndexNumber(indexNumber);
    }

    private Supervisor getSupervisorFromContext() {
        var indexNumber = getIndexNumberFromContext();
        return supervisorDAO.findByUserData_IndexNumber(indexNumber);
    }

    private String getIndexNumberFromContext() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return String.valueOf(userDetails.getUsername());
    }
}
