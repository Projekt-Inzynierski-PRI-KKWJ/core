package pl.edu.amu.wmi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.amu.wmi.entity.EvaluationCard;
import pl.edu.amu.wmi.enumerations.Semester;

import java.util.List;

@Repository
public interface EvaluationCardDAO extends JpaRepository<EvaluationCard, Long> {

    List<EvaluationCard> findAllByProject_Id(Long projectId);

    boolean existsBySemesterAndIsActiveTrue(Semester semester);

    @Modifying
    @Query("""
        update EvaluationCard ec
        set ec.isActive = false
        where ec.semester = :semester
    """)
    void deactivateBySemester(@Param("semester") Semester semester);

    @Modifying
    @Query("""
        update EvaluationCard ec
        set ec.isActive = true
        where ec.semester = :semester
    """)
    void activateBySemester(@Param("semester") Semester semester);

}
