package pl.edu.amu.projectmarket.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.dao.StudyYearDAO;
import pl.edu.amu.wmi.dao.SupervisorDAO;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.StudyYear;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;
import pl.edu.amu.projectmarket.model.ProjectCreateRequest;
import pl.edu.amu.projectmarket.model.PublishProjectMarketRequest;
import pl.edu.amu.projectmarket.service.ProjectApplicationService;
import pl.edu.amu.projectmarket.service.ProjectMarketService;
import pl.edu.amu.projectmarket.service.ProjectService;
import pl.edu.amu.projectmarket.helper.ApplyToProjectRequestHelper;
import pl.edu.amu.projectmarket.helper.ProjectApplicationHelper;
import pl.edu.amu.projectmarket.helper.ProjectCreateRequestHelper;
import pl.edu.amu.projectmarket.helper.ProjectMarketHelper;
import pl.edu.amu.projectmarket.helper.StudentHelper;
import pl.edu.amu.projectmarket.helper.SupervisorHelper;
import pl.edu.amu.projectmarket.web.mapper.ApplyToProjectRequestMapper;
import pl.edu.amu.projectmarket.web.mapper.ApplyToProjectRequestMapperImpl;
import pl.edu.amu.projectmarket.web.mapper.ProjectApplicationMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectApplicationMapperImpl;
import pl.edu.amu.projectmarket.web.mapper.ProjectMarketMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectMarketMapperImpl;
import pl.edu.amu.projectmarket.web.mapper.ProjectMarketSupervisorMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectMarketSupervisorMapperImpl;
import pl.edu.amu.projectmarket.web.mapper.ProjectMarketUserDataMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectMarketUserDataMapperImpl;
import pl.edu.amu.projectmarket.web.mapper.ProjectMemberMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectMemberMapperImpl;
import pl.edu.amu.projectmarket.web.mapper.ProjectRequestMapper;
import pl.edu.amu.projectmarket.web.mapper.ProjectRequestMapperImpl;
import pl.edu.amu.projectmarket.web.model.ProjectApplicationDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketDetailsDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketOwnerDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMarketSupervisorDTO;
import pl.edu.amu.projectmarket.web.model.ProjectMemberDTO;
import pl.edu.amu.projectmarket.web.model.StudentProjectApplicationDTO;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.edu.amu.projectmarket.helper.PaginationHelper.createPage;

@ExtendWith(MockitoExtension.class)
class ProjectMarketFacadeTest {

    private static final String INDEX_NUMBER = "123456";

    @Mock
    private ProjectApplicationService projectApplicationService;

    @Mock
    private ProjectMarketService projectMarketService;

    @Mock
    private ProjectService projectService;

    @Mock
    private SupervisorDAO supervisorDAO;

    @Mock
    private StudentDAO studentDAO;

    @Mock
    private StudyYearDAO studyYearDAO;

    private final ProjectRequestMapper projectRequestMapper = new ProjectRequestMapperImpl();
    private final ProjectMarketMapper projectMarketMapper = new ProjectMarketMapperImpl();
    private final ProjectMemberMapper projectMemberMapper = new ProjectMemberMapperImpl();
    private final ApplyToProjectRequestMapper applyToProjectRequestMapper = new ApplyToProjectRequestMapperImpl();
    private final ProjectApplicationMapper projectApplicationMapper = new ProjectApplicationMapperImpl();
    private final ProjectMarketSupervisorMapper projectMarketSupervisorMapper = new ProjectMarketSupervisorMapperImpl();
    private final ProjectMarketUserDataMapper projectMarketUserDataMapper = new ProjectMarketUserDataMapperImpl();

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;


    private ProjectMarketFacade projectMarketFacade;

    @BeforeEach
    public void setUp() {
        projectMarketMapper.setProjectMarketUserDataDTOMapper(projectMarketUserDataMapper);
        projectMarketFacade = new ProjectMarketFacade(projectApplicationService, projectMarketService,
            projectService, supervisorDAO, studentDAO, studyYearDAO, projectRequestMapper,
            projectMarketMapper, projectMemberMapper, applyToProjectRequestMapper,
            projectApplicationMapper, projectMarketSupervisorMapper);
    }

