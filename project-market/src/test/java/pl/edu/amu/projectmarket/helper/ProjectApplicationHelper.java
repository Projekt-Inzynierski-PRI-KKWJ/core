package pl.edu.amu.projectmarket.helper;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;
import pl.edu.amu.projectmarket.web.model.ProjectApplicationDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectApplicationHelper {

    public static ProjectApplication defaults() {
        ProjectApplication projectApplication = new ProjectApplication();
        projectApplication.setProjectMarket(ProjectMarketHelper.defaults());
        projectApplication.setStudent(StudentHelper.defaults());
        projectApplication.setStatus(ProjectApplicationStatus.PENDING);
        projectApplication.setContactData(RandomStringUtils.randomAlphanumeric(20));
        projectApplication.setSkills(RandomStringUtils.randomAlphanumeric(20));
        projectApplication.setOtherInformation(RandomStringUtils.randomAlphanumeric(20));

        return projectApplication;
    }

    public static ProjectApplicationDTO.ProjectApplicationDTOBuilder builderDTO() {
        return ProjectApplicationDTO.builder()
            .id(Long.valueOf(RandomStringUtils.randomNumeric(5)))
            .firstName(RandomStringUtils.randomAlphanumeric(20))
            .lastName(RandomStringUtils.randomAlphanumeric(20))
            .contactData(RandomStringUtils.randomAlphanumeric(20))
            .skills(RandomStringUtils.randomAlphanumeric(20))
            .otherInformation(RandomStringUtils.randomAlphanumeric(20))
            .status(ProjectApplicationStatus.PENDING.name())
            .applicationDate(LocalDateTime.now())
            .decisionDate(LocalDateTime.now());
    }

    public static ProjectApplicationDTO defaultDTO() {
       return builderDTO().build();
    }
}
