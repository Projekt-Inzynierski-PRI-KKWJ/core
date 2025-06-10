package pl.edu.amu.wmi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.dto.*;
import pl.edu.amu.wmi.entity.CriteriaProject;
import pl.edu.amu.wmi.entity.Supervisor;
import pl.edu.amu.wmi.enumerations.LevelOfRealization;
import pl.edu.amu.wmi.enumerations.TypeOfCriterium;
import pl.edu.amu.wmi.mapper.CriteriaProjectMapper;
import pl.edu.amu.wmi.service.CriteriaProjectService;
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


    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<CriteriaProjectDTO>> getByProjectId(@PathVariable Long projectId) {
        List<CriteriaProject> projects = criteriaProjectService.findByProjectId(projectId);
        List<CriteriaProjectDTO> result = projects.stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }


    @GetMapping("/search")
    public ResponseEntity<List<CriteriaProject>> getByProjectAndSemester(@RequestParam Long projectId, @RequestParam Semester semester,@RequestParam TypeOfCriterium type)
    {
        return ResponseEntity.ok(criteriaProjectService.findByProjectAndSemester(projectId, semester,type));
    }

    @PatchMapping("/{id}/level")
    @Secured({"SUPERVISOR"})
    public ResponseEntity<Void> updateLevelOfRealization(
            @PathVariable Long id,
            @RequestBody UpdateLevelDTO dto) {
        criteriaProjectService.updateLevelOfRealization(id, dto.getLevelOfRealization());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/comment")
    @Secured({"SUPERVISOR"})
    public ResponseEntity<Void> updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentDTO dto) {
        criteriaProjectService.updateComment(id, dto.getComment());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/comment-level")
    @Secured({"SUPERVISOR"})
    public ResponseEntity<Void> updateCommentAndLevel(
            @PathVariable Long id,
            @RequestBody UpdateCommentAndLevelDTO dto) {
        criteriaProjectService.updateCommentAndLevel(id, dto.getComment(), dto.getLevelOfRealization());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/enable")
    @Secured({"SUPERVISOR"})
    public ResponseEntity<Void> updateEnableForModification(
            @PathVariable Long id,
            @RequestBody UpdateEnableDTO dto) {
        criteriaProjectService.updateEnableForModification(id, dto.getEnable());
        return ResponseEntity.ok().build();
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
