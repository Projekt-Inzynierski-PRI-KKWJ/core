package pl.edu.amu.projectmarket.web;

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
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.dao.ProjectMarketDAO;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.dao.StudyYearDAO;
import pl.edu.amu.wmi.dao.SupervisorDAO;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;
import pl.edu.amu.projectmarket.model.PublishProjectMarketRequest;
import pl.edu.amu.projectmarket.service.ProjectApplicationService;
import pl.edu.amu.projectmarket.service.ProjectMarketService;
import pl.edu.amu.projectmarket.service.ProjectService;
import pl.edu.amu.projectmarket.web.mapper.ApplyToProjectRequestMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectApplicationMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectMarketMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectMarketSupervisorMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectMemberMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectRequestMapper;
import pl.edu.amu.projectmarket.web.model.ApplyToProjectRequestDTO;
import pl.edu.amu.projectmarket.web.model.ProjectApplicationDTO;
import pl.edu.amu.projectmarket.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDetailsDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketSupervisorDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMembersDTO;
import pl.edu.amu.projectmarket.web.model.StudentProjectApplicationDTO;


@Component
@RequiredArgsConstructor
public class ProjectMarketFacade {

    private static final List<ProjectMarketStatus> PROJECT_MARKET_STATUSES_AVAILABLE_TO_CLOSE =
        List.of(ProjectMarketStatus.ACTIVE, ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR);
    private static final String MARKET_IS_NOT_ACTIVE = "Market is not active.";
    public static final String STUDENT_NOT_FOUND = "Student not found.";

    private final ProjectApplicationService projectApplicationService;
    private final ProjectMarketService projectMarketService;
    private final ProjectService projectService;

    private final ProjectDAO projectDAO;
    private final ProjectMarketDAO projectMarketDAO;
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
        if(student == null) {
            throw new IllegalStateException("Student not found");
        }
        var studyYear = studyYearDAO.findByStudyYear(request.getStudyYear());
        
        // Don't create Project yet - just store as proposal
        var publishRequest = PublishProjectMarketRequest.builder()
            .project(null) // No project yet
            .maxMembers(request.getMaxMembers())
            .contactData(request.getContactData())
            .proposalName(request.getName())
            .proposalDescription(request.getDescription())
            .proposalOwnerId(student.getId())
            .proposalStudyYear(request.getStudyYear())
            .proposalTechnologies(request.getTechnologies() != null ? String.join(",", request.getTechnologies()) : "")
            .build();
            
