package pl.edu.amu.wmi.service.project.impl;



import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.amu.wmi.dao.*;
import pl.edu.amu.wmi.entity.*;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ProjectDeletionServiceImp {

    private final ProjectDAO projectDAO;
    private final StudentDAO studentDAO;
    private final StudentProjectDAO studentProjectDAO;
    private final CriteriaProjectDAO criteriaProjectDAO;
    private final ExternalLinkDAO externalLinkDAO;
    private final ProjectDefenseDAO projectDefenseDAO;
    private final ProjectMarketDAO projectMarketDAO;

    @Transactional
    public void deleteProject(Long projectId) {

        Project project = projectDAO.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        // STUDENT – czyszczenie redundancji
        List<Student> students = studentDAO.findAll().stream()
                .filter(s -> project.equals(s.getConfirmedProject()))
                .toList();

        for (Student student : students) {
            student.setConfirmedProject(null);
            student.setProjectConfirmed(false);
            student.setProjectAdmin(false);
            student.setProjectRole(null);
        }
        studentDAO.saveAll(students);

        // 2STUDENT_PROJECT
        studentProjectDAO.deleteAll(project.getAssignedStudents());

        // CRITERIA_PROJECT
        criteriaProjectDAO.deleteAll(
                criteriaProjectDAO.findByProject_Id(projectId)
        );

        // EXTERNAL_LINK (+ pliki)
        externalLinkDAO.deleteAll(project.getExternalLinks());

        // PROJECT_DEFENSE
        ProjectDefense defense = projectDefenseDAO.findByProjectIdAndIsActiveIsTrue(projectId);
        if (defense != null) {
            projectDefenseDAO.delete(defense);
        }

        // PROJECT_MARKET (jeśli istnieje)
        projectMarketDAO.findByProject_Id(projectId)
                .ifPresent(projectMarketDAO::delete);

        // PROJECT
        projectDAO.delete(project);
    }
}
