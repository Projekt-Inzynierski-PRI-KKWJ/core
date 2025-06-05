package pl.edu.amu.wmi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailNotificationDataDTO {
    private String name;
    private String email;
    private String content;
}
