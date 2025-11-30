package pl.edu.amu.wmi.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.amu.wmi.model.project.ProjectDTO;
import pl.edu.amu.wmi.service.StatisticsService;


import java.util.List;

@RestController
@RequestMapping("/statistics")
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/projects")
    public List<ProjectDTO> getAllProjects()
    {
        log.info("ENDPOINT!!! URUCHOMIONY");
        return statisticsService.getAllProjects();
    }



    @GetMapping("/ping")
    public String x()
    {
        log.info("ENDPOINT!!! XXXXXXXXXXXXXXXXXXXX URUCHOMIONY");
        return "X";
    }


}
