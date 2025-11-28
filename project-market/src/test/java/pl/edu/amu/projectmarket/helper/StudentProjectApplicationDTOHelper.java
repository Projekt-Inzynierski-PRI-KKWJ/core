package pl.edu.amu.projectmarket.helper;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;
import pl.edu.amu.projectmarket.web.model.StudentProjectApplicationDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudentProjectApplicationDTOHelper {

    public static StudentProjectApplicationDTO.StudentProjectApplicationDTOBuilder builder() {
        return StudentProjectApplicationDTO.builder()
            .id(Long.parseLong(RandomStringUtils.randomNumeric(4)))
            .status(ProjectApplicationStatus.PENDING.name())
            .applicationDate(LocalDateTime.now())
            .decisionDate(LocalDateTime.now());
    }

    public static StudentProjectApplicationDTO defaults() {
        return builder().build();
    }
}
