package pl.edu.amu.projectmarket.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMarketDTO {

    private Long id;
    private String projectName;
    private String projectDescription;
    private ProjectMarketOwnerDTO ownerDetails;
    private String availableSlots;
    private String studyYear;
    private ProjectMarketStatus status;
    private String supervisorFeedback;

}
