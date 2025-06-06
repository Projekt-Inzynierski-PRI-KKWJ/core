package pl.edu.amu.wmi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.amu.wmi.dao.CriteriaProjectDAO;
import pl.edu.amu.wmi.dao.ProjectDAO;
import pl.edu.amu.wmi.dao.UserDataDAO;
import pl.edu.amu.wmi.dto.CriteriaProjectDTO;
import pl.edu.amu.wmi.entity.CriteriaProject;
import pl.edu.amu.wmi.enumerations.Semester;
import pl.edu.amu.wmi.enumerations.TypeOfCriterium;

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

    public List<CriteriaProject> findByProjectAndSemester(Long projectId, Semester semester, TypeOfCriterium type) {
        return criteriaProjectRepository.findByProject_IdAndSemesterAndType(projectId, semester,type);
    }

    public CriteriaProject create(CriteriaProjectDTO dto) {
        CriteriaProject entity = new CriteriaProject();
        entity.setCriterium(dto.getCriterium());
        entity.setUserThatAddedTheCriterium(userDataRepository.findById(dto.getUserId()).orElseThrow());
        entity.setLevelOfRealization(dto.getLevelOfRealization());
        entity.setSemester(dto.getSemester());
        entity.setType(dto.getType());
        entity.setComment(dto.getComment());
        entity.setProject(projectRepository.findById(dto.getProjectId()).orElseThrow());
        entity.setEnableForModification(dto.getEnableForModification());


        return criteriaProjectRepository.save(entity);
    }

}