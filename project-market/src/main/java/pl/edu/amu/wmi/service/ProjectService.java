package pl.edu.amu.wmi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.mapper.ProjectMapper;
import pl.edu.amu.wmi.model.ProjectCreateRequest;
import pl.edu.amu.wmi.model.SubmitProjectRequest;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectDAO projectDAO;
    private final ProjectMapper projectMapper;

    public Project createProject(ProjectCreateRequest request) {
        return projectDAO.save(projectMapper.toEntity(request));
    }

    public Project getProjectById(Long id) {
        return projectDAO.getReferenceById(id);
    }

    public Project submitProject(SubmitProjectRequest request) {
        var project = request.getProject();
        project.submit(request.getSupervisor());
        return projectDAO.save(project);
    }

    public Project acceptProject(Project project) {
        project.accept();
        return projectDAO.save(project);
    }

    public Project rejectProject(Project project) {
        project.reject();
        return projectDAO.save(project);
    }
}
