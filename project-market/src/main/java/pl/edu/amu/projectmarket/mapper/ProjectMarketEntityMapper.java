package pl.edu.amu.projectmarket.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.projectmarket.model.PublishProjectMarketRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMarketEntityMapper {

    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "project", source = "request.project")
    @Mapping(target = "maxMembers", source = "request.maxMembers")
    @Mapping(target = "contactData", source = "request.contactData")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    ProjectMarket fromRequest(PublishProjectMarketRequest request);
}
