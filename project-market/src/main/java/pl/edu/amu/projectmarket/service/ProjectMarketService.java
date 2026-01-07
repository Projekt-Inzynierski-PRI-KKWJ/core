package pl.edu.amu.projectmarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectMarketDAO;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;
import pl.edu.amu.projectmarket.mapper.ProjectMarketEntityMapper;
import pl.edu.amu.projectmarket.model.PublishProjectMarketRequest;

@Service
@RequiredArgsConstructor
public class ProjectMarketService {

    private final ProjectMarketDAO projectMarketDAO;
    private final ProjectMarketEntityMapper projectMarketEntityMapper;

    public void publishMarket(PublishProjectMarketRequest request) {
        projectMarketDAO.save(projectMarketEntityMapper.fromRequest(request));
    }

    public Page<ProjectMarket> listActiveMarkets(Pageable pageable) {
        return projectMarketDAO.findByStatus(ProjectMarketStatus.ACTIVE, pageable);
    }

    public Page<ProjectMarket> searchActiveMarketsByNamePattern(String name, Pageable pageable) {
        return projectMarketDAO.findByProject_NameContainingIgnoreCaseAndStatus(name, ProjectMarketStatus.ACTIVE, pageable);
    }

    public void save(ProjectMarket projectMarket) {
        projectMarketDAO.save(projectMarket);
    }

    public ProjectMarket getProjectMarketById(Long id) {
        return projectMarketDAO.getReferenceById(id);
    }

    public Page<ProjectMarket> findByAssignedSupervisor(Supervisor supervisor, Pageable pageable) {
        return projectMarketDAO.findByProject_Supervisor(supervisor, pageable);
    }

    public Page<ProjectMarket> findByProjectLeader(Long studentId, Pageable pageable) {
        return projectMarketDAO.findByProjectLeader(studentId, pageable);
    }
}
