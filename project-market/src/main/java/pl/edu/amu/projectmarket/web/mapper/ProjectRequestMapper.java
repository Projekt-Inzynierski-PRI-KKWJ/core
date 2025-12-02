package pl.edu.amu.projectmarket.web.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.StudyYear;
import pl.edu.amu.projectmarket.model.ProjectCreateRequest;
import pl.edu.amu.projectmarket.web.model.ProjectCreateRequestDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectRequestMapper {

    @Mapping(target = "studyYear", source = "studyYear")
    @Mapping(target = "student", source = "student")
    ProjectCreateRequest fromDto(ProjectCreateRequestDTO createRequestDto, StudyYear studyYear, Student student);
}
