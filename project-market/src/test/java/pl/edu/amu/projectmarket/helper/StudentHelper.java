package pl.edu.amu.projectmarket.helper;

import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.enumerations.ProjectRole;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudentHelper {

    public static Student createDefaultStudent() {
        return createDefaultStudent(RandomStringUtils.randomAlphanumeric(5));
    }

    public static Student createDefaultStudent(String indexNumber) {
        Student student = new Student();
        student.setId(1L);

        student.setUserData(UserDataHelper.defaults(indexNumber));
        student.setPesel(RandomStringUtils.randomNumeric(11));

        ProjectRole[] roles = ProjectRole.values();
        student.setProjectRole(roles[ThreadLocalRandom.current().nextInt(roles.length)]);

        student.setProjectAdmin(ThreadLocalRandom.current().nextBoolean());
        student.setProjectConfirmed(ThreadLocalRandom.current().nextBoolean());

        student.setStudyYear(String.valueOf(ThreadLocalRandom.current().nextInt(1, 6)));

        return student;
    }
}
