package pl.edu.amu.wmi.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectMarketDAO;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;
import pl.edu.amu.wmi.mapper.ProjectMarketEntityMapper;
import pl.edu.amu.wmi.model.PublishProjectMarketRequest;

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

    public ProjectMarket getByProjectMarketId(Long id) {
        return projectMarketDAO.getReferenceById(id);
    }
}
