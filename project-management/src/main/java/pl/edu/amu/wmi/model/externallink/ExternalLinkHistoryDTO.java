package pl.edu.amu.wmi.model.externallink;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.amu.wmi.entity.ExternalLinkHistory;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExternalLinkHistoryDTO {

    private String id;
    private String externalLinkId;
    private String externalLinkName;
    private String previousUrl;
    private String previousLinkType;
    private String previousOriginalFileName;
    private String previousContentType;
    private Long previousFileSize;
    private String changedByUserName;
    private String changedByUserIndexNumber;
    private String changeType;
    private String changeDescription;
    private LocalDateTime changeDate;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

}
