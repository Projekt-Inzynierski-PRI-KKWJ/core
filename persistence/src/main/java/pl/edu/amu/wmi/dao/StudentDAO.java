package pl.edu.amu.wmi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.edu.amu.wmi.entity.Student;

import java.util.List;

@Repository
public interface StudentDAO extends JpaRepository<Student, Long> {

    Student findByStudyYearAndUserData_IndexNumber(String studyYear, String indexNumber);

    Student findByUserData_IndexNumber(String indexNumber);

    List<Student> findByStudyYearAndUserData_IndexNumberIn(String studyYear, List<String> indexNumbers);

    List<Student> findAllByStudyYear(String studyYear);

    @Query("SELECT DISTINCT s FROM Student s " +
           "LEFT JOIN FETCH s.confirmedProject cp " +
           "LEFT JOIN FETCH cp.evaluationCards " +
           "LEFT JOIN FETCH s.assignedProjects ap " +
           "LEFT JOIN FETCH ap.project " +
           "WHERE s.studyYear = :studyYear")
    List<Student> findAllByStudyYearWithProjects(@Param("studyYear") String studyYear);

    List<Student> findAllByStudyYear_AndUserData_IndexNumberIn(String studyYear, List<String> indexNumbers);

    List<Student> findAllByUserData_IndexNumber(String indexNumber);
}
