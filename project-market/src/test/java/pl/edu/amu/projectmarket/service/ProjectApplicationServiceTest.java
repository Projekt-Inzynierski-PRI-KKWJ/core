package pl.edu.amu.projectmarket.service;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.amu.projectmarket.helper.ApplyToProjectRequestHelper;
import pl.edu.amu.projectmarket.helper.ProjectApplicationHelper;
import pl.edu.amu.projectmarket.helper.ProjectMarketHelper;
import pl.edu.amu.projectmarket.helper.StudentHelper;
import pl.edu.amu.projectmarket.mapper.ProjectApplicationEntityMapper;
import pl.edu.amu.projectmarket.mapper.ProjectApplicationEntityMapperImpl;
import pl.edu.amu.wmi.dao.ProjectApplicationDAO;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectApplicationServiceTest {

    @Mock
    private ProjectApplicationDAO projectApplicationDAO;
    private final ProjectApplicationEntityMapper projectApplicationEntityMapper = new ProjectApplicationEntityMapperImpl();

    private ProjectApplicationService projectApplicationService;

    @BeforeEach
    void setUp() {
        projectApplicationService = new ProjectApplicationService(projectApplicationDAO, projectApplicationEntityMapper);
    }

    @Test
    void shouldApplyToMarket() {
        //given
        var request = ApplyToProjectRequestHelper.defaults();
        when(
            projectApplicationDAO.existsByStudent_IdAndProjectMarket_Id(request.getStudent().getId(), request.getProjectMarket().getId())).thenReturn(
            false);

        //when
        projectApplicationService.applyToMarket(request);

        //then
        verify(projectApplicationDAO, times(1)).save(any());
    }

    @Test
    void shouldNotApplyToMarketWhenStudentAlreadyExistsInMarket() {
        //given
        var request = ApplyToProjectRequestHelper.defaults();
        when(
            projectApplicationDAO.existsByStudent_IdAndProjectMarket_Id(request.getStudent().getId(), request.getProjectMarket().getId())).thenReturn(
            true);

        //when
        assertThatThrownBy(() -> projectApplicationService.applyToMarket(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Student already applied to this project");

        //then
        verify(projectApplicationDAO, times(0)).save(any());
    }

    @Test
    void shouldSave() {
        //given
        var application = ProjectApplicationHelper.defaults();

        //when
        projectApplicationService.save(application);

        //then
        verify(projectApplicationDAO, times(1)).save(any());
    }

    @Test
    void shouldGetApplicationsForMarket() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(5));
        var status = ProjectApplicationStatus.PENDING;
        var application = ProjectApplicationHelper.defaults();
        var application2 = ProjectApplicationHelper.defaults();
        when(projectApplicationDAO.findByStatusAndProjectMarket_Id(status, marketId)).thenReturn(List.of(application, application2));

        //when
        var result = projectApplicationService.getApplicationsForMarket(status, marketId);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(application, application2);
    }

    @Test
    void shouldGetApplicationsForStudent() {
        //given
        var student = StudentHelper.defaults();
        var application = ProjectApplicationHelper.defaults();
        var application2 = ProjectApplicationHelper.defaults();
        when(projectApplicationDAO.findByStudent(student)).thenReturn(List.of(application, application2));

        //when
        var result = projectApplicationService.getApplicationsForStudent(student);

        //then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(application, application2);
    }

    @Test
    void shouldFindProjectApplicationById() {
        //given
        var id = Long.parseLong(RandomStringUtils.randomNumeric(5));
        var application = ProjectApplicationHelper.defaults();
        when(projectApplicationDAO.findById(id)).thenReturn(Optional.of(application));

        //when
        var result = projectApplicationService.findProjectApplicationById(id);

        //then
        assertThat(result).isPresent().get().isEqualTo(application);
    }

    @Test
    void shouldNotFindProjectApplicationByIdWhenNotFound() {
        //given
        var id = Long.parseLong(RandomStringUtils.randomNumeric(5));
        when(projectApplicationDAO.findById(id)).thenReturn(Optional.empty());

        //when
        var result = projectApplicationService.findProjectApplicationById(id);

        //then
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldExistsByStudentAndProjectMarket(boolean expected) {
        //given
        var student = StudentHelper.defaults();
        var market = ProjectMarketHelper.defaults();
        when(projectApplicationDAO.existsByStudentAndProjectMarket(student, market)).thenReturn(expected);

        //when
        boolean result = projectApplicationService.existsByStudentAndProjectMarket(student, market);

        //then
        assertThat(result).isEqualTo(expected);
    }
}
