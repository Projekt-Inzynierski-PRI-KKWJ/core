package pl.edu.amu.projectmarket.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.amu.projectmarket.helper.ProjectCreateRequestHelper;
import pl.edu.amu.projectmarket.helper.ProjectHelper;
import pl.edu.amu.projectmarket.mapper.ProjectEntityMapper;
import pl.edu.amu.projectmarket.mapper.ProjectEntityMapperImpl;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.enumerations.EvaluationPhase;
import pl.edu.amu.wmi.service.grade.EvaluationCardService;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectDAO projectDAO;

    @Mock
    private EvaluationCardService evaluationCardService;
    private final ProjectEntityMapper projectEntityMapper = new ProjectEntityMapperImpl();

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectDAO, evaluationCardService, projectEntityMapper);
    }

    @Test
    void shouldCreateProject() {
        //given
        var request = ProjectCreateRequestHelper.defaults();
        var projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);
        var project = ProjectHelper.createDefaultProject();
        when(projectDAO.save(projectArgumentCaptor.capture())).thenReturn(project);

        //when
        Project result = projectService.createProject(request);

        //then
        verify(evaluationCardService, times(2)).createEvaluationCard(any(), any(),
            any(), eq(EvaluationPhase.SEMESTER_PHASE), any(), anyBoolean());
        assertThat(result)
            .returns(project.getName(), Project::getName)
            .returns(project.getTechnologies(), Project::getTechnologies)
            .returns(project.getDescription(), Project::getDescription)
            .returns(project.getStudyYear(), Project::getStudyYear);

        assertThat(result.getEvaluationCards()).hasSize(2);
    }
}