    @Test
    void shouldCreateProjectMarket() {
        //given
        getIndexNumberFromContext();
        var request = ProjectCreateRequestHelper.defaultsDTO();
        Student student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        StudyYear studyYear = mock(StudyYear.class);
        when(studyYearDAO.findByStudyYear(request.getStudyYear())).thenReturn(studyYear);

        Project project = mock(Project.class);
        ArgumentCaptor<ProjectCreateRequest> projectRequestCaptor = ArgumentCaptor.forClass(ProjectCreateRequest.class);
        when(projectService.createProject(projectRequestCaptor.capture())).thenReturn(project);

        ArgumentCaptor<PublishProjectMarketRequest> projectMarketCaptor = ArgumentCaptor.forClass(PublishProjectMarketRequest.class);
        doNothing().when(projectMarketService).publishMarket(projectMarketCaptor.capture());

        //when
        assertThatCode(() -> projectMarketFacade.createMarket(request))
            .doesNotThrowAnyException();

        //then
        verify(projectMarketService, times(1)).publishMarket(any());

        ProjectCreateRequest projectCreateRequest = projectRequestCaptor.getValue();
        assertThat(projectCreateRequest)
            .returns(request.getName(), ProjectCreateRequest::getName)
            .returns(request.getDescription(), ProjectCreateRequest::getDescription)
            .returns(request.getTechnologies(), ProjectCreateRequest::getTechnologies)
            .returns(studyYear, ProjectCreateRequest::getStudyYear)
            .returns(request.getMaxMembers(), ProjectCreateRequest::getMaxMembers)
            .returns(student, ProjectCreateRequest::getStudent)
            .returns(request.getContactData(), ProjectCreateRequest::getContactData);

        PublishProjectMarketRequest publishProjectMarketRequest = projectMarketCaptor.getValue();
        assertThat(publishProjectMarketRequest)
            .returns(project, PublishProjectMarketRequest::getProject)
            .returns(request.getMaxMembers(), PublishProjectMarketRequest::getMaxMembers)
            .returns(request.getContactData(), PublishProjectMarketRequest::getContactData);
    }

