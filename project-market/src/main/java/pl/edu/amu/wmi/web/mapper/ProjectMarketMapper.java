package pl.edu.amu.wmi.web.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.model.PublishProjectMarketRequest;
import pl.edu.amu.wmi.web.model.ProjectCreateRequestDTO;
import pl.edu.amu.wmi.web.model.ProjectMarketDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMarketMapper {


    ProjectMarketDTO toDto(ProjectMarket projectMarket);

    @Mapping(target = "project", source = "project")
    @Mapping(target = "maxMembers", source = "requestDTO.maxMembers")
    @Mapping(target = "contactData", source = "requestDTO.contactData")
    PublishProjectMarketRequest toPublishRequest(ProjectCreateRequestDTO requestDTO, Project project);
}
