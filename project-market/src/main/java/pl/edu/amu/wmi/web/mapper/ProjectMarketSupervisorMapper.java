package pl.edu.amu.wmi.web.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.web.model.ProjectMarketSupervisorDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMarketSupervisorMapper {

    @Mapping(target = "firstName", source = "supervisor.userData.firstName")
    @Mapping(target = "lastName", source = "supervisor.userData.lastName")
    ProjectMarketSupervisorDTO map(Supervisor supervisor);

    List<ProjectMarketSupervisorDTO> map(List<Supervisor> supervisors);
}
