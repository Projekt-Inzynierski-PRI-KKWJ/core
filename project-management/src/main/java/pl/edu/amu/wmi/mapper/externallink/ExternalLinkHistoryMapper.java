package pl.edu.amu.wmi.mapper.externallink;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.amu.wmi.entity.ExternalLinkHistory;
import pl.edu.amu.wmi.model.externallink.ExternalLinkHistoryDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExternalLinkHistoryMapper {

    @Mapping(target = "externalLinkId", source = "externalLink.id")
    @Mapping(target = "externalLinkName", source = "externalLink.externalLinkDefinition.name")
    @Mapping(target = "changedByUserName", expression = "java(history.getChangedByUser().getFirstName() + \" \" + history.getChangedByUser().getLastName())")
    @Mapping(target = "changedByUserIndexNumber", source = "changedByUser.indexNumber")
    @Mapping(target = "changeDate", source = "creationDate")
    @Mapping(target = "changeType", expression = "java(history.getChangeType().name())")
    @Mapping(target = "previousLinkType", expression = "java(history.getPreviousLinkType() != null ? history.getPreviousLinkType().name() : null)")
    ExternalLinkHistoryDTO mapToHistoryDto(ExternalLinkHistory history);

    List<ExternalLinkHistoryDTO> mapToHistoryDtoList(List<ExternalLinkHistory> historyList);

}
