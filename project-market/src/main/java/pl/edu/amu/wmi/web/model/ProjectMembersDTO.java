package pl.edu.amu.wmi.web.model;

import java.util.List;
import lombok.Data;

@Data
public class ProjectMembersDTO {

    private Integer availableSlots;
    private Integer totalSlots;
    private List<ProjectMemberDTO> members;
}
