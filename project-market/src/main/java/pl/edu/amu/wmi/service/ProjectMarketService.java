package pl.edu.amu.wmi.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.ProjectMarketDAO;
import pl.edu.amu.wmi.entity.Project;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;
import pl.edu.amu.wmi.mapper.ProjectMarketMapper;

@Service
@RequiredArgsConstructor
public class ProjectMarketService {

    private final ProjectMarketDAO projectMarketDAO;
    private final ProjectMarketMapper projectMarketMapper;

    public ProjectMarket publishMarket(Project project) {
        return projectMarketDAO.save(projectMarketMapper.fromProject(project));
    }

    public Page<ProjectMarket> listActiveMarkets(Pageable pageable) {
        return projectMarketDAO.findByStatus(ProjectMarketStatus.ACTIVE, pageable);
    }

    public List<ProjectMarket> searchActiveMarketsByName(String name) {
        return projectMarketDAO.findByProject_NameContainingIgnoreCaseAndStatus(name, ProjectMarketStatus.ACTIVE);
    }

    public Optional<ProjectMarket> findByProjectId(Long id) {
        return projectMarketDAO.findByProject_Id(id);
    }

    public ProjectMarket save(ProjectMarket projectMarket) {
        return projectMarketDAO.save(projectMarket);
    }

    public ProjectMarket closeMarket(ProjectMarket projectMarket) {
        projectMarket.close();
        return projectMarketDAO.save(projectMarket);
    }
}
