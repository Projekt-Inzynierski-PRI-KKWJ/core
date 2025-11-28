package pl.edu.amu.projectmarket.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import pl.edu.amu.projectmarket.helper.ProjectCreateRequestDTOHelper;
import pl.edu.amu.projectmarket.web.ProjectMarketFacade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static pl.edu.amu.projectmarket.web.controller.ControllerAssertionHelper.*;

@ExtendWith(MockitoExtension.class)
class ProjectMarketProjectControllerTest {

    @Mock
    private ProjectMarketFacade projectMarketFacade;

    @InjectMocks
    private ProjectMarketProjectController controller;

    @Test
    void shouldCreateProjectAndPublishOnMarket() {
        //given
        var request = ProjectCreateRequestDTOHelper.defaults();

        //when
        var result = controller.createProjectAndPublishOnMarket(request);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        verify(projectMarketFacade, times(1)).createMarket(request);
    }

    @Test
    void shouldNotCreateProjectAndPublishOnMarketWhenExceptionIsThrown() {
        //given
        var request = ProjectCreateRequestDTOHelper.defaults();
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).createMarket(request);

        //expect
        assertThrows(()-> controller.createProjectAndPublishOnMarket(request), exceptionMessage);
    }
}
