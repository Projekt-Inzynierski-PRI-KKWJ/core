package pl.edu.amu.wmi.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.mapper.project.ProjectMapper;
import pl.edu.amu.wmi.model.project.ProjectDTO;
import pl.edu.amu.wmi.service.StatisticsService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final ProjectDAO projectDAO;
    private final ProjectMapper projectMapper;

    @Autowired
    public StatisticsServiceImpl(ProjectDAO projectDAO, ProjectMapper projectMapper) {
        this.projectDAO = projectDAO;
        this.projectMapper = projectMapper;
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectDAO.findAll();
        return projects.stream()
                .map(projectMapper::mapToProjectDto)
                .collect(Collectors.toList());
    }
}
