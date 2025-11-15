package pl.edu.amu.wmi.web.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.web.model.ProjectApplicationDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectApplicationMapper {

    @Mapping(target = "firstName", source = "student.userData.firstName")
    @Mapping(target = "lastName", source = "student.userData.lastName")
    ProjectApplicationDTO map(ProjectApplication projectApplication);

    List<ProjectApplicationDTO> map(List<ProjectApplication> projectApplications);
}
