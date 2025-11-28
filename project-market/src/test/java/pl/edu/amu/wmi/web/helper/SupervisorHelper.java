package pl.edu.amu.wmi.web.helper;

import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.Supervisor;

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
}
