package pl.edu.amu.wmi.web.model;

import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class ProjectMarketDetailsDTO {

    private Long id;
    private String projectName;
    private String projectDescription;
    private Set<String> technologies;
    private ProjectMarketOwnerDTO ownerDetails;
    private Integer maxMembers;
    private String contactData;
    private List<ProjectMarketUserDataDTO> currentMembers;
    private String creationDate;
    private String modificationDate;
}
