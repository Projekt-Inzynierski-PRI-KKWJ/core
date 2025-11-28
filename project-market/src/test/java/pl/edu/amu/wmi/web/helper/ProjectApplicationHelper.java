package pl.edu.amu.wmi.web.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectApplicationHelper {

    public static ProjectApplication defaults() {
        ProjectApplication projectApplication = new ProjectApplication();
        projectApplication.setProjectMarket(ProjectMarketHelper.defaults());
        projectApplication.setStudent(StudentHelper.createDefaultStudent());
        projectApplication.setStatus(ProjectApplicationStatus.PENDING);
        projectApplication.setContactData(RandomStringUtils.randomAlphanumeric(20));
        projectApplication.setSkills(RandomStringUtils.randomAlphanumeric(20));
        projectApplication.setOtherInformation(RandomStringUtils.randomAlphanumeric(20));

        return projectApplication;
    }
}
