package pl.edu.amu.wmi.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
