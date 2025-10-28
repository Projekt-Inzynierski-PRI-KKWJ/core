package pl.edu.amu.wmi.model;

import java.util.List;
import lombok.Data;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.StudyYear;

@Data
public class ProjectCreateRequest {

    private String name;
    private String description;
    private List<String> technologies;
    private StudyYear studyYear;
    private Student student;
    private Long supervisorId;
    private String contactData;
    private int maxMembers;
}