        projectMarketService.publishMarket(publishRequest);
    }

    @Transactional
    public void updateProject(Long projectMarketId, ProjectCreateRequestDTO request) {
        var student = getStudentFromContext();
        if (student == null) {
            throw new IllegalStateException(STUDENT_NOT_FOUND);
        }

        var projectMarket = projectMarketDAO.findById(projectMarketId)
            .orElseThrow(() -> new IllegalArgumentException("Project market not found"));

        var project = projectMarket.getProject();
        
        // Only allow updates if project market is in ACTIVE status
        if (projectMarket.getStatus() != ProjectMarketStatus.ACTIVE) {
            throw new IllegalStateException("Can only update projects in ACTIVE status");
        }
        
        if (project == null) {
            // Editing a proposal - check if user is the proposal owner
            if (!projectMarket.getProposalOwnerId().equals(student.getId())) {
                throw new IllegalStateException("Only proposal owner can update the proposal");
            }
            
            // Update proposal fields
            projectMarket.setProposalName(request.getName());
            projectMarket.setProposalDescription(request.getDescription());
            projectMarket.setProposalTechnologies(request.getTechnologies() != null ? String.join(",", request.getTechnologies()) : "");
            projectMarket.setMaxMembers(request.getMaxMembers());
            projectMarket.setContactData(request.getContactData());
            projectMarketDAO.save(projectMarket);
        } else {
            // Editing an approved project - check if user is the project admin
            var isOwner = project.getAssignedStudents().stream()
                .anyMatch(sp -> sp.getStudent().getId().equals(student.getId()) && sp.isProjectAdmin());

            if (!isOwner) {
                throw new IllegalStateException("Only project owner can update the project");
            }

            // Update project fields
            project.setName(request.getName());
            project.setDescription(request.getDescription());
            project.setTechnologies(new java.util.HashSet<>(request.getTechnologies()));
            projectDAO.save(project);

            // Update project market fields
            projectMarket.setMaxMembers(request.getMaxMembers());
            projectMarket.setContactData(request.getContactData());
            projectMarketDAO.save(projectMarket);
        }
    }

    public Page<ProjectMarketDTO> getAllActiveProjectMarkets(Pageable pageable) {
        return projectMarketMapper.toProjectMarketDTOList(projectMarketService.listActiveMarkets(pageable));
    }

    public Page<ProjectMarketDTO> getAllProjectMarkets(Pageable pageable) {
        return projectMarketMapper.toProjectMarketDTOList(projectMarketService.listAllMarkets(pageable));
    }

    public ProjectMarketDetailsDTO getMarketDetailsById(Long id) {
        return projectMarketMapper.toProjectMarketDetailsDTO(projectMarketService.getProjectMarketById(id));
    }

    public Page<ProjectMarketDTO> searchProjectMarketsByNamePattern(String name, Pageable pageable) {
        return projectMarketMapper.toProjectMarketDTOList(projectMarketService.searchActiveMarketsByNamePattern(name, pageable));
    }

    public ProjectMembersDTO getProjectMembersByMarketId(Long marketId) {
        var projectMarket = projectMarketService.getProjectMarketById(marketId);
        return projectMemberMapper.fromProjectMarket(projectMarket);
    }

    @Transactional
    public void applyToProject(Long marketId, ApplyToProjectRequestDTO request) {
        var indexNumber = getIndexNumberFromContext();
        var student = studentDAO.findByUserData_IndexNumber(indexNumber);
        if (student == null) {
            throw new IllegalStateException("Student not found");
        }
        var projectMarket = projectMarketService.getProjectMarketById(marketId);
        if (ProjectMarketStatus.ACTIVE != projectMarket.getStatus()) {
            throw new IllegalStateException(MARKET_IS_NOT_ACTIVE);
        }
        if (projectMarket.getMaxMembers() <= projectMarket.getMembers().size()) {
            throw new IllegalStateException("Project market reached max number of members.");
        }
        if (projectApplicationService.existsByStudentAndProjectMarket(student, projectMarket)) {
            throw new IllegalStateException("Application already exists for this student and market.");
        }
        if (projectMarket.getMembers().stream().anyMatch(member -> member.getIndexNumber().equals(indexNumber))) {
            throw new IllegalStateException("Student already exists in project.");
        }

        projectApplicationService.applyToMarket(applyToProjectRequestMapper.fromDTO(request, student, projectMarket));
    }

    public List<ProjectApplicationDTO> getProjectApplicationByMarketIdInPendingStatus(Long marketId) {
        if (isOwnerByMarketId(marketId)) {
            var projectApplication = projectApplicationService.getApplicationsForMarket(ProjectApplicationStatus.PENDING, marketId);
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
        var applications = projectApplicationService.getApplicationsForStudent(getStudentFromContext());
        return projectApplicationMapper.mapToStudentProjectApplicationDTO(applications);
    }

    public List<ProjectMarketSupervisorDTO> getSupervisors(String studyYear) {
        var supervisors = supervisorDAO.findAllByStudyYear(studyYear);
        return projectMarketSupervisorMapper.map(supervisors);
    }

    @Transactional
    public void submitProjectMarketToSupervisor(Long marketId, Long supervisorId) {
        var market = projectMarketService.getProjectMarketById(marketId);
        if (!isOwnerByMarketId(marketId)) {
            throw new IllegalStateException("Only project owner can submit project to supervisor");
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
        var market = projectMarketService.getProjectMarketById(marketId);
        if (!isOwnerByMarket(market)) {
            throw new IllegalStateException("Only project owner can close this project market.");
        }
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

    public Page<ProjectMarketDTO> getMyProjects(Pageable pageable) {
        var student = getStudentFromContext();
        if (student == null) {
            throw new IllegalStateException(STUDENT_NOT_FOUND);
        }
        return projectMarketMapper.toProjectMarketDTOList(
            projectMarketService.findByProjectLeader(student.getId(), pageable));
    }

    @Transactional
    public void leaveProject(Long marketId) {
        var student = getStudentFromContext();
        if (student == null) {
            throw new IllegalStateException(STUDENT_NOT_FOUND);
        }
        
        var projectMarket = projectMarketDAO.findById(marketId)
            .orElseThrow(() -> new IllegalArgumentException("Project market not found"));
        
        var project = projectMarket.getProject();
        
        // Can only leave approved projects (not proposals)
        if (project == null) {
            throw new IllegalStateException("Cannot leave project proposals. Only approved projects can be left.");
        }
        
        // Find the student in the project
        var studentProject = project.getAssignedStudents().stream()
            .filter(sp -> sp.getStudent().getId().equals(student.getId()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Student is not part of this project"));
        
        // Don't allow admin to leave
        if (studentProject.isProjectAdmin()) {
            throw new IllegalStateException("Project admin cannot leave the project");
        }
        
        // Only allow leaving if project is not yet accepted
        if (project.getAcceptanceStatus() != null && project.getAcceptanceStatus() != pl.edu.amu.wmi.enumerations.AcceptanceStatus.PENDING) {
            throw new IllegalStateException("Cannot leave an accepted project");
        }
        
        // Remove student from project
        project.getAssignedStudents().remove(studentProject);
        projectDAO.save(project);
    }

    @Transactional
    public void approveProjectAndCloseMarket(Long marketId) {
        // Create the actual Project now that supervisor approved
        var projectMarket = projectMarketService.getProjectMarketById(marketId);
        
        if (projectMarket.getProject() == null) {
            // Create the project from the proposal data
            var student = studentDAO.getReferenceById(projectMarket.getProposalOwnerId());
            var studyYear = studyYearDAO.findByStudyYear(projectMarket.getProposalStudyYear());
            var supervisor = supervisorDAO.getReferenceById(projectMarket.getProposalSupervisorId());
            
            java.util.List<String> technologies = projectMarket.getProposalTechnologies() != null && !projectMarket.getProposalTechnologies().isEmpty()
                ? java.util.Arrays.stream(projectMarket.getProposalTechnologies().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList())
                : java.util.List.of();
            
            var projectRequest = ProjectCreateRequestDTO.builder()
                .name(projectMarket.getProposalName())
                .description(projectMarket.getProposalDescription())
                .technologies(technologies)
                .studyYear(projectMarket.getProposalStudyYear())
                .contactData(projectMarket.getContactData())
                .maxMembers(projectMarket.getMaxMembers())
                .build();
                
            var project = projectService.createProject(projectRequestMapper.fromDto(projectRequest, studyYear, student));
            project.setSupervisor(supervisor);
            projectMarket.setProject(project);
            projectMarketService.save(projectMarket);
            
            // Add all accepted applicants to the newly created project
            var acceptedApplications = projectMarket.getApplications().stream()
                .filter(app -> app.getStatus() == ProjectApplicationStatus.ACCEPTED)
                .toList();
            
            for (var application : acceptedApplications) {
                project.addStudent(application.getStudent(), pl.edu.amu.wmi.enumerations.ProjectRole.NONE, false);
            }
            
            if (!acceptedApplications.isEmpty()) {
                projectDAO.save(project);
            }
        }
        
        manipulateProjectMarketBySupervisor(marketId, ProjectMarket::approveBySupervisor);
    }

    @Transactional
    public void rejectProjectAndCloseMarket(Long marketId) {
        manipulateProjectMarketBySupervisor(marketId, ProjectMarket::rejectBySupervisor);
    }

    @Transactional
    public void provideFeedback(Long marketId, String feedback) {
        var supervisor = getSupervisorFromContext();
        if (supervisor == null) {
            throw new IllegalStateException("Supervisor not found");
        }
        var market = projectMarketService.getProjectMarketById(marketId);
        
        // Check supervisor assignment for both proposals and approved projects
        Long assignedSupervisorId = market.getProject() != null 
            ? (market.getProject().getSupervisor() != null ? market.getProject().getSupervisor().getId() : null)
            : market.getProposalSupervisorId();
            
        if (assignedSupervisorId == null || !Objects.equals(assignedSupervisorId, supervisor.getId())) {
            throw new IllegalStateException("You are not the supervisor of this project");
        }
        market.setSupervisorFeedback(feedback);
        projectMarketService.save(market);
    }

    private void manipulateProjectMarketBySupervisor(Long marketId, Consumer<ProjectMarket> consumer) {
        var market = projectMarketService.getProjectMarketById(marketId);
        checkIfIsPossibleToManipulateProjectDataBySupervisor(market);
        consumer.accept(market);
        projectMarketService.save(market);
    }

    private void checkIfIsPossibleToManipulateProjectDataBySupervisor(ProjectMarket market) {
        var supervisor = getSupervisorFromContext();
        
        // Check supervisor assignment for both proposals and approved projects
        Long assignedSupervisorId = market.getProject() != null 
            ? (market.getProject().getSupervisor() != null ? market.getProject().getSupervisor().getId() : null)
            : market.getProposalSupervisorId();
        
        Long currentSupervisorId = supervisor != null ? supervisor.getId() : null;
        
        if (supervisor == null || assignedSupervisorId == null || !assignedSupervisorId.equals(currentSupervisorId)) {
            throw new IllegalStateException(
                String.format("Supervisor not found or is not assigned to this project market. " +
                    "Current supervisor ID: %s, Assigned supervisor ID: %s, Market ID: %s, Project: %s", 
                    currentSupervisorId, assignedSupervisorId, market.getId(), market.getProject() != null ? "exists" : "null")
            );
        }
        if (market.getStatus() != ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR) {
            throw new IllegalStateException("Project market status is not SENT_FOR_APPROVAL_TO_SUPERVISOR");
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
        if (student == null) {
            throw new IllegalStateException(STUDENT_NOT_FOUND);
        }
        var projectMarket = projectMarketService.getProjectMarketById(marketId);
        
        // For proposals, check proposalOwnerId
        if (projectMarket.getProject() == null) {
            return projectMarket.getProposalOwnerId() != null && projectMarket.getProposalOwnerId().equals(student.getId());
        }
        
        // For approved projects, check project leader
        var owner = projectMarket.getProjectLeader();
        return owner != null && owner.getStudent().getId().equals(student.getId());
    }

    private boolean isOwnerByMarket(ProjectMarket projectMarket) {
        var student = getStudentFromContext();
        if (student == null) {
            throw new IllegalStateException(STUDENT_NOT_FOUND);
        }
        
        // For proposals, check proposalOwnerId
        if (projectMarket.getProject() == null) {
            return projectMarket.getProposalOwnerId() != null && projectMarket.getProposalOwnerId().equals(student.getId());
        }
        
        // For approved projects, check project leader
        var owner = projectMarket.getProjectLeader();
        return owner != null && owner.getStudent().getId().equals(student.getId());
    }

    private boolean isOwnerByApplication(ProjectApplication application) {
        var student = getStudentFromContext();
        if (student == null) {
            throw new IllegalStateException(STUDENT_NOT_FOUND);
        }
        var projectMarket = application.getProjectMarket();
        
        // For proposals, check proposalOwnerId
        if (projectMarket.getProject() == null) {
            return projectMarket.getProposalOwnerId() != null && projectMarket.getProposalOwnerId().equals(student.getId());
        }
        
        // For approved projects, check project leader
        var owner = projectMarket.getProjectLeader();
        return owner != null && owner.getStudent().getId().equals(student.getId());
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
