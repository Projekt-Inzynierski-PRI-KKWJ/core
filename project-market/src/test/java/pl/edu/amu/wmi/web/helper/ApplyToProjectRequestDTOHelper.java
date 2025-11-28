package pl.edu.amu.wmi.web.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.web.model.ApplyToProjectRequestDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplyToProjectRequestDTOHelper {

    public static ApplyToProjectRequestDTO.ApplyToProjectRequestDTOBuilder builder() {
        return ApplyToProjectRequestDTO.builder()
            .skills(RandomStringUtils.randomAlphabetic(10))
            .otherInformation(RandomStringUtils.randomAlphabetic(10))
            .contactData(RandomStringUtils.randomAlphabetic(10));
    }

    public static ApplyToProjectRequestDTO defaults() {
        return builder().build();
    }
}
