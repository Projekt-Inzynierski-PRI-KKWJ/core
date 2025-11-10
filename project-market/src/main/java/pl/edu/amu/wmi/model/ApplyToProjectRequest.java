package pl.edu.amu.wmi.model;

import lombok.Data;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Student;

@Data
public class ApplyToProjectRequest {

    private ProjectMarket projectMarket;
    private Student student;
    private String contactData;
    private String skills;
    private String otherInformation;
}
