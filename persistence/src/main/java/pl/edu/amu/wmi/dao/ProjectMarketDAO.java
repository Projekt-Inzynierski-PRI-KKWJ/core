package pl.edu.amu.wmi.dao;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.amu.wmi.entity.ProjectMarket;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;

@Repository
public interface ProjectMarketDAO extends JpaRepository<ProjectMarket, Long> {

    Page<ProjectMarket> findByStatus(ProjectMarketStatus status, Pageable pageable);

    @Query("SELECT pm FROM ProjectMarket pm " +
           "LEFT JOIN pm.project p " +
           "WHERE pm.status = :status " +
           "AND (LOWER(pm.proposalName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR (p IS NOT NULL AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))))")
    Page<ProjectMarket> findByProject_NameContainingIgnoreCaseAndStatus(@Param("name") String name, @Param("status") ProjectMarketStatus status, Pageable pageable);

    @Query("SELECT pm FROM ProjectMarket pm WHERE pm.project.id = :projectId")
    Optional<ProjectMarket> findByProject_Id(@Param("projectId") Long projectId);

    @Query("SELECT pm FROM ProjectMarket pm " +
           "LEFT JOIN pm.project p " +
           "WHERE pm.proposalSupervisorId = :supervisorId " +
           "OR (p IS NOT NULL AND p.supervisor.id = :supervisorId)")
    Page<ProjectMarket> findByProject_Supervisor(@Param("supervisorId") Long supervisorId, Pageable pageable);

    @Query("SELECT DISTINCT pm FROM ProjectMarket pm " +
           "LEFT JOIN StudentProject sp ON sp.project = pm.project " +
           "WHERE (pm.proposalOwnerId = :studentId " +
           "OR (sp.student.id = :studentId AND sp.isProjectAdmin = true)) " +
           "AND pm.status IN :statuses")
    Page<ProjectMarket> findByProjectLeader(@Param("studentId") Long studentId, @Param("statuses") java.util.List<ProjectMarketStatus> statuses, Pageable pageable);
}
