package pl.edu.amu.wmi.service;

import pl.edu.amu.wmi.model.project.ProjectDTO;

import java.util.List;

public interface StatisticsService {

    List<ProjectDTO> getAllProjects();
}
