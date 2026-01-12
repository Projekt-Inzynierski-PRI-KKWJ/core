package pl.edu.amu.wmi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.edu.amu.wmi.enumerations.ProjectApplicationStatus;
import pl.edu.amu.wmi.enumerations.ProjectRole;

@Slf4j
@Getter
@Setter
@Entity
@Table(name = "PROJECT_APPLICATION")
public class ProjectApplication extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "project_market_id", nullable = false)
    private ProjectMarket projectMarket;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectApplicationStatus status;

    @Column(length = 2000)
    private String contactData;

    @Column(length = 2000)
    private String skills;

    @Column(length = 2000)
    private String otherInformation;

    private LocalDateTime decisionDate;

    public void accept() {
        var student = this.getStudent();
        var projectMarket = this.getProjectMarket();
        var project = projectMarket.getProject();
        
        // Only add student to project if it exists (approved projects)
        // For proposals, student will be added when supervisor approves
        if (project != null) {
            project.addStudent(student, ProjectRole.NONE, false);
        }
        
        this.status = ProjectApplicationStatus.ACCEPTED;
        setModificationDate(LocalDateTime.now());
        setDecisionDate(LocalDateTime.now());
    }

    public void reject() {
        this.status = ProjectApplicationStatus.REJECTED;
        setModificationDate(LocalDateTime.now());
        setDecisionDate(LocalDateTime.now());
    }
}
