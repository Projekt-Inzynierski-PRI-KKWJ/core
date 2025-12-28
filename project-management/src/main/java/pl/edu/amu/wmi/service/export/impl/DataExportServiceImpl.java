package pl.edu.amu.wmi.service.export.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.dao.StudentDAO;
import pl.edu.amu.wmi.dao.SupervisorDAO;
import pl.edu.amu.wmi.dao.StudyYearDAO;
import pl.edu.amu.wmi.entity.EvaluationCard;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.Student;
import pl.edu.amu.wmi.entity.StudyYear;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.enumerations.AcceptanceStatus;
import pl.edu.amu.wmi.enumerations.Semester;
import pl.edu.amu.wmi.mapper.project.ProjectMapper;
import pl.edu.amu.wmi.model.export.StudyYearDataExportDTO;
import pl.edu.amu.wmi.model.project.ProjectDetailsDTO;
import pl.edu.amu.wmi.service.export.DataExportService;
import pl.edu.amu.wmi.service.grade.EvaluationCardService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class DataExportServiceImpl implements DataExportService {

    private final ProjectDAO projectDAO;
    private final StudentDAO studentDAO;
    private final SupervisorDAO supervisorDAO;
    private final StudyYearDAO studyYearDAO;
    private final ProjectMapper projectMapper;
    private final EvaluationCardService evaluationCardService;

    public DataExportServiceImpl(ProjectDAO projectDAO,
                                StudentDAO studentDAO,
                                SupervisorDAO supervisorDAO,
                                StudyYearDAO studyYearDAO,
                                ProjectMapper projectMapper,
                                EvaluationCardService evaluationCardService) {
        this.projectDAO = projectDAO;
        this.studentDAO = studentDAO;
        this.supervisorDAO = supervisorDAO;
        this.studyYearDAO = studyYearDAO;
        this.projectMapper = projectMapper;
        this.evaluationCardService = evaluationCardService;
    }

    @Override
    public StudyYearDataExportDTO exportStudyYearData(String studyYear) {
        log.info("Starting data export for study year: {}", studyYear);

        StudyYearDataExportDTO exportDTO = new StudyYearDataExportDTO();
        exportDTO.setStudyYear(studyYear);

        // Export projects with all related data
        log.info("Querying projects for study year: {}", studyYear);
        List<Project> projects = projectDAO.findAllBySupervisorIsNotNullAndStudyYear_StudyYear(studyYear);
        log.info("Found {} projects for study year: {}", projects.size(), studyYear);
        List<ProjectDetailsDTO> projectDTOs = projects.stream()
                .map(project -> {
                    ProjectDetailsDTO dto = projectMapper.mapToProjectDetailsDto(project);
                    // Populate grade information
                    dto.setFirstSemesterGrade(evaluationCardService.getPointsForSemester(project, Semester.FIRST));
                    dto.setSecondSemesterGrade(evaluationCardService.getPointsForSemester(project, Semester.SECOND));
                    dto.setFinalGrade(getFinalGrade(project));
                    dto.setCriteriaMet(getCriteriaMet(project));
                    return dto;
                })
                .toList();
        exportDTO.setProjects(projectDTOs);

        // Export students
        log.info("Querying students for study year: {}", studyYear);
        List<Student> students = studentDAO.findAllByStudyYear(studyYear);
        log.info("Found {} students for study year: {}", students.size(), studyYear);
        List<StudyYearDataExportDTO.StudentExportDTO> studentDTOs = students.stream()
                .map(this::mapStudentToExportDTO)
                .toList();
        exportDTO.setStudents(studentDTOs);

        // Export supervisors
        log.info("Querying supervisors for study year: {}", studyYear);
        List<Supervisor> supervisors = supervisorDAO.findAllByStudyYear(studyYear);
        log.info("Found {} supervisors for study year: {}", supervisors.size(), studyYear);
        List<StudyYearDataExportDTO.SupervisorExportDTO> supervisorDTOs = supervisors.stream()
                .map(this::mapSupervisorToExportDTO)
                .toList();
        exportDTO.setSupervisors(supervisorDTOs);

        // Create metadata
        StudyYearDataExportDTO.ExportMetadata metadata = new StudyYearDataExportDTO.ExportMetadata();
        metadata.setExportDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        metadata.setTotalProjects(projects.size());
        metadata.setTotalStudents(students.size());
        metadata.setTotalSupervisors(supervisors.size());
        metadata.setAcceptedProjects((int) projects.stream()
                .filter(p -> p.getAcceptanceStatus() == AcceptanceStatus.ACCEPTED)
                .count());
        metadata.setConfirmedProjects((int) students.stream()
                .filter(Student::isProjectConfirmed)
                .count());

        exportDTO.setMetadata(metadata);

        log.info("Data export completed for study year: {}. Projects: {}, Students: {}, Supervisors: {}",
                studyYear, projects.size(), students.size(), supervisors.size());
        log.info("Export DTO study year field: {}", exportDTO.getStudyYear());

        return exportDTO;
    }

    private StudyYearDataExportDTO.StudentExportDTO mapStudentToExportDTO(Student student) {
        StudyYearDataExportDTO.StudentExportDTO dto = new StudyYearDataExportDTO.StudentExportDTO();
        dto.setIndexNumber(student.getUserData().getIndexNumber());
        dto.setFirstName(student.getUserData().getFirstName());
        dto.setLastName(student.getUserData().getLastName());
        dto.setEmail(student.getUserData().getEmail());
        dto.setStudyYear(student.getStudyYear());
        dto.setAccepted(student.isProjectConfirmed());
        dto.setProjectAdmin(student.isProjectAdmin());
        dto.setConfirmedProjectId(student.getConfirmedProject() != null ?
                String.valueOf(student.getConfirmedProject().getId()) : null);
        dto.setRole(student.getProjectRole() != null ? student.getProjectRole().name() : null);
        return dto;
    }

    private StudyYearDataExportDTO.SupervisorExportDTO mapSupervisorToExportDTO(Supervisor supervisor) {
        StudyYearDataExportDTO.SupervisorExportDTO dto = new StudyYearDataExportDTO.SupervisorExportDTO();
        dto.setIndexNumber(supervisor.getUserData().getIndexNumber());
        dto.setFirstName(supervisor.getUserData().getFirstName());
        dto.setLastName(supervisor.getUserData().getLastName());
        dto.setEmail(supervisor.getUserData().getEmail());
        dto.setStudyYear(supervisor.getStudyYear());
        dto.setMaxProjects(supervisor.getMaxNumberOfProjects() != null ? supervisor.getMaxNumberOfProjects() : 0);
        dto.setCurrentProjects(supervisor.getProjects() != null ? supervisor.getProjects().size() : 0);

        // Calculate availability based on existing logic from SupervisorProjectServiceImpl
        int acceptedProjectCount = supervisor.getProjects() != null ?
                (int) supervisor.getProjects().stream()
                        .filter(project -> project.getAcceptanceStatus() == AcceptanceStatus.ACCEPTED)
                        .count() : 0;
        int maxProjects = supervisor.getMaxNumberOfProjects() != null ? supervisor.getMaxNumberOfProjects() : 0;
        dto.setAvailable(acceptedProjectCount < maxProjects);

        return dto;
    }

    @Override
    public List<String> getAvailableStudyYears() {
        log.info("Retrieving all available study years");

        List<StudyYear> studyYears = studyYearDAO.findAll();
        List<String> studyYearStrings = studyYears.stream()
                .map(StudyYear::getStudyYear)
                .sorted() // Sort alphabetically
                .toList();

        log.info("Found {} study years", studyYearStrings.size());
        return studyYearStrings;
    }
}
