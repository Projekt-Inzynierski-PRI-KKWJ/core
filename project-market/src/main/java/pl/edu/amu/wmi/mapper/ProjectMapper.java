package pl.edu.amu.wmi.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.model.ProjectCreateRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "acceptanceStatus", constant = "DRAFT")
    Project toEntity(ProjectCreateRequest projectCreateRequest);

    Project toSubmit(Project projectm )
}
