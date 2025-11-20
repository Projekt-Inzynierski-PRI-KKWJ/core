package pl.edu.amu.wmi.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.amu.wmi.entity.ProjectApplication;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;

public interface ProjectApplicationDAO extends JpaRepository<ProjectApplication, Long> {

    boolean existsByStudent_IdAndProjectMarket_Id(Long studentId, Long projectMarketId);

    List<ProjectApplication> findByStatusAndProjectMarket_Id(ProjectApplicationStatus status, Long projectMarketId);
}
