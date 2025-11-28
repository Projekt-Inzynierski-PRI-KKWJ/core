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
public class StudentProjectApplicationDTO {

    private Long id;
    private LocalDateTime applicationDate;
    private LocalDateTime decisionDate;
    private String status;
}
