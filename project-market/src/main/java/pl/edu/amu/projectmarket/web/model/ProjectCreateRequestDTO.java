package pl.edu.amu.projectmarket.web.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequestDTO {

    private String name;
    private String description;
    private List<String> technologies;
    private String studyYear;
    private String contactData;
    private int maxMembers;
}
