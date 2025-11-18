package pl.edu.amu.wmi.dao;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;

@Repository
public interface ProjectMarketDAO extends JpaRepository<ProjectMarket, Long> {

    Page<ProjectMarket> findByStatus(ProjectMarketStatus status, Pageable pageable);

    Page<ProjectMarket> findByProject_NameContainingIgnoreCaseAndStatus(String name, ProjectMarketStatus status, Pageable pageable);

    Optional<ProjectMarket> findByProject_Id(Long projectId);

    Page<ProjectMarket> findByProject_Supervisor(Supervisor supervisor, Pageable pageable);
}
