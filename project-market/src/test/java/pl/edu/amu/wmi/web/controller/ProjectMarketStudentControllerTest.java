package pl.edu.amu.wmi.web.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import pl.edu.amu.wmi.helper.ProjectMembersDTOHelper;
import pl.edu.amu.wmi.web.ProjectMarketFacade;
import pl.edu.amu.wmi.web.model.ProjectMembersDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static pl.edu.amu.wmi.web.controller.ControllerAssertionHelper.*;

@ExtendWith(MockitoExtension.class)
class ProjectMarketStudentControllerTest {

    @Mock
    private ProjectMarketFacade projectMarketFacade;

    @InjectMocks
    private ProjectMarketStudentController controller;

    @Test
    void shouldGetProjectMembers() {
        //given
        var projectMarketId = Long.parseLong(RandomStringUtils.randomNumeric(10));
        var projectMembers = ProjectMembersDTOHelper.projectMembersDefaults();

        when(projectMarketFacade.getProjectMembersByMarketId(projectMarketId)).thenReturn(projectMembers);

        //when
        ResponseEntity<ProjectMembersDTO> result = controller.getProjectMembers(projectMarketId);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var resultBody = result.getBody();
        assertThat(resultBody).isEqualTo(projectMembers);
    }

    @Test
    void shouldNotGetProjectMembersWhenExceptionIsThrown() {
        //given
        var projectMarketId = Long.parseLong(RandomStringUtils.randomNumeric(10));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).getProjectMembersByMarketId(projectMarketId);

        //expect
        assertThrows(()-> controller.getProjectMembers(projectMarketId), exceptionMessage);
    }
}
