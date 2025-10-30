package pl.edu.amu.wmi.web.mapper;

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
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.StudentProject;
import pl.edu.amu.wmi.model.PublishProjectMarketRequest;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketOwnerDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketUserDataDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProjectMarketMapper {

    private ProjectMarketUserDataDTOMapper projectMarketUserDataDTOMapper;

    @Mapping(target = "project", source = "project")
    @Mapping(target = "maxMembers", source = "requestDTO.maxMembers")
    @Mapping(target = "contactData", source = "requestDTO.contactData")
    public abstract PublishProjectMarketRequest toPublishRequest(ProjectCreateRequestDTO requestDTO, Project project);

    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "projectDescription", source = "project.description")
    @Mapping(target = "technologies", source = "project.technologies")
    @Mapping(target = "ownerDetails", expression = "java(getOwner(projectMarket))")
    @Mapping(target = "currentMembers", expression = "java(getCurrentMembers(projectMarket))")
    public abstract ProjectMarketDTO toProjectMarketDTO(ProjectMarket projectMarket);

    public Page<ProjectMarketDTO> toProjectMarketDTOList(Page<ProjectMarket> projectMarkets) {
        return projectMarkets.map(this::toProjectMarketDTO);
    }

    protected ProjectMarketOwnerDTO getOwner(ProjectMarket projectMarket) {
        var students = projectMarket.getProject().getAssignedStudents().stream().toList();
        var leader = students.stream().filter(StudentProject::isProjectAdmin).findFirst();
        if (leader.isEmpty()) {
            return null;
        }
        var userData = leader.get().getStudent().getUserData();
        return projectMarketUserDataDTOMapper.mapToProjectMarketOwner(userData);
    }

    protected List<ProjectMarketUserDataDTO> getCurrentMembers(ProjectMarket projectMarket) {
        var students = projectMarket.getProject().getAssignedStudents().stream().toList();
        var members = students.stream().filter(a -> !a.isProjectAdmin()).toList();
        if (members.isEmpty()) {
            return List.of();
        }
        var usersData = members.stream()
            .map(StudentProject::getStudent)
            .map(Student::getUserData)
            .toList();
        return projectMarketUserDataDTOMapper.mapToProjectMarketUserData(usersData);
    }

    @Autowired
    public void setProjectMarketUserDataDTOMapper(ProjectMarketUserDataDTOMapper projectMarketUserDataDTOMapper) {
        this.projectMarketUserDataDTOMapper = projectMarketUserDataDTOMapper;
    }
}
