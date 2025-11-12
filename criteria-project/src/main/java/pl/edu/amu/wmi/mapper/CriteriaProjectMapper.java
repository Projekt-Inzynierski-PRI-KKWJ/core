package pl.edu.amu.wmi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.edu.amu.wmi.dto.CriteriaProjectDTO;
import pl.edu.amu.wmi.entity.CriteriaProject;

@Mapper(componentModel = "spring")
public interface CriteriaProjectMapper {

    //Changed ID on index
    CriteriaProjectMapper INSTANCE = Mappers.getMapper(CriteriaProjectMapper.class);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "userThatAddedTheCriterium.indexNumber", target = "index")
    CriteriaProjectDTO toDto(CriteriaProject entity);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "index", target = "userThatAddedTheCriterium.indexNumber")
    CriteriaProject toEntity(CriteriaProjectDTO dto);

}
