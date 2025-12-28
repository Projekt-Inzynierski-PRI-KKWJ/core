package pl.edu.amu.projectmarket.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyToProjectRequestDTO {

    private String contactData;
    private String skills;
    private String otherInformation;
}
