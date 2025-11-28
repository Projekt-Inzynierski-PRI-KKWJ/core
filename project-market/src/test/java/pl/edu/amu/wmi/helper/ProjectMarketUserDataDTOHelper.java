package pl.edu.amu.wmi.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.web.model.ProjectMarketUserDataDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectMarketUserDataDTOHelper {

    public static ProjectMarketUserDataDTO.ProjectMarketUserDataDTOBuilder builder() {
        return ProjectMarketUserDataDTO.builder()
            .firstName(RandomStringUtils.randomAlphanumeric(20))
            .lastName(RandomStringUtils.randomAlphanumeric(20))
            .email(RandomStringUtils.randomAlphanumeric(20));
    }

    public static ProjectMarketUserDataDTO defaults() {
        return builder().build();
    }
}
