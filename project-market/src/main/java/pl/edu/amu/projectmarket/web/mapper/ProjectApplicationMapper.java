package pl.edu.amu.projectmarket.web.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.projectmarket.web.model.ProjectApplicationDTO;
import pl.edu.amu.projectmarket.web.model.StudentProjectApplicationDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectApplicationMapper {

    @Mapping(target = "firstName", source = "student.userData.firstName")
    @Mapping(target = "lastName", source = "student.userData.lastName")
    @Mapping(target = "applicationDate", source = "creationDate")
    ProjectApplicationDTO mapToProjectApplicationDTO(ProjectApplication projectApplication);

    List<ProjectApplicationDTO> mapToProjectApplicationDTO(List<ProjectApplication> projectApplications);

    @Mapping(target = "applicationDate", source = "creationDate")
    @Mapping(target = "projectId", source = "projectMarket.id")
    @Mapping(target = "projectName", source = "projectMarket.proposalName")
    @Mapping(target = "projectDescription", source = "projectMarket.proposalDescription")
    @Mapping(target = "technologies", expression = "java(parseTechnologies(projectApplication.getProjectMarket().getProposalTechnologies()))")
    StudentProjectApplicationDTO mapToStudentProjectApplicationDTO(ProjectApplication projectApplication);

    List<StudentProjectApplicationDTO> mapToStudentProjectApplicationDTO(List<ProjectApplication> projectApplications);

    default java.util.List<String> parseTechnologies(String technologies) {
        if (technologies == null || technologies.trim().isEmpty()) {
            return java.util.List.of();
        }
        return java.util.Arrays.stream(technologies.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(java.util.stream.Collectors.toList());
    }
}
