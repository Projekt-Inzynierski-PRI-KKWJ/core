package pl.edu.amu.wmi.dto;

import lombok.Data;
import pl.edu.amu.wmi.enumerations.LevelOfRealization;

@Data
public class UpdateLevelDTO {
    private LevelOfRealization levelOfRealization;
}

