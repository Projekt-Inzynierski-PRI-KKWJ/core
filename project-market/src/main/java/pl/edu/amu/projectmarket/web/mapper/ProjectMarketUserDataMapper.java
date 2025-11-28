package pl.edu.amu.projectmarket.web.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.UserData;
import pl.edu.amu.projectmarket.web.model.ProjectMarketOwnerDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketUserDataDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMarketUserDataMapper {

    ProjectMarketOwnerDTO mapToProjectMarketOwner(UserData userData);

    ProjectMarketUserDataDTO mapToProjectMarketUserData(UserData userData);

    List<ProjectMarketUserDataDTO> mapToProjectMarketUserData(List<UserData> userData);
}
