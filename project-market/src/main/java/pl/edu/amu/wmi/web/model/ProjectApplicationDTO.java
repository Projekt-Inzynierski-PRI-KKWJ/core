package pl.edu.amu.wmi.web.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectApplicationDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String contactData;
    private String skills;
    private String otherInformation;
    private String status;
    private LocalDateTime applicationDate;
    private LocalDateTime decisionDate;
}
