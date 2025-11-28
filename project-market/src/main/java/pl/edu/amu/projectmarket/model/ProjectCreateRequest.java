package pl.edu.amu.projectmarket.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.StudyYear;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequest {

    private String name;
    private String description;
    private List<String> technologies;
    private StudyYear studyYear;
    private Student student;
    private String contactData;
    private int maxMembers;
}
