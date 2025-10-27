package pl.edu.amu.wmi.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.StudyYear;
import pl.edu.amu.wmi.model.ProjectCreateRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMarketProjectMapper {

    @Mapping(target = "acceptanceStatus", constant = "PENDING")
    @Mapping(target = "studyYear", source = "year")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    Project toEntity(ProjectCreateRequest projectCreateRequest, StudyYear year);
}
