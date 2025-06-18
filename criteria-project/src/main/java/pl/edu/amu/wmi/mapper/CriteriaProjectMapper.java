package pl.edu.amu.wmi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.edu.amu.wmi.dto.CriteriaProjectDTO;
import pl.edu.amu.wmi.entity.CriteriaProject;

@Mapper(componentModel = "spring")
public interface CriteriaProjectMapper {

    CriteriaProjectMapper INSTANCE = Mappers.getMapper(CriteriaProjectMapper.class);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "userThatAddedTheCriterium.id", target = "userId")
    @Mapping(source = "id", target = "id")
    CriteriaProjectDTO toDto(CriteriaProject entity);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "userId", target = "userThatAddedTheCriterium.id")
    CriteriaProject toEntity(CriteriaProjectDTO dto);
}
