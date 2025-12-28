package pl.edu.amu.wmi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotificationStudentDataDTO {
    private String name;
    private String email;
}
