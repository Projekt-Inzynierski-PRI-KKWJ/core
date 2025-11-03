package pl.edu.amu.wmi.web.model;

import lombok.Data;

@Data
public class ProjectMarketDTO {

    private Long id;
    private String projectName;
    private String projectDescription;
    private ProjectMarketOwnerDTO ownerDetails;
    private String availableSlots;

}
