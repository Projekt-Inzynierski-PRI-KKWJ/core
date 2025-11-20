package pl.edu.amu.wmi.web.model;

import lombok.Data;

@Data
public class ProjectApplicationDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String contactData;
    private String skills;
    private String otherInformation;

}
