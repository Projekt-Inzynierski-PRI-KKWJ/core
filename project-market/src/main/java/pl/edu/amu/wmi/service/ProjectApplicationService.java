package pl.edu.amu.wmi.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectApplicationDAO;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;
import pl.edu.amu.wmi.mapper.ProjectApplicationEntityMapper;
import pl.edu.amu.wmi.model.ApplyToProjectRequest;

@Service
@RequiredArgsConstructor
public class ProjectApplicationService {

    private final ProjectApplicationDAO projectApplicationDAO;
    private final ProjectApplicationEntityMapper projectApplicationEntityMapper;

    public void applyToMarket(ApplyToProjectRequest request) {
        var exists = projectApplicationDAO.existsByStudent_IdAndProjectMarket_Id(request.getStudent().getId(),
            request.getProjectMarket().getId());
        if (exists) {
            throw new IllegalStateException("Student already applied to this project");
        }
        projectApplicationDAO.save(projectApplicationEntityMapper.toEntity(request));
    }

    public List<ProjectApplication> getApplicationForMarket(ProjectApplicationStatus status, Long projectMarketId) {
        return projectApplicationDAO.findByStatusAndProjectMarket_Id(status, projectMarketId);
    }

    public ProjectApplication accept(ProjectApplication application) {
        application.accept();
        return projectApplicationDAO.save(application);
    }

    public ProjectApplication reject(ProjectApplication application) {
        application.reject();
        return projectApplicationDAO.save(application);
    }
}
