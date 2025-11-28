package pl.edu.amu.wmi.web.controller;

import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import pl.edu.amu.wmi.exception.BusinessException;
import pl.edu.amu.wmi.helper.ApplyToProjectRequestDTOHelper;
import pl.edu.amu.wmi.helper.ProjectApplicationHelper;
import pl.edu.amu.wmi.helper.StudentProjectApplicationDTOHelper;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.web.model.ProjectApplicationDTO;
import pl.edu.amu.wmi.web.model.StudentProjectApplicationDTO;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static pl.edu.amu.wmi.web.controller.ControllerAssertionHelper.*;

@ExtendWith(MockitoExtension.class)
class ProjectMarketApplicationControllerTest {

    @Mock
    private ProjectMarketFacade projectMarketFacade;

    @InjectMocks
    private ProjectMarketApplicationController controller;

    @Test
    void shouldApplyToProject() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(5));
        var request = ApplyToProjectRequestDTOHelper.defaults();

        //when
        ResponseEntity<Void> result = controller.applyToProject(marketId, request);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        verify(projectMarketFacade, times(1)).applyToProject(marketId, request);
    }

    @Test
    void shouldNotApplyToProjectWhenExceptionThrows() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(5));
        var request = ApplyToProjectRequestDTOHelper.defaults();
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).applyToProject(marketId, request);

        //expect
        assertThrows(() -> controller.applyToProject(marketId, request), exceptionMessage);
    }

    @Test
    void shouldGetApplicationsForOwner() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(5));
        var projectApplication = ProjectApplicationHelper.defaultDTO();
        var projectApplication2 = ProjectApplicationHelper.defaultDTO();
        when(projectMarketFacade.getProjectApplicationByMarketIdInPendingStatus(marketId)).thenReturn(
            List.of(projectApplication, projectApplication2));

        //when
        ResponseEntity<List<ProjectApplicationDTO>> result = controller.getApplicationsForOwner(marketId);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var resultBody = result.getBody();
        assertThat(resultBody).hasSize(2);
        assertThat(resultBody.get(0)).isEqualTo(projectApplication);
        assertThat(resultBody.get(1)).isEqualTo(projectApplication2);
    }

    @Test
    void shouldNotGetApplicationsForOwnerWhenExceptionThrows() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(5));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).getProjectApplicationByMarketIdInPendingStatus(marketId);

        //expect
        assertThrows(() -> controller.getApplicationsForOwner(marketId), exceptionMessage);
    }

    @Test
    void shouldApproveApplication() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(5));

        //when
        ResponseEntity<Void> result = controller.approveApplication(applicationId);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        verify(projectMarketFacade, times(1)).approveCandidate(applicationId);
    }

    @Test
    void shouldNotApproveApplicationWhenExceptionThrows() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(5));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).approveCandidate(applicationId);

        //expect
        assertThrows(() -> controller.approveApplication(applicationId), exceptionMessage);
    }

    @Test
    void shouldRejectApplication() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(5));

        //when
        ResponseEntity<Void> result = controller.rejectApplication(applicationId);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        verify(projectMarketFacade, times(1)).rejectCandidate(applicationId);
    }

    @Test
    void shouldNotRejectApplicationWhenExceptionThrows() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(5));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).rejectCandidate(applicationId);

        //expect
        assertThrows(() -> controller.rejectApplication(applicationId), exceptionMessage);
    }

    @Test
    void shouldGetApplicationsForStudent() {
        //given
        var studentProjectApplication = StudentProjectApplicationDTOHelper.defaults();
        var studentProjectApplication2 = StudentProjectApplicationDTOHelper.defaults();
        when(projectMarketFacade.getApplicationsForStudent()).thenReturn(List.of(studentProjectApplication, studentProjectApplication2));

        //when
        ResponseEntity<List<StudentProjectApplicationDTO>> result = controller.getApplicationsForStudent();

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var resultBody = result.getBody();
        assertThat(resultBody).hasSize(2);
        assertThat(resultBody.get(0)).isEqualTo(studentProjectApplication);
        assertThat(resultBody.get(1)).isEqualTo(studentProjectApplication2);
    }

    @Test
    void shouldNotGetApplicationsForStudentWhenExceptionThrows() {
        //given
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).getApplicationsForStudent();

        //expect
        assertThrows(() -> controller.getApplicationsForStudent(), exceptionMessage);
    }
}
