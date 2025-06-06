package pl.edu.amu.wmi.model;

import pl.edu.amu.wmi.model.user.StudentDTO;

public record EmailNotificationDataDTO(String name, String email, String content) {

    public static EmailNotificationDataDTO fromStudent(StudentDTO student, String content) {
        return new EmailNotificationDataDTO(student.getName(), student.getEmail(), content);
    }
}
