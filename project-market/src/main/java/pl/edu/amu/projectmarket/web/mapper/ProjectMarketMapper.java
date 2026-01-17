package pl.edu.amu.projectmarket.web.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.projectmarket.model.PublishProjectMarketRequest;
import pl.edu.amu.projectmarket.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDetailsDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketOwnerDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketUserDataDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProjectMarketMapper {

    private static final String AVAILABLE_SLOTS_PATTERN = "%s/%s";

    private ProjectMarketUserDataMapper projectMarketUserDataMapper;
    private StudentDAO studentDAO;

    @Mapping(target = "project", source = "project")
    @Mapping(target = "maxMembers", source = "requestDTO.maxMembers")
    @Mapping(target = "contactData", source = "requestDTO.contactData")
    public abstract PublishProjectMarketRequest toPublishRequest(ProjectCreateRequestDTO requestDTO, Project project);

    @Mapping(target = "projectName", expression = "java(getProjectName(projectMarket))")
    @Mapping(target = "projectDescription", expression = "java(getProjectDescription(projectMarket))")
    @Mapping(target = "technologies", expression = "java(getTechnologies(projectMarket))")
    @Mapping(target = "ownerDetails", expression = "java(getOwner(projectMarket))")
    @Mapping(target = "currentMembers", expression = "java(getCurrentMembers(projectMarket))")
    @Mapping(target = "studyYear", expression = "java(getStudyYear(projectMarket))")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "supervisorFeedback", source = "supervisorFeedback")
    @Mapping(target = "projectId", expression = "java(getProjectId(projectMarket))")
    @Mapping(target = "accepted", expression = "java(isProjectAccepted(projectMarket))")
    public abstract ProjectMarketDetailsDTO toProjectMarketDetailsDTO(ProjectMarket projectMarket);


    @Mapping(target = "projectName", expression = "java(getProjectName(projectMarket))")
    @Mapping(target = "projectDescription", expression = "java(getProjectDescription(projectMarket))")
    @Mapping(target = "ownerDetails", expression = "java(getOwner(projectMarket))")
    @Mapping(target = "availableSlots", expression = "java(calculateAvailableSlots(projectMarket))")
    @Mapping(target = "studyYear", expression = "java(getStudyYear(projectMarket))")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "supervisorFeedback", source = "supervisorFeedback")
    public abstract ProjectMarketDTO toProjectMarketDTO(ProjectMarket projectMarket);

    public Page<ProjectMarketDTO> toProjectMarketDTOList(Page<ProjectMarket> projectMarkets) {
        return projectMarkets.map(this::toProjectMarketDTO);
    }
    
    protected String getProjectName(ProjectMarket projectMarket) {
        return projectMarket.getProject() != null 
            ? projectMarket.getProject().getName() 
            : projectMarket.getProposalName();
    }
    
    protected String getProjectDescription(ProjectMarket projectMarket) {
        return projectMarket.getProject() != null 
            ? projectMarket.getProject().getDescription() 
            : projectMarket.getProposalDescription();
    }
    
    protected java.util.Set<String> getTechnologies(ProjectMarket projectMarket) {
        if (projectMarket.getProject() != null) {
            return projectMarket.getProject().getTechnologies();
        } else if (projectMarket.getProposalTechnologies() != null && !projectMarket.getProposalTechnologies().isEmpty()) {
            return java.util.Arrays.stream(projectMarket.getProposalTechnologies().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toSet());
        }
        return new java.util.HashSet<>();
    }

    protected ProjectMarketOwnerDTO getOwner(ProjectMarket projectMarket) {
        if (projectMarket.getProject() != null) {
            return projectMarketUserDataMapper.mapToProjectMarketOwner(projectMarket.getLeader());
        } else {
            // For proposals, get owner from proposal data
            var student = projectMarket.getProposalOwnerId() != null
                ? getStudentById(projectMarket.getProposalOwnerId())
                : null;
            return student != null 
                ? projectMarketUserDataMapper.mapToProjectMarketOwner(student.getUserData())
                : null;
        }
    }
    
    // Helper method to get student - will be injected via Spring
    protected Student getStudentById(Long id) {
        return studentDAO != null ? studentDAO.getReferenceById(id) : null;
    }

    protected List<ProjectMarketUserDataDTO> getCurrentMembers(ProjectMarket projectMarket) {
        if (projectMarket.getProject() == null) {
            // For proposals: return owner + accepted applicants
            var members = new java.util.ArrayList<ProjectMarketUserDataDTO>();
            
            // Add owner (admin)
            if (projectMarket.getProposalOwnerId() != null) {
                var owner = getStudentById(projectMarket.getProposalOwnerId());
                if (owner != null && owner.getUserData() != null) {
                    var ownerDTO = projectMarketUserDataMapper.mapToProjectMarketUserData(owner.getUserData());
                    ownerDTO.setId(owner.getId());
                    ownerDTO.setAdmin(true);
                    members.add(ownerDTO);
                }
            }
            
            // Add accepted applicants (non-admin)
            if (projectMarket.getApplications() != null) {
                var acceptedApplicants = projectMarket.getApplications().stream()
                    .filter(app -> app.getStatus() == pl.edu.amu.wmi.enumerations.ProjectApplicationStatus.ACCEPTED)
                    .map(app -> {
                        var dto = projectMarketUserDataMapper.mapToProjectMarketUserData(app.getStudent().getUserData());
                        dto.setId(app.getStudent().getId());
                        dto.setAdmin(false);
                        return dto;
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();
                members.addAll(acceptedApplicants);
            }
            
            return members;
        }
        
        // For approved projects: use existing project members with their roles
        return projectMarket.getProject().getAssignedStudents().stream()
            .map(sp -> {
                var dto = projectMarketUserDataMapper.mapToProjectMarketUserData(sp.getStudent().getUserData());
                dto.setId(sp.getStudent().getId());
                dto.setAdmin(sp.isProjectAdmin());
                return dto;
            })
            .toList();
    }

    protected static String calculateAvailableSlots(ProjectMarket projectMarket) {
        int maxSlots = projectMarket.getMaxMembers();
        int currentlyEnrolledInProject = projectMarket.getCurrentMembersCount();
        return AVAILABLE_SLOTS_PATTERN.formatted(currentlyEnrolledInProject, maxSlots);
    }

    protected static String getStudyYear(ProjectMarket projectMarket) {
        return projectMarket.getProject() != null 
            ? projectMarket.getProject().getStudyYear().getStudyYear()
            : projectMarket.getProposalStudyYear();
    }
    
    protected Long getProjectId(ProjectMarket projectMarket) {
        return projectMarket.getProject() != null 
            ? projectMarket.getProject().getId() 
            : null;
    }
    
    protected Boolean isProjectAccepted(ProjectMarket projectMarket) {
        if (projectMarket.getProject() == null) {
            return false;
        }
        var status = projectMarket.getProject().getAcceptanceStatus();
        return status != null && status != pl.edu.amu.wmi.enumerations.AcceptanceStatus.PENDING;
    }

    @Autowired
    public void setProjectMarketUserDataDTOMapper(ProjectMarketUserDataMapper projectMarketUserDataMapper) {
        this.projectMarketUserDataMapper = projectMarketUserDataMapper;
    }
    
    @Autowired
    public void setStudentDAO(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }
}
