package pl.edu.amu.projectmarket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.amu.wmi.entity.Project;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishProjectMarketRequest {

    private Project project;
    private int maxMembers;
    private String contactData;
    private String proposalName;
    private String proposalDescription;
    private Long proposalOwnerId;
    private String proposalStudyYear;
    private String proposalTechnologies; // Comma-separated
}
