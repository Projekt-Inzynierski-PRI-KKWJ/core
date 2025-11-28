package pl.edu.amu.wmi.service.imp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.mapper.project.ProjectMapper;
import pl.edu.amu.wmi.model.project.ProjectDTO;
import pl.edu.amu.wmi.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final ProjectDAO projectDAO;
    private final ProjectMapper projectMapper;

    @Autowired
    public StatisticsServiceImpl(ProjectDAO projectDAO, ProjectMapper projectMapper) {
        this.projectDAO = projectDAO;
        this.projectMapper = projectMapper;
    }

//    @Override
//    public List<ProjectDTO> getAllProjects() {
//        List<Project> projects = projectDAO.findAll();
//        return projects.stream()
//                .map(projectMapper::mapToProjectDto)
//                .collect(Collectors.toList());
//    }


    @Override
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectDAO.findAll();



        log.info("=== [STATISTICS] Pobieranie wszystkich projektów – znaleziono {} elementów ===", projects.size());

        for (Project project : projects) {
            log.info("Projekt, Name: {}, Supervisor: {}",
                    project.getName(),
                    project.getSupervisor() != null ? project.getSupervisor().getFullName() : "BRAK"
            );
        }

        List<ProjectDTO> result = projects.stream()
                .map(projectMapper::mapToProjectDto)
                .collect(Collectors.toList());

        log.info("=== [STATISTICS] Zmapowano projekty do DTO – liczba: {} ===", result.size());

        return result;
    }
}
