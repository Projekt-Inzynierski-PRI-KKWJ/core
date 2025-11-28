package pl.edu.amu.projectmarket.web.controller;

import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import pl.edu.amu.projectmarket.helper.ProjectMarketHelper;
import pl.edu.amu.projectmarket.helper.SupervisorHelper;
import pl.edu.amu.projectmarket.web.ProjectMarketFacade;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketSupervisorDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.amu.projectmarket.helper.PaginationHelper.createPage;
import static pl.edu.amu.projectmarket.web.controller.ControllerAssertionHelper.*;

@ExtendWith(MockitoExtension.class)
class ProjectMarketSupervisorControllerTest {

    @Mock
    private ProjectMarketFacade projectMarketFacade;

    @InjectMocks
    private ProjectMarketSupervisorController controller;

    @Test
    void closeProjectMarketByOwner() {
        //given
        var studyYear = RandomStringUtils.randomAlphabetic(10);
        var supervisor1 = SupervisorHelper.defaultsDTO();
        var supervisor2 = SupervisorHelper.defaultsDTO();

        when(projectMarketFacade.getSupervisors(studyYear)).thenReturn(List.of(supervisor1, supervisor2));

        //when
        ResponseEntity<List<ProjectMarketSupervisorDTO>> result = controller.getSupervisors(studyYear);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var resultBody = result.getBody();
        assertThat(resultBody).hasSize(2);
        assertThat(resultBody.get(0)).isEqualTo(supervisor1);
        assertThat(resultBody.get(1)).isEqualTo(supervisor2);
    }

    @Test
    void shouldNotGetSupervisorsWhenExceptionIsThrown() {
        //given
        var studyYear = RandomStringUtils.randomAlphabetic(10);
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).getSupervisors(studyYear);

        //when
        ResponseEntity<List<ProjectMarketSupervisorDTO>> result = controller.getSupervisors(studyYear);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).hasSize(0);
    }

    @Test
    void shouldGetProjectMarketsForSupervisor() {
        Pageable pageable = PageRequest.of(0, 10);
        var projectMarket = ProjectMarketHelper.defaultsDTO();
        var projectMarket2 = ProjectMarketHelper.defaultsDTO();
        when(projectMarketFacade.getProjectMarketsForSupervisor(pageable)).thenReturn(createPage(projectMarket, projectMarket2));

        //when
        ResponseEntity<Page<ProjectMarketDTO>> result = controller.getProjectMarketsForSupervisor(pageable);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var resultBody = result.getBody();
        assertThat(resultBody).isNotNull();
        assertThat(resultBody.getTotalElements()).isEqualTo(2);
        assertThat(resultBody.getContent()).hasSize(2);
        var content = resultBody.getContent();
        assertThat(content.get(0)).isEqualTo(projectMarket);
        assertThat(content.get(1)).isEqualTo(projectMarket2);
    }

    @Test
    void shouldApproveProjectAndCloseMarket() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));

        //when
        var result = controller.approveProjectAndCloseMarket(marketId);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        verify(projectMarketFacade, times(1)).approveProjectAndCloseMarket(marketId);
    }

    @Test
    void shouldNotApproveProjectAndCloseMarketWhenExceptionIsThrown() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).approveProjectAndCloseMarket(marketId);

        //expect
        assertThrows(() -> controller.approveProjectAndCloseMarket(marketId), exceptionMessage);
    }

    @Test
    void shouldRejectProjectAndCloseMarket() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));

        //when
        var result = controller.rejectProjectAndCloseMarket(marketId);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        verify(projectMarketFacade, times(1)).rejectProjectAndCloseMarket(marketId);
    }

    @Test
    void shouldNotRejectProjectAndCloseMarketWhenExceptionIsThrown() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).rejectProjectAndCloseMarket(marketId);

        //expect
        assertThrows(() -> controller.rejectProjectAndCloseMarket(marketId), exceptionMessage);
    }


}
