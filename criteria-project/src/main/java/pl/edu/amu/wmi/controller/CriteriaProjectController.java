package pl.edu.amu.wmi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.entity.CriteriaProject;
import pl.edu.amu.wmi.enumerations.TypeOfCriterium;
import pl.edu.amu.wmi.mapper.CriteriaProjectMapper;
import pl.edu.amu.wmi.service.CriteriaProjectService;
import pl.edu.amu.wmi.dto.CriteriaProjectDTO;
import pl.edu.amu.wmi.enumerations.Semester;

import java.util.*;

@RestController
@RequestMapping("/api/criteria-projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CriteriaProjectController {

    private final CriteriaProjectService criteriaProjectService;
    private final CriteriaProjectMapper mapper;

    @GetMapping
    public ResponseEntity<List<CriteriaProject>> getAll() {
        return ResponseEntity.ok(criteriaProjectService.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<CriteriaProject> getById(@PathVariable Long id) {
        return ResponseEntity.ok(criteriaProjectService.getById(id));
    }


    @GetMapping("/search")
    public ResponseEntity<List<CriteriaProject>> getByProjectAndSemester(@RequestParam Long projectId, @RequestParam Semester semester,@RequestParam TypeOfCriterium type)
    {
        return ResponseEntity.ok(criteriaProjectService.findByProjectAndSemester(projectId, semester,type));
    }

    @PatchMapping("/{id}/level")
    public ResponseEntity<CriteriaProjectDTO> updateLevelOfRealization(@PathVariable Long id,
                                                                       @RequestBody CriteriaProjectDTO dto) {
        CriteriaProject updated = criteriaProjectService.updateLevelOfRealization(id, dto);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<CriteriaProjectDTO> updateEnableForModification(@PathVariable Long id,
                                                                          @RequestParam boolean enable) {
        CriteriaProject updated = criteriaProjectService.updateEnableForModification(id, enable);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @PostMapping("/single")
    public ResponseEntity<CriteriaProjectDTO> create(@RequestBody @Valid CriteriaProjectDTO dto) {
        CriteriaProject created = criteriaProjectService.create(dto);
        CriteriaProjectDTO response = mapper.toDto(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    public ResponseEntity<List<CriteriaProjectDTO>> create(@RequestBody @Valid List<CriteriaProjectDTO> listDTO) {
        List<CriteriaProjectDTO> response = new ArrayList<>();

        for (CriteriaProjectDTO dto : listDTO) {
            boolean alreadyExists = criteriaProjectService.existsByKey(dto);

            if (!alreadyExists) {
                CriteriaProject created = criteriaProjectService.create(dto);
                response.add(mapper.toDto(created));
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        criteriaProjectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("It works, at least I think so ....");
    }

}
