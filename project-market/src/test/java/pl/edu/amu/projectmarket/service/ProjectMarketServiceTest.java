package pl.edu.amu.projectmarket.service;

import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.edu.amu.projectmarket.helper.ProjectMarketHelper;
import pl.edu.amu.projectmarket.helper.PublishProjectMarketRequestHelper;
import pl.edu.amu.projectmarket.helper.SupervisorHelper;
import pl.edu.amu.projectmarket.mapper.ProjectMarketEntityMapper;
import pl.edu.amu.projectmarket.mapper.ProjectMarketEntityMapperImpl;
import pl.edu.amu.wmi.dao.ProjectMarketDAO;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.amu.projectmarket.helper.PaginationHelper.*;


@ExtendWith(MockitoExtension.class)
class ProjectMarketServiceTest {

    @Mock
    private ProjectMarketDAO projectMarketDAO;
    private final ProjectMarketEntityMapper projectMarketEntityMapper = new ProjectMarketEntityMapperImpl();

    private ProjectMarketService projectMarketService;

    @BeforeEach
    void setUp() {
        projectMarketService = new ProjectMarketService(projectMarketDAO, projectMarketEntityMapper);
    }

    @Test
    void shouldPublishMarket() {
        //given
        var request = PublishProjectMarketRequestHelper.defaults();
        var captor = ArgumentCaptor.forClass(ProjectMarket.class);
        var projectMarket = ProjectMarketHelper.defaults();
        when(projectMarketDAO.save(captor.capture())).thenReturn(projectMarket);

        //when
        projectMarketService.publishMarket(request);

        //then
        var result = captor.getValue();
        assertThat(result)
            .returns(request.getMaxMembers(), ProjectMarket::getMaxMembers)
            .returns(request.getContactData(), ProjectMarket::getContactData)
            .returns(request.getProject(), ProjectMarket::getProject);
    }

    @Test
    void shouldListActiveMarkets() {
        //given
        var pageable = PageRequest.of(0, 10);
        var projectMarket = ProjectMarketHelper.defaults();
        var projectMarket2 = ProjectMarketHelper.defaults();
        var projectMarket3 = ProjectMarketHelper.defaults();
        when(projectMarketDAO.findByStatus(ProjectMarketStatus.ACTIVE, pageable)).thenReturn(
            createPage(projectMarket, projectMarket2, projectMarket3));

        //when
        Page<ProjectMarket> result = projectMarketService.listActiveMarkets(pageable);

        //then
        assertThat(result).hasSize(3)
            .containsExactly(projectMarket, projectMarket2, projectMarket3);
    }

    @Test
    void shouldSearchActiveMarketsByNamePattern() {
        //given
        var pageable = PageRequest.of(0, 10);
        var name = RandomStringUtils.randomAlphabetic(10);
        var projectMarket = ProjectMarketHelper.defaults();
        var projectMarket2 = ProjectMarketHelper.defaults();
        when(projectMarketDAO.findByProject_NameContainingIgnoreCaseAndStatus(name, ProjectMarketStatus.ACTIVE, pageable)).thenReturn(
            createPage(projectMarket, projectMarket2));

        //when
        Page<ProjectMarket> result = projectMarketService.searchActiveMarketsByNamePattern(name, pageable);

        //then
        assertThat(result).hasSize(2)
            .containsExactly(projectMarket, projectMarket2);
    }

    @Test
    void shouldSave() {
        //given
        var projectMarket = ProjectMarketHelper.defaults();

        //when
        projectMarketService.save(projectMarket);

        //then
        verify(projectMarketDAO, times(1)).save(projectMarket);
    }

    @Test
    void shouldGetProjectMarketById() {
        //given
        var id = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectMarket = ProjectMarketHelper.defaults();
        when(projectMarketDAO.getReferenceById(id)).thenReturn(projectMarket);

        //when
        var result = projectMarketService.getProjectMarketById(id);

        //then
        assertThat(result).isEqualTo(projectMarket);
    }

    @Test
    void shouldFindByAssignedSupervisor() {
        //given
        var supervisor = SupervisorHelper.defaults();
        var pageable = PageRequest.of(0, 10);
        var projectMarket = ProjectMarketHelper.defaults();
        var projectMarket2 = ProjectMarketHelper.defaults();
        when(projectMarketDAO.findByProject_Supervisor(supervisor.getId(), pageable)).thenReturn(createPage(projectMarket, projectMarket2));

        //when
        var result = projectMarketService.findByAssignedSupervisor(supervisor, pageable);

        //then
        assertThat(result).hasSize(2)
            .containsExactly(projectMarket, projectMarket2);
    }
}
