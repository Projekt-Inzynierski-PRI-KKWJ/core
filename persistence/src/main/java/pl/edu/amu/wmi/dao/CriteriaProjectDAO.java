package pl.edu.amu.wmi.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.amu.wmi.entity.CriteriaProject;
import pl.edu.amu.wmi.enumerations.Semester;

import java.util.List;

@Repository
public interface CriteriaProjectDAO extends JpaRepository<CriteriaProject, Long>
{
    List<CriteriaProject> findByProjectIdAndSemester(Long projectId, Semester semester);


}
