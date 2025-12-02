package pl.edu.amu.wmi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.amu.wmi.entity.EvaluationCard;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.enumerations.EvaluationPhase;
import pl.edu.amu.wmi.enumerations.EvaluationStatus;
import pl.edu.amu.wmi.enumerations.Semester;
import pl.edu.amu.wmi.model.user.StudentCreationRequestDTO;
import pl.edu.amu.wmi.model.user.StudentDTO;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface StudentUserMapper {

    Student createEntity(StudentDTO dto);

    @Mapping(target = "name", expression = "java(String.format(\"%s %s\", entity.getUserData().getFirstName(), entity.getUserData().getLastName()))")
    @Mapping(target = "email", source = "userData.email")
    @Mapping(target = "indexNumber", source = "userData.indexNumber")
    // TODO: 6/19/2023 should we return a single role or a list ?
    @Mapping(target = "role", expression = "java(entity.getUserData().getRoles().iterator().next().getName().name())")
    @Mapping(target = "accepted", expression = "java(entity.isProjectConfirmed())")
    @Mapping(target = "confirmedProjectId", expression = "java(entity.getConfirmedProject() != null ? entity.getConfirmedProject().getId() : null)")
    @Mapping(target = "confirmedProjectName", expression = "java(entity.getConfirmedProject() != null ? entity.getConfirmedProject().getName() : null)")
    @Mapping(target = "assignedProjectIds", expression = "java(entity.getAssignedProjects().stream().map(sp -> sp.getProject().getId()).toList())")
    @Mapping(target = "assignedProjectNames", expression = "java(entity.getAssignedProjects().stream().map(sp -> sp.getProject().getName()).toList())")
    @Mapping(target = "firstSemesterGrade", expression = "java(getFirstSemesterGrade(entity))")
    @Mapping(target = "secondSemesterGrade", expression = "java(getSecondSemesterGrade(entity))")
    @Mapping(target = "finalGrade", expression = "java(getFinalGrade(entity))")
    @Mapping(target = "isApprovedForDefense", expression = "java(getApprovalStatus(entity))")
    StudentDTO mapToDto(Student entity);

    List<StudentDTO> mapToDtoList(List<Student> entityList);

    @Mapping(target = "userData.firstName", source = "dto.name")
    @Mapping(target = "userData.lastName", source = "dto.surname")
    @Mapping(target = "userData.email", source = "dto.email")
    @Mapping(target = "userData.indexNumber", source = "dto.indexNumber")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    Student createEntity(StudentCreationRequestDTO dto);

    // Helper methods for grade extraction
    default String getFirstSemesterGrade(Student entity) {
        if (entity.getConfirmedProject() == null) return null;
        return getGradeForSemester(entity.getConfirmedProject(), Semester.FIRST);
    }

    default String getSecondSemesterGrade(Student entity) {
        if (entity.getConfirmedProject() == null) return null;
        return getGradeForSemester(entity.getConfirmedProject(), Semester.SECOND);
    }

    default Double getFinalGrade(Student entity) {
        if (entity.getConfirmedProject() == null) return null;
        Optional<EvaluationCard> mostRecentCard = findMostRecentEvaluationCard(entity.getConfirmedProject(), null);
        return mostRecentCard.map(EvaluationCard::getFinalGrade).orElse(null);
    }

    default Boolean getApprovalStatus(Student entity) {
        if (entity.getConfirmedProject() == null) return null;
        Optional<EvaluationCard> mostRecentCard = findMostRecentEvaluationCard(entity.getConfirmedProject(), null);
        return mostRecentCard.map(EvaluationCard::isApprovedForDefense).orElse(null);
    }

    default String getGradeForSemester(Project project, Semester semester) {
        Optional<EvaluationCard> evaluationCard = findMostRecentEvaluationCard(project, semester);
        return evaluationCard.map(card -> pointsToOverallPercent(card.getTotalPoints())).orElse(null);
    }

    default Optional<EvaluationCard> findMostRecentEvaluationCard(Project project, Semester semester) {
        if (project == null || project.getEvaluationCards() == null) return Optional.empty();
        
        List<EvaluationCard> evaluationCards = project.getEvaluationCards();
        
        if (semester == null) {
            // Return the most recent active evaluation card
            return evaluationCards.stream()
                    .filter(EvaluationCard::isActive)
                    .findFirst();
        } else {
            // Find evaluation card for specific semester (priority: ACTIVE > RETAKE > DEFENSE)
            return evaluationCards.stream()
                    .filter(card -> Objects.equals(semester, card.getSemester()))
                    .filter(card -> Objects.equals(EvaluationStatus.ACTIVE, card.getEvaluationStatus()))
                    .findFirst()
                    .or(() -> evaluationCards.stream()
                            .filter(card -> Objects.equals(semester, card.getSemester()))
                            .filter(card -> Objects.equals(EvaluationPhase.RETAKE_PHASE, card.getEvaluationPhase()))
                            .findFirst()
                            .or(() -> evaluationCards.stream()
                                    .filter(card -> Objects.equals(semester, card.getSemester()))
                                    .filter(card -> Objects.equals(EvaluationPhase.DEFENSE_PHASE, card.getEvaluationPhase()))
                                    .findFirst()));
        }
    }

    default String pointsToOverallPercent(Double points) {
        if (points == null) return null;
        Double pointsOverall = points * 100 / 4;
        return String.format("%.2f", pointsOverall) + "%";
    }
}
