package pl.edu.amu.wmi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.entity.CriteriaProject;
import pl.edu.amu.wmi.mapper.CriteriaProjectMapper;
import pl.edu.amu.wmi.service.CriteriaProjectService;
import pl.edu.amu.wmi.dto.CriteriaProjectDTO;
import pl.edu.amu.wmi.enumerations.Semester;

import java.util.List;

@RestController
@RequestMapping("/api/criteria-projects")
@RequiredArgsConstructor
public class CriteriaProjectController {

    private final CriteriaProjectService criteriaProjectService;
    private final CriteriaProjectMapper mapper;

    @GetMapping
    public ResponseEntity<List<CriteriaProject>> getAll() {
        return ResponseEntity.ok(criteriaProjectService.getAll());
    }


    @PostMapping
    public ResponseEntity<CriteriaProjectDTO> create(@RequestBody @Valid CriteriaProjectDTO dto) {
        CriteriaProject created = criteriaProjectService.create(dto);
        CriteriaProjectDTO response = mapper.toDto(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriteriaProject> getById(@PathVariable Long id) {
        return ResponseEntity.ok(criteriaProjectService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        criteriaProjectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CriteriaProject>> getByProjectAndSemester(@RequestParam Long projectId, @RequestParam Semester semester)
    {
        return ResponseEntity.ok(criteriaProjectService.findByProjectAndSemester(projectId, semester));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("It works, at least I think so ....");
    }

}
