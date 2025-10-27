package pl.edu.amu.wmi.model;

import java.util.List;
import lombok.Data;

@Data
public class ProjectCreateRequestDto {

    private String name;
    private String description;
    private List<String> technologies;
    private String studyYear;
}
