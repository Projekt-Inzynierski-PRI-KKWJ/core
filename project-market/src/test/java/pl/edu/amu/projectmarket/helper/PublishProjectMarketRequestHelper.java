package pl.edu.amu.projectmarket.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.projectmarket.model.PublishProjectMarketRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PublishProjectMarketRequestHelper {

    public static PublishProjectMarketRequest.PublishProjectMarketRequestBuilder builder() {
        return PublishProjectMarketRequest.builder()
            .project(ProjectHelper.createDefaultProject())
            .contactData(RandomStringUtils.randomAlphanumeric(10))
            .maxMembers(Integer.parseInt(RandomStringUtils.randomNumeric(3)));
    }

    public static PublishProjectMarketRequest defaults() {
        return builder().build();
    }
}
