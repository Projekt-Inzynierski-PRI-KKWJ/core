package pl.edu.amu.wmi.controller;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.amu.wmi.model.EmailNotificationDataDTO;
import pl.edu.amu.wmi.service.NotificationService;
import pl.edu.amu.wmi.util.EMailTemplate;

@RestController
@Slf4j
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/receiver-data/{eMailTemplate}")
    public ResponseEntity<List<EmailNotificationDataDTO>> getReceiverData(@RequestHeader("study-year") String studyYear,
                                                                          @PathVariable String eMailTemplate) {
        return ResponseEntity.ok(notificationService.getReceiverData(studyYear, EMailTemplate.valueOf(eMailTemplate)));
    }
}
