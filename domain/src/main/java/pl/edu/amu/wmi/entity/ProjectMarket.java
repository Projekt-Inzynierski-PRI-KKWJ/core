package pl.edu.amu.wmi.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.edu.amu.wmi.enumerations.AcceptanceStatus;
import pl.edu.amu.wmi.enumerations.ProjectMarketStatus;

@Slf4j
@Getter
@Setter
@Entity
@Table(name = "PROJECT_MARKET")
public class ProjectMarket extends AbstractEntity {

    @OneToOne
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectMarketStatus status;

    private LocalDateTime closedAt;

    private Integer maxMembers;

    private String contactData;

    @OneToMany(mappedBy = "projectMarket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectApplication> applications;

    public List<UserData> getMembers() {
        var members = this.getProject().getAssignedStudents().stream().toList();
        if (members.isEmpty()) {
            return List.of();
        }
        return members.stream()
            .map(StudentProject::getStudent)
            .map(Student::getUserData)
            .toList();
    }

    public UserData getLeader() {
        var students = this.getProject().getAssignedStudents().stream().toList();
        var leader = students.stream().filter(StudentProject::isProjectAdmin).findFirst();
        return leader.map(studentProject -> studentProject.getStudent().getUserData()).orElse(null);
    }

    public StudentProject getProjectLeader() {
        var students = this.getProject().getAssignedStudents().stream().toList();
        return students.stream().filter(StudentProject::isProjectAdmin).findFirst().orElse(null);
    }

    public Integer getCurrentMembersCount() {
        return getMembers().size();
    }

    public void submit(Supervisor supervisor) {
        this.status = ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR;
        this.setModificationDate(LocalDateTime.now());
        this.getProject().submit(supervisor);
    }

    public void closeByOwner() {
        this.status = ProjectMarketStatus.CLOSED_BY_OWNER;
        this.setModificationDate(LocalDateTime.now());
        this.getProject().setAcceptanceStatus(AcceptanceStatus.REJECTED);
    }

    public void approveBySupervisor() {
        this.status = ProjectMarketStatus.APPROVED_BY_SUPERVISOR;
        this.setModificationDate(LocalDateTime.now());
        this.getProject().accept();
    }
}
