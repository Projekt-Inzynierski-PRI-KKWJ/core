package pl.edu.amu.projectmarket.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMarketOwnerDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String indexNumber;
}
