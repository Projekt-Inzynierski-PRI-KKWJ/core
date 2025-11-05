package pl.edu.amu.wmi.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.amu.wmi.entity.ProjectApplication;

public interface ProjectApplicationDAO extends JpaRepository<ProjectApplication, Long> {

    boolean existsByStudent_IdAndProjectMarket_Id(Long studentId, Long projectMarketId);

    List<ProjectApplication> findByProjectMarket_Id(Long projectMarketId);
}
