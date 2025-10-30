package pl.edu.amu.wmi.model;

import lombok.Data;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.Supervisor;

@Data
public class SubmitProjectRequest {

    private Project project;
    private Supervisor supervisor;
}
