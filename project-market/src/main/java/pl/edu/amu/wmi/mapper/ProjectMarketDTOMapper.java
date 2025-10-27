package pl.edu.amu.wmi.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.model.ProjectMarketDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMarketDTOMapper {

    ProjectMarketDTO toDto(ProjectMarket projectMarket);
}
