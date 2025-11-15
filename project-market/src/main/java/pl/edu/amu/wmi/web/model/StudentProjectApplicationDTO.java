package pl.edu.amu.wmi.web.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StudentProjectApplicationDTO {

    private Long id;
    private LocalDateTime applicationDate;
    private LocalDateTime decisionDate;
    private String status;
}
