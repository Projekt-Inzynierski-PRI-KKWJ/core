package pl.edu.amu.wmi.web.helper;

import java.util.List;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;

import static pl.edu.amu.wmi.web.helper.ProjectHelper.createDefaultProject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectMarketHelper {

    public static ProjectMarket defaults() {
        return createMarket(ProjectHelper::createDefaultProject);
    }

    public static ProjectMarket defaults(String indexNumber) {
        return createMarket(() -> createDefaultProject(indexNumber));
    }

    private static ProjectMarket createMarket(Supplier<Project> supplier) {
        var projectMarket = new ProjectMarket();
        projectMarket.setProject(supplier.get());
        projectMarket.setStatus(ProjectMarketStatus.ACTIVE);
        projectMarket.setContactData(RandomStringUtils.randomAlphanumeric(6));
        projectMarket.setMaxMembers(Integer.valueOf(RandomStringUtils.randomNumeric(6)));
        projectMarket.setApplications(List.of());
        return projectMarket;
    }
}
