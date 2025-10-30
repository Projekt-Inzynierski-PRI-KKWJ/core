package pl.edu.amu.wmi.model;

import lombok.Data;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Student;

@Data
public class ProjectApplicationRequest {

    private String message;
    private ProjectMarket projectMarket;
    private Student student;
}
