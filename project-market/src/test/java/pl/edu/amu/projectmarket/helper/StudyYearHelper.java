package pl.edu.amu.projectmarket.helper;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import pl.edu.amu.wmi.entity.StudyYear;
import pl.edu.amu.wmi.enumerations.StudyType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyYearHelper {

    public static StudyYear createStudyYear() {
        StudyYear studyYear = new StudyYear();
        studyYear.setStudyYear(RandomStringUtils.randomNumeric(4));
        studyYear.setStudyType(StudyType.FULL_TIME);
        studyYear.setActive(true);
        studyYear.setId(Long.valueOf(RandomStringUtils.randomNumeric(4)));
        studyYear.setFirstSemesterCode(RandomStringUtils.randomNumeric(4));
        studyYear.setSecondSemesterCode(RandomStringUtils.randomNumeric(4));
        studyYear.setSubjectCode(RandomStringUtils.randomNumeric(4));
        studyYear.setSubjectType(RandomStringUtils.randomNumeric(4));
        studyYear.setCreationDate(LocalDateTime.now());
        studyYear.setModificationDate(LocalDateTime.now());
        studyYear.setYear(RandomStringUtils.randomNumeric(4));
        studyYear.setVersion(Long.valueOf(RandomStringUtils.randomNumeric(4)));

        return studyYear;
    }
}
