package pl.edu.amu.wmi.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.amu.wmi.model.enumeration.DataFeedType;
import pl.edu.amu.wmi.service.DataFeedReset;
import pl.edu.amu.wmi.service.exportdata.DataFeedExportService;
import pl.edu.amu.wmi.service.exportdata.impl.DataFeedExportServiceFactory;
import pl.edu.amu.wmi.service.importdata.DataFeedImportService;
import pl.edu.amu.wmi.service.importdata.impl.DataFeedImportServiceFactory;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/data")
@Slf4j
public class DataFeedController {

    private  final DataFeedReset resetService;

    public DataFeedController(DataFeedReset resetService)
    {
        this.resetService=resetService;
    }


    @Secured({"COORDINATOR"})
    @DeleteMapping("/reset")
    public ResponseEntity<Map<String, String>> resetDataFeed() {
        try {
            resetService.resetDatabase();
            return ResponseEntity.ok(Map.of("message", "Database reset successfully"));
        } catch (Exception e) {
            log.error("Failed to reset database", e);
            return ResponseEntity.status(500).body(Map.of("error", "Database reset failed: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/import/student", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createStudents(@RequestHeader("study-year") String studyYear,
                                               @RequestHeader("index-number") String userIndexNumber, @RequestParam MultipartFile data) throws Exception {
        DataFeedImportService service = DataFeedImportServiceFactory.getService(DataFeedType.NEW_STUDENT);
        service.saveRecords(data, studyYear);
        return ResponseEntity.ok().build();
    }

    @Secured({"COORDINATOR"})
    @PostMapping("/import/supervisor")
    public ResponseEntity<Void> createSupervisors(@RequestHeader("study-year") String studyYear,
                                                 @RequestHeader("index-number") String userIndexNumber,@RequestParam MultipartFile data) throws Exception {
        DataFeedImportService service = DataFeedImportServiceFactory.getService(DataFeedType.NEW_SUPERVISOR);
        service.saveRecords(data, studyYear);
        return ResponseEntity.ok().build();
    }

    @Secured({"COORDINATOR"})
    @GetMapping("export/student")
    public void exportStudentsData(@RequestHeader("study-year") String studyYear, HttpServletResponse servletResponse) throws Exception {
        servletResponse.setContentType("text/csv");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"students.csv\"");

        DataFeedExportService service = DataFeedExportServiceFactory.getService(DataFeedType.STUDENT_GROUPS);
        service.exportData(servletResponse.getWriter(), studyYear);
    }

    @Secured({"COORDINATOR"})
    @PostMapping("import/criteria")
    public ResponseEntity<Void> createCriteria(@RequestHeader("study-year") String studyYear, @RequestParam MultipartFile data) throws Exception {
        DataFeedImportService service = DataFeedImportServiceFactory.getService(DataFeedType.CRITERIA);
        service.saveRecords(data, studyYear);
        return ResponseEntity.ok().build();
    }

    @Secured({"COORDINATOR"})
    @GetMapping("export/criteria")
    public void exportCriteria(@RequestHeader("study-year") String studyYear, HttpServletResponse servletResponse) throws Exception {
        servletResponse.setContentType("application/json");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"criteria.json\"");
        DataFeedExportService service = DataFeedExportServiceFactory.getService(DataFeedType.CRITERIA);
        service.exportData(servletResponse.getWriter(), studyYear);
    }

    @Secured({"COORDINATOR"})
    @GetMapping("export/grades")
    public void exportGrades(@RequestHeader("study-year") String studyYear, HttpServletResponse servletResponse) throws Exception {
        servletResponse.setContentType("text/csv");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"grades.csv\"");
        DataFeedExportService service = DataFeedExportServiceFactory.getService(DataFeedType.GRADES);
        service.exportData(servletResponse.getWriter(), studyYear);
    }
//    @Secured({"COORDINATOR"})
//    @DeleteMapping("reset/database")
//    public void resetAllFromDatabaseExceptCoordinators()
//    {
//
//    }


}
