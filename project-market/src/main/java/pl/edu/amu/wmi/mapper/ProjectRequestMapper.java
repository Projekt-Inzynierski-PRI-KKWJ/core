package pl.edu.amu.wmi.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.model.ProjectCreateRequest;
import pl.edu.amu.wmi.model.ProjectCreateRequestDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectRequestMapper {

    ProjectCreateRequest fromDto(ProjectCreateRequestDto createRequestDto);
}
