package pl.edu.amu.wmi.web.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.model.ApplyToProjectRequest;
import pl.edu.amu.wmi.web.model.ApplyToProjectRequestDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApplyToProjectRequestMapper {

    @Mapping(target = "student", source = "student")
    @Mapping(target = "projectMarket", source = "projectMarket")
    @Mapping(target = "contactData", source = "requestDTO.contactData")
    ApplyToProjectRequest fromDTO(ApplyToProjectRequestDTO requestDTO, Student student, ProjectMarket projectMarket);

}
