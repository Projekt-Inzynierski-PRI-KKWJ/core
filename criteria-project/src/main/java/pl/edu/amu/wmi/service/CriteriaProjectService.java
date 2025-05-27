package pl.edu.amu.wmi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.CriteriaProjectDAO;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.dao.UserDataDAO;
import pl.edu.amu.wmi.dto.CriteriaProjectDTO;
import pl.edu.amu.wmi.entity.CriteriaProject;
import pl.edu.amu.wmi.enumerations.Semester;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CriteriaProjectService {

    private final CriteriaProjectDAO criteriaProjectRepository;
    private final UserDataDAO userDataRepository;
    private final ProjectDAO projectRepository;
    public List<CriteriaProject> getAll() {
        return criteriaProjectRepository.findAll();
    }

    public CriteriaProject save(CriteriaProject project) {
        return criteriaProjectRepository.save(project);
    }

    public CriteriaProject getById(Long id) {
        return criteriaProjectRepository.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        criteriaProjectRepository.deleteById(id);
    }

    public List<CriteriaProject> findByProjectAndSemester(Long projectId, Semester semester) {
        return criteriaProjectRepository.findByProjectIdAndSemester(projectId, semester);
    }

    public CriteriaProject create(CriteriaProjectDTO dto) {
        CriteriaProject entity = new CriteriaProject();
        entity.setCriterium(dto.getCriterium());
        entity.setLevelOfRealization(dto.getLevelOfRealization());
        entity.setSemester(dto.getSemester());
        entity.setUserThatAddedTheCriterium(userDataRepository.findById(dto.getUserId()).orElseThrow());
        entity.setProject(projectRepository.findById(dto.getProjectId()).orElseThrow());

        return criteriaProjectRepository.save(entity);
    }

}