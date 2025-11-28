package pl.edu.amu.wmi.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMarketUserDataDTO {

    private String firstName;
    private String lastName;
    private String email;
}
