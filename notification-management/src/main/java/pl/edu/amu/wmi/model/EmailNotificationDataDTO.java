package pl.edu.amu.wmi.model;

import java.util.ArrayList;
import java.util.List;
import pl.edu.amu.wmi.model.user.StudentDTO;

public record EmailNotificationDataDTO(List<EmailNotificationStudentDataDTO> students, String content) {

    public static EmailNotificationDataDTO fromStudent(List<StudentDTO> students, String content) {
        List<EmailNotificationStudentDataDTO> studentsData = new ArrayList<>();
        students.forEach(student -> studentsData
            .add(new EmailNotificationStudentDataDTO(student.getName(), student.getEmail())));
        return new EmailNotificationDataDTO(studentsData, content);
    }
}
