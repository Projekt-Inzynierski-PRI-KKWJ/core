package pl.edu.amu.wmi.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectApplicationDAO;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.mapper.ProjectApplicationMapper;
import pl.edu.amu.wmi.model.ProjectApplicationRequest;

@Service
@RequiredArgsConstructor
public class ProjectApplicationService {

    private final ProjectApplicationDAO projectApplicationDAO;
    private final ProjectApplicationMapper projectApplicationMapper;

    public ProjectApplication applyToMarket(ProjectApplicationRequest request) {
        var exists = projectApplicationDAO.existsByStudent_IdAndProjectMarket_Id(request.getStudent().getId(),
            request.getProjectMarket().getId());
        if (exists) {
            throw new IllegalStateException("Student already applied to this project");
        }
        return projectApplicationDAO.save(projectApplicationMapper.toEntity(request));
    }

    public List<ProjectApplication> getApplicationForMarket(Long projectMarketId) {
        return projectApplicationDAO.findByProjectMarket_Id(projectMarketId);
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
