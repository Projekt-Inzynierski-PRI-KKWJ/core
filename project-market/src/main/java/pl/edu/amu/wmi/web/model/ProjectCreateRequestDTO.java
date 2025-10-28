package pl.edu.amu.wmi.web.model;

import java.util.List;
import lombok.Data;

@Data
public class ProjectCreateRequestDTO {

    private String name;
    private String description;
    private List<String> technologies;
    private String studyYear;
    private String contactData;
    private int maxMembers;
}