    @Test
    void shouldNotCreateProjectMarketWhenStudentNotFound() {
        getIndexNumberFromContext();
        var request = ProjectCreateRequestHelper.defaultsDTO();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(null);

        //when
        assertThatThrownBy(() -> projectMarketFacade.createMarket(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Student not found");

        //then
        verify(studyYearDAO, times(0)).findByStudyYear(INDEX_NUMBER);
        verify(projectMarketService, times(0)).publishMarket(any());
        verify(projectService, times(0)).createProject(any());
    }

    @Test
    void shouldGetAllActiveProjectMarkets() {
        //given
        var projectMarket = ProjectMarketHelper.defaults();
        var projectMarket2 = ProjectMarketHelper.defaults();
        var page = createPage(projectMarket, projectMarket2);

        Pageable pageable = PageRequest.of(0, 10);
        when(projectMarketService.listActiveMarkets(pageable)).thenReturn(page);

        //when
        Page<ProjectMarketDTO> result = projectMarketFacade.getAllActiveProjectMarkets(pageable);

        //then
        assertThat(result).hasSize(2)
            .map(ProjectMarketDTO::getProjectName)
            .containsExactlyInAnyOrder(projectMarket.getProject().getName(), projectMarket2.getProject().getName());
    }

    @Test
    void shouldGetMarketDetailsById() {
        //given
        var id = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectMarket = ProjectMarketHelper.defaults();
        projectMarket.setId(id);
        when(projectMarketService.getProjectMarketById(id)).thenReturn(projectMarket);

        //when
        ProjectMarketDetailsDTO result = projectMarketFacade.getMarketDetailsById(id);

        //then
        assertThat(result)
            .returns(id, ProjectMarketDetailsDTO::getId)
            .returns(projectMarket.getProject().getName(), ProjectMarketDetailsDTO::getProjectName)
            .returns(projectMarket.getProject().getDescription(), ProjectMarketDetailsDTO::getProjectDescription)
            .returns(projectMarket.getProject().getTechnologies(), ProjectMarketDetailsDTO::getTechnologies)
            .returns(projectMarket.getMaxMembers(), ProjectMarketDetailsDTO::getMaxMembers)
            .returns(projectMarket.getContactData(), ProjectMarketDetailsDTO::getContactData)
            .returns(projectMarket.getProject().getStudyYear().getStudyYear(), ProjectMarketDetailsDTO::getStudyYear);

        assertThat(result.getOwnerDetails())
            .returns(projectMarket.getLeader().getFirstName(), ProjectMarketOwnerDTO::getFirstName)
            .returns(projectMarket.getLeader().getLastName(), ProjectMarketOwnerDTO::getLastName)
            .returns(projectMarket.getLeader().getEmail(), ProjectMarketOwnerDTO::getEmail);

        assertThat(result.getCurrentMembers()).hasSize(1);
    }

    @Test
    void shouldSearchProjectMarketsByNamePattern() {
        //given
        var projectMarket = ProjectMarketHelper.defaults();
        var projectMarket2 = ProjectMarketHelper.defaults();
        var page = createPage(projectMarket, projectMarket2);
        Pageable pageable = PageRequest.of(0, 10);
        var name = RandomStringUtils.randomAlphanumeric(6);

        when(projectMarketService.searchActiveMarketsByNamePattern(name, pageable)).thenReturn(page);

        //when
        var result = projectMarketFacade.searchProjectMarketsByNamePattern(name, pageable);

        //then
        assertThat(result).hasSize(2);
        assertThat(result)
            .map(ProjectMarketDTO::getProjectName)
            .containsExactlyInAnyOrder(projectMarket.getProject().getName(), projectMarket2.getProject().getName());
    }

    @Test
    void shouldGetProjectMembersByMarketId() {
        //given
        Long marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectMarket = ProjectMarketHelper.defaults();
        projectMarket.setId(marketId);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        //when
        var result = projectMarketFacade.getProjectMembersByMarketId(marketId);

        //then
        assertThat(result.getMembers()).hasSize(1);
        assertThat(result.getMembers().get(0))
            .returns(projectMarket.getMembers().get(0).getFirstName(), ProjectMemberDTO::getFirstName)
            .returns(projectMarket.getMembers().get(0).getLastName(), ProjectMemberDTO::getLastName);
    }

    @Test
    void shouldApplyToProjectSuccessfully() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var applyToProjectRequest = ApplyToProjectRequestHelper.defaultsDTO();
        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults();
        projectMarket.setMaxMembers(100);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        when(projectApplicationService.existsByStudentAndProjectMarket(student, projectMarket)).thenReturn(false);

        //when
        projectMarketFacade.applyToProject(marketId, applyToProjectRequest);

        //then
        verify(projectApplicationService, times(1)).applyToMarket(any());
    }

    @Test
    void shouldNotApplyToProjectWhenStudentNotExist() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var applyToProjectRequest = ApplyToProjectRequestHelper.defaultsDTO();
        getIndexNumberFromContext();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(null);

        //when
        assertThatThrownBy(() -> projectMarketFacade.applyToProject(marketId, applyToProjectRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Student not found");

        //then
        verify(projectApplicationService, times(0)).applyToMarket(any());
    }

    @ParameterizedTest
    @MethodSource("projectMarketStatuses")
    void shouldNotApplyToProjectWhenProjectMarketIsNotActive(ProjectMarketStatus projectMarketStatus) {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var applyToProjectRequest = ApplyToProjectRequestHelper.defaultsDTO();
        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults();
        projectMarket.setMaxMembers(100);
        projectMarket.setStatus(projectMarketStatus);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        //when
        assertThatThrownBy(() -> projectMarketFacade.applyToProject(marketId, applyToProjectRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Market is not active.");

        //then
        verify(projectApplicationService, times(0)).applyToMarket(any());
    }

    @Test
    void shouldNotApplyToProjectWhenProjectMarkedReachedMaxMembers() {
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var applyToProjectRequest = ApplyToProjectRequestHelper.defaultsDTO();
        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults();
        projectMarket.setMaxMembers(1);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        //when
        assertThatThrownBy(() -> projectMarketFacade.applyToProject(marketId, applyToProjectRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Project market reached max number of members.");

        //then
        verify(projectApplicationService, times(0)).applyToMarket(any());
    }

    @Test
    void shouldNotApplyToProjectWhenApplicationAlreadyExists() {
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var applyToProjectRequest = ApplyToProjectRequestHelper.defaultsDTO();
        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults();
        projectMarket.setMaxMembers(100);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        when(projectApplicationService.existsByStudentAndProjectMarket(student, projectMarket)).thenReturn(true);

        //when
        assertThatThrownBy(() -> projectMarketFacade.applyToProject(marketId, applyToProjectRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Application already exists for this student and market.");

        //then
        verify(projectApplicationService, times(0)).applyToMarket(any());
    }

    @Test
    void shouldNotApplyToProjectWhenStudentIsAlreadyInProject() {
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var applyToProjectRequest = ApplyToProjectRequestHelper.defaultsDTO();
        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        projectMarket.setMaxMembers(100);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        when(projectApplicationService.existsByStudentAndProjectMarket(student, projectMarket)).thenReturn(false);

        //when
        assertThatThrownBy(() -> projectMarketFacade.applyToProject(marketId, applyToProjectRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Student already exists in project.");

        //then
        verify(projectApplicationService, times(0)).applyToMarket(any());
    }

    @Test
    void shouldGetProjectApplicationByMarketIdInPendingStatus() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        var projectApplication = ProjectApplicationHelper.defaults();
        when(projectApplicationService.getApplicationsForMarket(ProjectApplicationStatus.PENDING, marketId)).thenReturn(List.of(projectApplication));

        //when
        List<ProjectApplicationDTO> result = projectMarketFacade.getProjectApplicationByMarketIdInPendingStatus(marketId);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0))
            .returns(projectApplication.getStudent().getUserData().getFirstName(), ProjectApplicationDTO::getFirstName)
            .returns(projectApplication.getStudent().getUserData().getLastName(), ProjectApplicationDTO::getLastName)
            .returns(projectApplication.getSkills(), ProjectApplicationDTO::getSkills)
            .returns(projectApplication.getContactData(), ProjectApplicationDTO::getContactData)
            .returns(projectApplication.getOtherInformation(), ProjectApplicationDTO::getOtherInformation);
    }

    @Test
    void shouldNotGetProjectApplicationByMarketIdInPendingStatusWhenNotAllowed() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        //when
        assertThatThrownBy(() -> projectMarketFacade.getProjectApplicationByMarketIdInPendingStatus(marketId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("You are not allowed to perform this operation");

        //then
        verify(projectApplicationService, times(0)).getApplicationsForMarket(any(), any());
    }

    @Test
    void shouldNotGetProjectApplicationByMarketIdInPendingStatusStudentNotFound() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(null);

        //when
        assertThatThrownBy(() -> projectMarketFacade.getProjectApplicationByMarketIdInPendingStatus(marketId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Student not found.");

        //then
        verify(projectApplicationService, times(0)).getApplicationsForMarket(any(), any());
    }

    @Test
    void shouldApproveCandidate() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectApplication = ProjectApplicationHelper.defaults();

        when(projectApplicationService.findProjectApplicationById(applicationId)).thenReturn(Optional.of(projectApplication));

        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        //when
        projectMarketFacade.approveCandidate(applicationId);

        //then
        verify(projectApplicationService, times(1)).save(any());
        assertThat(projectApplication.getStatus()).isEqualTo(ProjectApplicationStatus.ACCEPTED);
    }

    @Test
    void shouldNotApproveCandidateWhenApplicationDoesNotExist() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(4));

        when(projectApplicationService.findProjectApplicationById(applicationId)).thenReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> projectMarketFacade.approveCandidate(applicationId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Application with id %s not found".formatted(applicationId));

        //then
        verify(projectApplicationService, times(0)).save(any());
    }

    @Test
    void shouldNotApproveCandidateWhenApproverIsNotOwner() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectApplication = ProjectApplicationHelper.defaults();

        when(projectApplicationService.findProjectApplicationById(applicationId)).thenReturn(Optional.of(projectApplication));

        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        //when
        assertThatThrownBy(() -> projectMarketFacade.approveCandidate(applicationId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("You are not allowed to perform this operation");

        //then
        verify(projectApplicationService, times(0)).save(any());
    }

    @Test
    void shouldNotApproveCandidateWhenStudentIsNull() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectApplication = ProjectApplicationHelper.defaults();

        when(projectApplicationService.findProjectApplicationById(applicationId)).thenReturn(Optional.of(projectApplication));

        getIndexNumberFromContext();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(null);

        //when
        assertThatThrownBy(() -> projectMarketFacade.approveCandidate(applicationId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Student not found.");

        //then
        verify(projectApplicationService, times(0)).save(any());
    }

    @ParameterizedTest
    @MethodSource("projectApplicationStatuses")
    void shouldNotApproveCandidateWhenProjectApplicationStatusIsNotPending(ProjectApplicationStatus status) {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectApplication = ProjectApplicationHelper.defaults();
        projectApplication.setStatus(status);

        when(projectApplicationService.findProjectApplicationById(applicationId)).thenReturn(Optional.of(projectApplication));

        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        //when
        assertThatThrownBy(() -> projectMarketFacade.approveCandidate(applicationId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Application should be in PENDING state");

        //then
        verify(projectApplicationService, times(0)).save(any());
    }

    @Test
    void shouldRejectCandidate() {
        //given
        var applicationId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectApplication = ProjectApplicationHelper.defaults();

        when(projectApplicationService.findProjectApplicationById(applicationId)).thenReturn(Optional.of(projectApplication));

        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        //when
        projectMarketFacade.rejectCandidate(applicationId);

        //then
        verify(projectApplicationService, times(1)).save(any());
        assertThat(projectApplication.getStatus()).isEqualTo(ProjectApplicationStatus.REJECTED);
    }

    @Test
    void shouldGetApplicationsForStudent() {
        //given
        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectApplication = ProjectApplicationHelper.defaults();
        when(projectApplicationService.getApplicationsForStudent(student)).thenReturn(List.of(projectApplication));

        //when
        List<StudentProjectApplicationDTO> result = projectMarketFacade.getApplicationsForStudent();

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0))
            .returns(projectApplication.getId(), StudentProjectApplicationDTO::getId)
            .returns(projectApplication.getCreationDate(), StudentProjectApplicationDTO::getApplicationDate)
            .returns(projectApplication.getDecisionDate(), StudentProjectApplicationDTO::getDecisionDate)
            .returns(projectApplication.getStatus().name(), StudentProjectApplicationDTO::getStatus);
    }

    @Test
    void shouldGetSupervisors() {
        //given
        String studyYear = RandomStringUtils.randomNumeric(4);
        var supervisor = SupervisorHelper.defaults(studyYear);
        var supervisor2 = SupervisorHelper.defaults(studyYear);

        when(supervisorDAO.findAllByStudyYear(studyYear)).thenReturn(List.of(supervisor, supervisor2));

        //when
        List<ProjectMarketSupervisorDTO> result = projectMarketFacade.getSupervisors(studyYear);

        //then
        assertThat(result).hasSize(2);

        assertThat(result.get(0))
            .returns(supervisor.getId(), ProjectMarketSupervisorDTO::getId)
            .returns(supervisor.getUserData().getFirstName(), ProjectMarketSupervisorDTO::getFirstName)
            .returns(supervisor.getUserData().getLastName(), ProjectMarketSupervisorDTO::getLastName);
        assertThat(result.get(1))
            .returns(supervisor2.getId(), ProjectMarketSupervisorDTO::getId)
            .returns(supervisor2.getUserData().getFirstName(), ProjectMarketSupervisorDTO::getFirstName)
            .returns(supervisor2.getUserData().getLastName(), ProjectMarketSupervisorDTO::getLastName);
    }

    @Test
    void shouldSubmitProjectMarketToSupervisor() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));

        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        var supervisorId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var supervisor = SupervisorHelper.defaults();
        when(supervisorDAO.getReferenceById(supervisorId)).thenReturn(supervisor);

        //when
        projectMarketFacade.submitProjectMarketToSupervisor(marketId, supervisorId);

        //then
        verify(projectMarketService, times(1)).save(any());
        assertThat(projectMarket.getProject().getSupervisor().getId()).isEqualTo(supervisor.getId());
        assertThat(projectMarket.getStatus()).isEqualTo(ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR);
    }

    @Test
    void shouldNotSubmitProjectMarketToSupervisorWhenSupervisorReachedMaximumNumberOfProjects() {
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        var supervisorId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var supervisor = SupervisorHelper.defaults();
        supervisor.setMaxNumberOfProjects(1);
        when(supervisorDAO.getReferenceById(supervisorId)).thenReturn(supervisor);

        //when
        assertThatThrownBy(() -> projectMarketFacade.submitProjectMarketToSupervisor(marketId, supervisorId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Maximum number of projects reached by supervisor.");

        //then
        verify(projectMarketService, times(0)).save(any());
    }

    @ParameterizedTest
    @MethodSource("projectMarketStatuses")
    void shouldNotSubmitProjectMarketToSupervisorWhenMarketHasWrongStatus(ProjectMarketStatus status) {
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        projectMarket.setStatus(status);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        var supervisorId = Long.parseLong(RandomStringUtils.randomNumeric(4));

        //when
        assertThatThrownBy(() -> projectMarketFacade.submitProjectMarketToSupervisor(marketId, supervisorId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Market is not active.");

        //then
        verify(projectMarketService, times(0)).save(any());
    }

    @Test
    void shouldNotSubmitProjectMarketToSupervisorWhenStudentIsNotAnOwner() {
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        var supervisorId = Long.parseLong(RandomStringUtils.randomNumeric(4));

        //when
        assertThatThrownBy(() -> projectMarketFacade.submitProjectMarketToSupervisor(marketId, supervisorId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Only project owner can submit project to supervisor");

        //then
        verify(projectMarketService, times(0)).save(any());
    }

    @ParameterizedTest
    @MethodSource("projectMarketStatusesAvailableToClose")
    void shouldCloseProjectMarketByOwner(ProjectMarketStatus status) {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        projectMarket.setStatus(status);

        //when
        projectMarketFacade.closeProjectMarketByOwner(marketId);

        //then
        verify(projectMarketService, times(1)).save(any());
        assertThat(projectMarket.getStatus()).isEqualTo(ProjectMarketStatus.CLOSED_BY_OWNER);

    }

    @ParameterizedTest
    @MethodSource("projectMarketStatusesNotAvailableToClose")
    void shouldNotCloseProjectMarketByOwnerWhenProjectMarketStatusIsWrong(ProjectMarketStatus status) {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        var student = StudentHelper.defaults();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        projectMarket.setStatus(status);

        //when
        assertThatThrownBy(() -> projectMarketFacade.closeProjectMarketByOwner(marketId))
            .isInstanceOf(IllegalStateException.class);

        //then
        verify(projectMarketService, times(0)).save(any());
    }

    @Test
    void shouldNotCloseProjectMarketByOwnerWhenStudentIsNotAnOwner() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        var student = mock(Student.class);
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(student);

        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);

        //when
        assertThatThrownBy(() -> projectMarketFacade.closeProjectMarketByOwner(marketId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Only project owner can close this project market.");

        //then
        verify(projectMarketService, times(0)).save(any());
    }

    @Test
    void shouldNotCloseProjectMarketByOwnerWhenStudentIsNull() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        getIndexNumberFromContext();
        when(studentDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(null);

        //when
        assertThatThrownBy(() -> projectMarketFacade.closeProjectMarketByOwner(marketId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Student not found.");

        //then
        verify(projectMarketService, times(0)).save(any());
    }

    @Test
    void shouldGetProjectMarketsForSupervisor() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        getIndexNumberFromContext();
        var supervisor = SupervisorHelper.defaults();
        when(supervisorDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(supervisor);

        var projectMarket = ProjectMarketHelper.defaults();
        when(projectMarketService.findByAssignedSupervisor(supervisor, pageable)).thenReturn(createPage(projectMarket));

        //when
        Page<ProjectMarketDTO> result = projectMarketFacade.getProjectMarketsForSupervisor(pageable);

        //then
        assertThat(result).hasSize(1)
            .map(ProjectMarketDTO::getProjectName)
            .containsExactlyInAnyOrder(projectMarket.getProject().getName());

    }

    @Test
    void shouldNotGetProjectMarketsForSupervisorWhenSupervisorIsNull() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        getIndexNumberFromContext();
        when(supervisorDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(null);

        //when
        assertThatThrownBy(() -> projectMarketFacade.getProjectMarketsForSupervisor(pageable))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Could not get supervisor data");

        //then
        verify(projectMarketService, times(0)).findByAssignedSupervisor(any(), any());
    }

    @Test
    void shouldApproveProjectAndCloseProjectMarket() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        getIndexNumberFromContext();
        var supervisor = SupervisorHelper.defaults();
        projectMarket.submit(supervisor);
        when(supervisorDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(supervisor);

        //when
        projectMarketFacade.approveProjectAndCloseMarket(marketId);

        //then
        verify(projectMarketService, times(1)).save(any());
        assertThat(projectMarket.getStatus()).isEqualTo(ProjectMarketStatus.APPROVED_BY_SUPERVISOR);
    }

    @Test
    void shouldNotApproveProjectAndCloseProjectMarketWhenSupervisorIsNull() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        getIndexNumberFromContext();
        var supervisor = SupervisorHelper.defaults();
        projectMarket.submit(supervisor);
        when(supervisorDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(null);

        //when
        assertThatThrownBy(() -> projectMarketFacade.approveProjectAndCloseMarket(marketId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Supervisor not found or is not assigned to this project market");

        //then
        verify(projectMarketService, times(0)).save(any());
        assertThat(projectMarket.getStatus()).isEqualTo(ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR);
    }

    @Test
    void shouldNotApproveProjectAndCloseProjectMarketWhenSupervisorIsNotSignedToProjectMarket() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        getIndexNumberFromContext();
        var supervisor = SupervisorHelper.defaults();
        projectMarket.submit(supervisor);
        when(supervisorDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(SupervisorHelper.defaults());

        //when
        assertThatThrownBy(() -> projectMarketFacade.approveProjectAndCloseMarket(marketId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Supervisor not found or is not assigned to this project market");

        //then
        verify(projectMarketService, times(0)).save(any());
        assertThat(projectMarket.getStatus()).isEqualTo(ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR);
    }

    @ParameterizedTest
    @MethodSource("projectMarketStatusesNotAvailableToApproveBySupervisor")
    void shouldNotApproveProjectAndCloseProjectMarketWhenStatusIsWrong(ProjectMarketStatus status) {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        getIndexNumberFromContext();
        var supervisor = SupervisorHelper.defaults();
        projectMarket.submit(supervisor);
        projectMarket.setStatus(status);
        when(supervisorDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(supervisor);

        //when
        assertThatThrownBy(() -> projectMarketFacade.approveProjectAndCloseMarket(marketId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Project market status is not SENT_FOR_APPROVAL_TO_SUPERVISOR");

        //then
        verify(projectMarketService, times(0)).save(any());
        assertThat(projectMarket.getStatus()).isEqualTo(status);
    }

    @Test
    void shouldRejectProjectAndCloseProjectMarket() {
        //given
        var marketId = Long.parseLong(RandomStringUtils.randomNumeric(4));
        var projectMarket = ProjectMarketHelper.defaults(INDEX_NUMBER);
        when(projectMarketService.getProjectMarketById(marketId)).thenReturn(projectMarket);
        getIndexNumberFromContext();
        var supervisor = SupervisorHelper.defaults();
        projectMarket.submit(supervisor);
        when(supervisorDAO.findByUserData_IndexNumber(INDEX_NUMBER)).thenReturn(supervisor);

        //when
        projectMarketFacade.rejectProjectAndCloseMarket(marketId);

        //then
        verify(projectMarketService, times(1)).save(any());
        assertThat(projectMarket.getStatus()).isEqualTo(ProjectMarketStatus.REJECTED_BY_SUPERVISOR);
    }

    private void getIndexNumberFromContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(INDEX_NUMBER);
    }

    private static Stream<Arguments> projectMarketStatuses() {
        return Stream.of(Arguments.of(ProjectMarketStatus.CLOSED_BY_OWNER),
            Arguments.of(ProjectMarketStatus.REJECTED_BY_SUPERVISOR),
            Arguments.of(ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR),
            Arguments.of(ProjectMarketStatus.APPROVED_BY_SUPERVISOR));
    }

    private static Stream<Arguments> projectMarketStatusesAvailableToClose() {
        return Stream.of(Arguments.of(ProjectMarketStatus.ACTIVE),
            Arguments.of(ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR));
    }

    private static Stream<Arguments> projectMarketStatusesNotAvailableToClose() {
        return Stream.of(Arguments.of(ProjectMarketStatus.CLOSED_BY_OWNER),
            Arguments.of(ProjectMarketStatus.REJECTED_BY_SUPERVISOR),
            Arguments.of(ProjectMarketStatus.APPROVED_BY_SUPERVISOR));
    }

    private static Stream<Arguments> projectMarketStatusesNotAvailableToApproveBySupervisor() {
        return Stream.of(Arguments.of(ProjectMarketStatus.CLOSED_BY_OWNER),
            Arguments.of(ProjectMarketStatus.REJECTED_BY_SUPERVISOR),
            Arguments.of(ProjectMarketStatus.APPROVED_BY_SUPERVISOR),
            Arguments.of(ProjectMarketStatus.ACTIVE));
    }

    private static Stream<Arguments> projectApplicationStatuses() {
        return Stream.of(Arguments.of(ProjectApplicationStatus.REJECTED),
            Arguments.of(ProjectApplicationStatus.ACCEPTED));
    }
}
