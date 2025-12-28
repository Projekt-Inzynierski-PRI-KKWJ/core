package pl.edu.amu.projectmarket.helper;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.projectmarket.model.ProjectCreateRequest;
import pl.edu.amu.projectmarket.web.model.ProjectCreateRequestDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectCreateRequestHelper {

    public static ProjectCreateRequest defaults() {
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName(RandomStringUtils.randomAlphanumeric(20));
        request.setDescription(RandomStringUtils.randomAlphanumeric(20));
        request.setStudent(StudentHelper.defaults());
        request.setTechnologies(List.of(RandomStringUtils.randomAlphanumeric(20)));
        request.setContactData(RandomStringUtils.randomAlphanumeric(20));
        request.setMaxMembers(Integer.parseInt(RandomStringUtils.randomNumeric(2)));
        request.setStudyYear(StudyYearHelper.createStudyYear());
        return request;
    }

    public static ProjectCreateRequestDTO.ProjectCreateRequestDTOBuilder builderDTO() {
        return ProjectCreateRequestDTO.builder()
            .name(RandomStringUtils.randomAlphabetic(10))
            .description(RandomStringUtils.randomAlphabetic(10))
            .contactData(RandomStringUtils.randomAlphabetic(10))
            .maxMembers(Integer.parseInt(RandomStringUtils.randomNumeric(4)))
            .technologies(List.of(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)))
            .studyYear("FULL_TIME#2023");
    }

    public static ProjectCreateRequestDTO defaultsDTO() {
        return builderDTO().build();
    }
}
