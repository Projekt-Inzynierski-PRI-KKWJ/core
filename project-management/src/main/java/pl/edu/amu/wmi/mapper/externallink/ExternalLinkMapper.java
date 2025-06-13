package pl.edu.amu.wmi.mapper.externallink;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.amu.wmi.entity.ExternalLink;
import pl.edu.amu.wmi.model.externallink.ExternalLinkDTO;

import java.util.Set;


@Mapper(componentModel = "spring")
public interface ExternalLinkMapper {

    @Mapping(target = "externalLinkDefinition", ignore = true)
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    ExternalLink mapToEntity(ExternalLinkDTO dto);

    @Mapping(target = "name", source = "externalLinkDefinition.name")
    @Mapping(target = "columnHeader", source = "externalLinkDefinition.columnHeader")
    @Mapping(target = "deadline", source = "externalLinkDefinition.deadline")
    @Mapping(target = "linkType", expression = "java(entity.getLinkType() != null ? entity.getLinkType().name() : null)")
    ExternalLinkDTO mapToDto(ExternalLink entity);

    Set<ExternalLinkDTO> mapToDtoSet(Set<ExternalLink> externalLinks);

}
