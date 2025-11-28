package pl.edu.amu.wmi.helper;

import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.web.model.ProjectMarketSupervisorDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SupervisorHelper {

    public static Supervisor defaults() {
        return defaults(RandomStringUtils.randomAlphanumeric(4));
    }

    public static Supervisor defaults(String studyYear) {
        Supervisor supervisor = new Supervisor();
        supervisor.setId(Long.parseLong(RandomStringUtils.randomNumeric(4)));
        supervisor.setUserData(UserDataHelper.defaults());

        Set<Project> projects = new HashSet<>();
        projects.add(ProjectHelper.createDefaultProject());
        supervisor.setProjects(projects);

        supervisor.setMaxNumberOfProjects(10);
        supervisor.setStudyYear(studyYear);

        return supervisor;
    }

    public static ProjectMarketSupervisorDTO.ProjectMarketSupervisorDTOBuilder builderDTO() {
        return ProjectMarketSupervisorDTO.builder()
            .firstName(RandomStringUtils.randomAlphanumeric(4))
            .lastName(RandomStringUtils.randomAlphanumeric(4))
            .id(Long.parseLong(RandomStringUtils.randomNumeric(4)));
    }

    public static ProjectMarketSupervisorDTO defaultsDTO() {
        return builderDTO().build();
    }
}
