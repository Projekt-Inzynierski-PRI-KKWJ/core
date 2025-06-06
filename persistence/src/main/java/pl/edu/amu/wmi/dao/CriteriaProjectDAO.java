package pl.edu.amu.wmi.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.amu.wmi.entity.CriteriaProject;
import pl.edu.amu.wmi.enumerations.Semester;
import pl.edu.amu.wmi.enumerations.TypeOfCriterium;

import java.util.List;

@Repository
public interface CriteriaProjectDAO extends JpaRepository<CriteriaProject, Long>
{
    List<CriteriaProject> findByProject_IdAndSemesterAndType(Long projectId, Semester semester, TypeOfCriterium type);


    boolean existsByCriteriumAndProject_IdAndSemesterAndType(
            String criterium,
            Long projectId,
            Semester semester,
            TypeOfCriterium type
    );

}
