package pl.edu.amu.wmi.helper;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.web.model.ProjectMemberDTO;
import pl.edu.amu.wmi.web.model.ProjectMembersDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectMembersDTOHelper {

    public static ProjectMembersDTO.ProjectMembersDTOBuilder projectMembersBuilder() {
        return ProjectMembersDTO.builder()
            .members(List.of(projectMemberDefaults(), projectMemberDefaults()))
            .availableSlots(Integer.parseInt(RandomStringUtils.randomNumeric(3)))
            .totalSlots(Integer.parseInt(RandomStringUtils.randomNumeric(3)));
    }

    public static ProjectMembersDTO projectMembersDefaults() {
        return projectMembersBuilder().build();
    }

    public static ProjectMemberDTO.ProjectMemberDTOBuilder projectMemberBuilder() {
        return ProjectMemberDTO.builder()
            .firstName(RandomStringUtils.randomAlphabetic(20))
            .lastName(RandomStringUtils.randomAlphabetic(20));
    }

    public static ProjectMemberDTO projectMemberDefaults() {
        return projectMemberBuilder().build();
    }
}
