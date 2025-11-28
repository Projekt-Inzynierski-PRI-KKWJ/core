package pl.edu.amu.projectmarket.helper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.EvaluationCard;
import pl.edu.amu.wmi.entity.ExternalLink;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.StudentProject;
import pl.edu.amu.wmi.entity.StudyYear;
import pl.edu.amu.wmi.enumerations.AcceptanceStatus;
import pl.edu.amu.wmi.enumerations.EvaluationPhase;
import pl.edu.amu.wmi.enumerations.EvaluationStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectHelper {

    public static Project createDefaultProject() {
        return createDefaultProject(RandomStringUtils.randomAlphanumeric(5));
    }

    public static Project createDefaultProject(String indexNumber) {
        Project project = new Project();
        project.setName("Project-" + RandomStringUtils.randomAlphanumeric(8));
        project.setDescription(RandomStringUtils.randomAlphanumeric(100));

        Student student = StudentHelper.createDefaultStudent(indexNumber);
        project.setStudents(Set.of(student));
        student.setConfirmedProject(project);

        project.setTechnologies(Set.of(
            RandomStringUtils.randomAlphabetic(5),
            RandomStringUtils.randomAlphabetic(7),
            RandomStringUtils.randomAlphabetic(6)
        ));

        AcceptanceStatus[] statuses = AcceptanceStatus.values();
        project.setAcceptanceStatus(
            statuses[ThreadLocalRandom.current().nextInt(statuses.length)]
        );


        ExternalLink link = new ExternalLink();
        link.setUrl("https://example.com/" + RandomStringUtils.randomAlphanumeric(6));
        project.setExternalLinks(Set.of(link));

        StudyYear sy = new StudyYear();
        sy.setStudyYear(String.valueOf(ThreadLocalRandom.current().nextInt(1, 6)));
        project.setStudyYear(sy);

        EvaluationCard evaluationCard = new EvaluationCard();
        evaluationCard.setActive(true);
        evaluationCard.setProject(project);
        evaluationCard.setEvaluationStatus(EvaluationStatus.ACTIVE);
        evaluationCard.setEvaluationPhase(EvaluationPhase.SEMESTER_PHASE);
        project.setEvaluationCards(List.of(evaluationCard));

        StudentProject sp = new StudentProject(student, project);
        sp.setProjectAdmin(true);
        Set<StudentProject> studentProjects = new HashSet<>();
        studentProjects.add(sp);
        project.setAssignedStudents(studentProjects);

        return project;
    }
}
