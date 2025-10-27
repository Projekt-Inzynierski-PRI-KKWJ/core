package pl.edu.amu.wmi.web;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.amu.wmi.dao.RoleDAO;
import pl.edu.amu.wmi.dao.UserDataDAO;
import pl.edu.amu.wmi.model.user.*;
import pl.edu.amu.wmi.service.SessionDataService;
import pl.edu.amu.wmi.service.StudentService;
import pl.edu.amu.wmi.service.SupervisorService;
import pl.edu.amu.wmi.service.UserService;


import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final SupervisorService supervisorService;

    private final StudentService studentService;

    private final UserService userService;

    private final SessionDataService sessionDataService;

    private final UserDataDAO userDataRepository;


    @Autowired
    public UserController(SupervisorService supervisorService, StudentService studentService, UserService userService, SessionDataService sessionDataService, UserDataDAO userDataRepository, RoleDAO roleRepository) {
        this.supervisorService = supervisorService;
        this.studentService = studentService;
        this.userService = userService;
        this.sessionDataService = sessionDataService;
        this.userDataRepository = userDataRepository;

    }


    //Counts the number of users in the users table and returns its number to the frontend
    @GetMapping("/initialization/count")
    public ResponseEntity<Long> getUsersCount()
    {
        long count = userDataRepository.count();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/initialization/coordinator")
    public ResponseEntity<CoordinatorDTO> initializeCoordinator(@RequestBody @Valid CoordinatorDTO coordinatorDTO) {
        try {
            CoordinatorDTO created = userService.initializeCoordinator(coordinatorDTO);
            return ResponseEntity.ok(created);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("")
    public ResponseEntity<UserDTO> getUser(@RequestHeader("study-year") String studyYear) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getUser(userDetails.getUsername(), studyYear));
    }

    @PutMapping("/study-year")
    public ResponseEntity<UserDTO> updateStudyYear(@RequestHeader("study-year") String studyYear, @RequestBody ActualStudyYearDTO updatedStudyYear) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        sessionDataService.updateActualStudyYear(updatedStudyYear.studyYear(), userDetails.getUsername());
        return ResponseEntity.ok(userService.getUser(userDetails.getUsername(), studyYear));
    }

    @GetMapping("/supervisor")
    public ResponseEntity<List<SupervisorDTO>> getSupervisors(@RequestHeader("study-year") String studyYear) {
        return ResponseEntity.ok()
                .body(supervisorService.findAll(studyYear));
    }

    @Secured({"COORDINATOR"})
    @PostMapping("/supervisor")
    public ResponseEntity<SupervisorDTO> createSupervisor(@RequestHeader("study-year") String studyYear ,@RequestBody SupervisorCreationRequestDTO supervisor) {
        return ResponseEntity.ok()
                .body(supervisorService.create(supervisor, studyYear));
    }


    @GetMapping("/student")
    public ResponseEntity<List<StudentDTO>> getStudents(@RequestHeader("study-year") String studyYear) {
        return ResponseEntity.ok()
                .body(studentService.findAll(studyYear));
    }

    @Secured({"COORDINATOR"})
    @PostMapping("/student")
    public ResponseEntity<StudentDTO> createStudent(@RequestHeader("study-year") String studyYear, @RequestBody StudentCreationRequestDTO student) {
        return ResponseEntity.ok()
                .body(studentService.create(student, studyYear));
    }
}
