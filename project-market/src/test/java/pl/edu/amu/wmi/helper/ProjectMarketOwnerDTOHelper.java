package pl.edu.amu.wmi.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.web.model.ProjectMarketOwnerDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectMarketOwnerDTOHelper {

    public static ProjectMarketOwnerDTO.ProjectMarketOwnerDTOBuilder builder() {
        return ProjectMarketOwnerDTO.builder()
            .firstName(RandomStringUtils.randomAlphabetic(19))
            .lastName(RandomStringUtils.randomAlphabetic(19))
            .email(RandomStringUtils.randomAlphabetic(19));
    }

    public static ProjectMarketOwnerDTO defaults() {
        return builder().build();
    }
}
