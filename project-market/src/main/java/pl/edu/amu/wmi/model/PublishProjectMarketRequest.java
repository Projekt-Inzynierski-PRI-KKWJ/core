package pl.edu.amu.wmi.model;

import lombok.Data;
import pl.edu.amu.wmi.entity.Project;

@Data
public class PublishProjectMarketRequest {

    private Project project;
    private int maxMembers;
    private String contactData;
}
