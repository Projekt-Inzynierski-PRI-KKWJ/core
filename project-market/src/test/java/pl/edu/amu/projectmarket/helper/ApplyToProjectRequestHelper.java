package pl.edu.amu.projectmarket.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.projectmarket.model.ApplyToProjectRequest;
import pl.edu.amu.projectmarket.web.model.ApplyToProjectRequestDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplyToProjectRequestHelper {

    public static ApplyToProjectRequest.ApplyToProjectRequestBuilder builder() {
        return ApplyToProjectRequest.builder()
            .projectMarket(ProjectMarketHelper.defaults())
            .student(StudentHelper.defaults())
            .contactData(RandomStringUtils.randomAlphanumeric(10))
            .otherInformation(RandomStringUtils.randomAlphanumeric(10))
            .skills(RandomStringUtils.randomAlphanumeric(10));
    }

    public static ApplyToProjectRequest defaults() {
        return builder().build();
    }

    public static ApplyToProjectRequestDTO.ApplyToProjectRequestDTOBuilder builderDTO() {
        return ApplyToProjectRequestDTO.builder()
            .skills(RandomStringUtils.randomAlphabetic(10))
            .otherInformation(RandomStringUtils.randomAlphabetic(10))
            .contactData(RandomStringUtils.randomAlphabetic(10));
    }

    public static ApplyToProjectRequestDTO defaultsDTO() {
        return builderDTO().build();
    }
}
