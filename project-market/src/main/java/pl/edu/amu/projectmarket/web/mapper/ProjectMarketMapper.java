package pl.edu.amu.projectmarket.web.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.ProjectMarket;
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

    @Mapping(target = "project", source = "project")
    @Mapping(target = "maxMembers", source = "requestDTO.maxMembers")
    @Mapping(target = "contactData", source = "requestDTO.contactData")
    public abstract PublishProjectMarketRequest toPublishRequest(ProjectCreateRequestDTO requestDTO, Project project);

    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "projectDescription", source = "project.description")
    @Mapping(target = "technologies", source = "project.technologies")
    @Mapping(target = "ownerDetails", expression = "java(getOwner(projectMarket))")
    @Mapping(target = "currentMembers", expression = "java(getCurrentMembers(projectMarket))")
    @Mapping(target = "studyYear", expression = "java(getStudyYear(projectMarket))")
    public abstract ProjectMarketDetailsDTO toProjectMarketDetailsDTO(ProjectMarket projectMarket);


    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "projectDescription", source = "project.description")
    @Mapping(target = "ownerDetails", expression = "java(getOwner(projectMarket))")
    @Mapping(target = "availableSlots", expression = "java(calculateAvailableSlots(projectMarket))")
    @Mapping(target = "studyYear", expression = "java(getStudyYear(projectMarket))")
    public abstract ProjectMarketDTO toProjectMarketDTO(ProjectMarket projectMarket);

    public Page<ProjectMarketDTO> toProjectMarketDTOList(Page<ProjectMarket> projectMarkets) {
        return projectMarkets.map(this::toProjectMarketDTO);
    }

    protected ProjectMarketOwnerDTO getOwner(ProjectMarket projectMarket) {
        return projectMarketUserDataMapper.mapToProjectMarketOwner(projectMarket.getLeader());
    }

    protected List<ProjectMarketUserDataDTO> getCurrentMembers(ProjectMarket projectMarket) {
        return projectMarketUserDataMapper.mapToProjectMarketUserData(projectMarket.getMembers());
    }

    protected static String calculateAvailableSlots(ProjectMarket projectMarket) {
        int maxSlots = projectMarket.getMaxMembers();
        int currentlyEnrolledInProject = projectMarket.getCurrentMembersCount();
        return AVAILABLE_SLOTS_PATTERN.formatted(currentlyEnrolledInProject, maxSlots);
    }

    protected static String getStudyYear(ProjectMarket projectMarket) {
        return projectMarket.getProject().getStudyYear().getStudyYear();
    }

    @Autowired
    public void setProjectMarketUserDataDTOMapper(ProjectMarketUserDataMapper projectMarketUserDataMapper) {
        this.projectMarketUserDataMapper = projectMarketUserDataMapper;
    }
}
