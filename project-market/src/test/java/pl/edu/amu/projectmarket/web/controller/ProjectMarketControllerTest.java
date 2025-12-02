package pl.edu.amu.projectmarket.web.controller;

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
import pl.edu.amu.projectmarket.web.ProjectMarketFacade;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDetailsDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.amu.projectmarket.helper.PaginationHelper.*;
import static pl.edu.amu.projectmarket.web.controller.ControllerAssertionHelper.*;


@ExtendWith(MockitoExtension.class)
class ProjectMarketControllerTest {

    @Mock
    private ProjectMarketFacade projectMarketFacade;

    @InjectMocks
    private ProjectMarketController controller;

    @Test
    void shouldGetAllActiveProjectMarkets() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        var projectMarket = ProjectMarketHelper.defaultsDTO();
        var projectMarket2 = ProjectMarketHelper.defaultsDTO();
        when(projectMarketFacade.getAllActiveProjectMarkets(pageable)).thenReturn(createPage(projectMarket, projectMarket2));

        //when
        ResponseEntity<Page<ProjectMarketDTO>> result = controller.getAllActiveProjectMarkets(pageable);

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
    void shouldNotGetAllActiveProjectMarketsWhenExceptionThrows() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).getAllActiveProjectMarkets(pageable);

        //when
        ResponseEntity<Page<ProjectMarketDTO>> result = controller.getAllActiveProjectMarkets(pageable);
        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var resultBody = result.getBody();
        assertThat(resultBody).isEmpty();
    }

    @Test
    void shouldGetProjectMarketById() {
        //given
        var projectMarketId = Long.parseLong(RandomStringUtils.randomNumeric(8));
        var projectMarketDetails = ProjectMarketHelper.detailsDefaultsDTO();

        when(projectMarketFacade.getMarketDetailsById(projectMarketId)).thenReturn(projectMarketDetails);

        //when
        ResponseEntity<ProjectMarketDetailsDTO> result = controller.getProjectMarketById(projectMarketId);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var resultBody = result.getBody();
        assertThat(resultBody).isEqualTo(projectMarketDetails);
    }

    @Test
    void shouldNotGetProjectMarketByIdWhenExceptionThrows() {
        //given
        var projectMarketId = Long.parseLong(RandomStringUtils.randomNumeric(8));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).getMarketDetailsById(projectMarketId);

        //when
        assertThrows(() -> controller.getProjectMarketById(projectMarketId), exceptionMessage);
    }

    @Test
    void shouldSearchByName() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        var name = RandomStringUtils.randomAlphabetic(10);
        var projectMarket = ProjectMarketHelper.defaultsDTO();
        var projectMarket2 = ProjectMarketHelper.defaultsDTO();

        when(projectMarketFacade.searchProjectMarketsByNamePattern(name, pageable)).thenReturn(createPage(projectMarket, projectMarket2));

        //when
        ResponseEntity<Page<ProjectMarketDTO>> result = controller.searchByName(name, pageable);

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
    void shouldNotSearchByNameWhenExceptionThrows() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        var name = RandomStringUtils.randomAlphabetic(10);
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).searchProjectMarketsByNamePattern(name, pageable);

        //when
        ResponseEntity<Page<ProjectMarketDTO>> result = controller.searchByName(name, pageable);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var resultBody = result.getBody();
        assertThat(resultBody).isEmpty();
    }

    @Test
    void shouldSubmitProjectToSupervisor() {
        //given
        Long projectMarketId = Long.parseLong(RandomStringUtils.randomNumeric(8));
        Long supervisorId = Long.parseLong(RandomStringUtils.randomNumeric(8));

        //when
        var result = controller.submitProjectMarketToSupervisor(projectMarketId, supervisorId);

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        verify(projectMarketFacade, times(1)).submitProjectMarketToSupervisor(projectMarketId, supervisorId);
    }

    @Test
    void shouldNotSubmitProjectToSupervisorWhenExceptionThrows() {
        //given
        var projectMarketId = Long.parseLong(RandomStringUtils.randomNumeric(8));
        var supervisorId = Long.parseLong(RandomStringUtils.randomNumeric(8));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).submitProjectMarketToSupervisor(projectMarketId, supervisorId);

        //expect
        assertThrows(() -> controller.submitProjectMarketToSupervisor(projectMarketId, supervisorId), exceptionMessage);
    }

    @Test
    void shouldCloseProjectMarketByOwner() {
        //given
        var projectMarketId = Long.parseLong(RandomStringUtils.randomNumeric(8));

        //when
        var response = controller.closeProjectMarketByOwner(projectMarketId);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        verify(projectMarketFacade, times(1)).closeProjectMarketByOwner(projectMarketId);
    }

    @Test
    void shouldNotCloseProjectMarketByOwnerWhenExceptionThrows() {
        //given
        var projectMarketId = Long.parseLong(RandomStringUtils.randomNumeric(8));
        var exceptionMessage = "exceptionMessage";
        var exception = new IllegalStateException(exceptionMessage);
        doThrow(exception).when(projectMarketFacade).closeProjectMarketByOwner(projectMarketId);

        //expect
        assertThrows(() -> controller.closeProjectMarketByOwner(projectMarketId), exceptionMessage);
    }
}
