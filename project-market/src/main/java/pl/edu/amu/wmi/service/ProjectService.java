package pl.edu.amu.wmi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.entity.EvaluationCard;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.enumerations.EvaluationPhase;
import pl.edu.amu.wmi.enumerations.EvaluationStatus;
import pl.edu.amu.wmi.enumerations.Semester;
import pl.edu.amu.wmi.mapper.ProjectEntityMapper;
import pl.edu.amu.wmi.model.ProjectCreateRequest;
import pl.edu.amu.wmi.service.grade.EvaluationCardService;

import static pl.edu.amu.wmi.enumerations.EvaluationStatus.INACTIVE;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectDAO projectDAO;
    private final EvaluationCardService evaluationCardService;
    private final ProjectEntityMapper projectEntityMapper;

    public Project createProject(ProjectCreateRequest request) {
        var project = projectDAO.save(projectEntityMapper.toEntity(request));
        addEvaluationCardToProject(project, request.getStudyYear().getStudyYear());
        return project;
    }

    private void addEvaluationCardToProject(Project project, String studyYear) {
        EvaluationCard evaluationCard = new EvaluationCard();
        project.addEvaluationCard(evaluationCard);
        evaluationCard.setProject(project);

        var semesters = Semester.values();
        for(Semester semester : semesters) {
            var isActive = Semester.FIRST.equals(semester);
            var evaluationStatus =  Semester.FIRST.equals(semester) ? EvaluationStatus.ACTIVE : INACTIVE;
            evaluationCardService.createEvaluationCard(project, studyYear,
                semester, EvaluationPhase.SEMESTER_PHASE, evaluationStatus, isActive);
        }
    }
}
