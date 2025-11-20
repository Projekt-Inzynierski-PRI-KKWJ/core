package pl.edu.amu.wmi.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.ProjectMarket;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMarketMapper {

    @Mapping(target = "status", constant = "ACTIVE")
    ProjectMarket fromProject(Project project);
}
