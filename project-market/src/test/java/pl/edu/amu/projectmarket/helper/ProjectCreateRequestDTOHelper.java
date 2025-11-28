package pl.edu.amu.projectmarket.helper;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.projectmarket.web.model.ProjectCreateRequestDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectCreateRequestDTOHelper {

    public static ProjectCreateRequestDTO.ProjectCreateRequestDTOBuilder builder() {
        return ProjectCreateRequestDTO.builder()
            .name(RandomStringUtils.randomAlphabetic(10))
            .description(RandomStringUtils.randomAlphabetic(10))
            .contactData(RandomStringUtils.randomAlphabetic(10))
            .maxMembers(Integer.parseInt(RandomStringUtils.randomNumeric(4)))
            .technologies(List.of(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)))
            .studyYear("FULL_TIME#2023");
    }

    public static ProjectCreateRequestDTO defaults() {
        return builder().build();
    }
}
