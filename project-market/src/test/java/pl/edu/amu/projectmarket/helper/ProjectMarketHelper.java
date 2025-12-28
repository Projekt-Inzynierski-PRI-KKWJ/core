package pl.edu.amu.projectmarket.helper;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDetailsDTO;

import static pl.edu.amu.projectmarket.helper.ProjectHelper.createDefaultProject;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectMarketHelper {

    public static ProjectMarket defaults() {
        return createMarket(ProjectHelper::createDefaultProject);
    }

    public static ProjectMarket defaults(String indexNumber) {
        return createMarket(() -> createDefaultProject(indexNumber));
    }

    public static ProjectMarketDTO.ProjectMarketDTOBuilder builder() {
        return ProjectMarketDTO.builder()
            .id(Long.parseLong(RandomStringUtils.randomNumeric(8)))
            .projectName(RandomStringUtils.randomAlphanumeric(8))
            .projectDescription(RandomStringUtils.randomAlphanumeric(8))
            .availableSlots(RandomStringUtils.randomAlphanumeric(8))
            .ownerDetails(ProjectMarketOwnerDTOHelper.defaults())
            .studyYear(RandomStringUtils.randomNumeric(4));
    }

    public static ProjectMarketDTO defaultsDTO() {
        return builder().build();
    }

    public static ProjectMarketDetailsDTO.ProjectMarketDetailsDTOBuilder detailsBuilder() {
        return ProjectMarketDetailsDTO.builder()
            .id(Long.parseLong(RandomStringUtils.randomNumeric(8)))
            .projectName(RandomStringUtils.randomAlphanumeric(8))
            .projectDescription(RandomStringUtils.randomAlphanumeric(8))
            .technologies(Set.of(RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(8)))
            .ownerDetails(ProjectMarketOwnerDTOHelper.defaults())
            .maxMembers(Integer.parseInt(RandomStringUtils.randomNumeric(3)))
            .contactData(RandomStringUtils.randomAlphanumeric(8))
            .currentMembers(List.of(ProjectMarketUserDataDTOHelper.defaults()));
    }

    public static ProjectMarketDetailsDTO detailsDefaultsDTO() {
        return detailsBuilder().build();
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
