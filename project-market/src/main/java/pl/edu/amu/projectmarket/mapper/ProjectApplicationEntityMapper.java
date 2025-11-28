package pl.edu.amu.projectmarket.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.projectmarket.model.ApplyToProjectRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectApplicationEntityMapper {

    @Mapping(target = "status", constant = "PENDING")
    ProjectApplication toEntity(ApplyToProjectRequest request);
}
