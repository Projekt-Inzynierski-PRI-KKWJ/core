package pl.edu.amu.wmi.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectApplicationDAO;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Student;
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
        save(projectApplicationEntityMapper.toEntity(request));
    }

    public void save(ProjectApplication application) {
        projectApplicationDAO.save(application);
    }

    public List<ProjectApplication> getApplicationForMarket(ProjectApplicationStatus status, Long projectMarketId) {
        return projectApplicationDAO.findByStatusAndProjectMarket_Id(status, projectMarketId);
    }

    public List<ProjectApplication> getApplicationForStudent(Student student) {
        return projectApplicationDAO.findByStudent(student);
    }

    public Optional<ProjectApplication> findProjectApplicationById(Long id) {
        return projectApplicationDAO.findById(id);
    }

    public boolean existsByStudentAndMProjectMarket(Student student, ProjectMarket projectMarket) {
        return projectApplicationDAO.existsByStudentAndProjectMarket(student, projectMarket);
    }
}
