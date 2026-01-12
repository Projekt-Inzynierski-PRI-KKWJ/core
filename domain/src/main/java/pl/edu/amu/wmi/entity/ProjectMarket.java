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
    @JoinColumn(name = "project_id", nullable = true, unique = true)
    private Project project;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectMarketStatus status;

    private LocalDateTime closedAt;

    private Integer maxMembers;

    private String contactData;

    @Column(length = 2000)
    private String supervisorFeedback;
    
    // Proposal fields (before project is created)
    @Column(nullable = false)
    private String proposalName;
    
    @Column(length = 2000)
    private String proposalDescription;
    
    @Column(nullable = false)
    private Long proposalOwnerId; // Student ID who created the proposal
    
    @Column(nullable = false)
    private String proposalStudyYear;
    
    private Long proposalSupervisorId; // Supervisor ID when submitted as proposal
    
    @Column(length = 1000)
    private String proposalTechnologies; // Comma-separated technologies

    @OneToMany(mappedBy = "projectMarket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectApplication> applications;

    public List<UserData> getMembers() {
        if (this.project == null) {
            // For proposals: return owner + accepted applicants
            // Note: We can't fetch owner's UserData here without a DAO reference
            // So this will return empty for proposals - use getCurrentMembersCount() instead
            return List.of();
        }
        var members = this.project.getAssignedStudents().stream().toList();
        if (members.isEmpty()) {
            return List.of();
        }
        return members.stream()
            .map(StudentProject::getStudent)
            .map(Student::getUserData)
            .toList();
    }

    public UserData getLeader() {
        if (this.project == null) {
            return null;
        }
        var students = this.project.getAssignedStudents().stream().toList();
        var leader = students.stream().filter(StudentProject::isProjectAdmin).findFirst();
        return leader.map(studentProject -> studentProject.getStudent().getUserData()).orElse(null);
    }

    public StudentProject getProjectLeader() {
        if (this.project == null) {
            return null;
        }
        var students = this.project.getAssignedStudents().stream().toList();
        return students.stream().filter(StudentProject::isProjectAdmin).findFirst().orElse(null);
    }

    public Integer getCurrentMembersCount() {
        if (this.project == null) {
            // For proposals: count owner (1) + accepted applicants
            long acceptedCount = this.applications != null 
                ? this.applications.stream()
                    .filter(app -> app.getStatus() == pl.edu.amu.wmi.enumerations.ProjectApplicationStatus.ACCEPTED)
                    .count()
                : 0;
            return 1 + (int) acceptedCount; // 1 for owner + accepted applicants
        }
        // For approved projects: count actual project members
        return getMembers().size();
    }
    
    public boolean hasAvailableSlots() {
        return getCurrentMembersCount() < this.maxMembers;
    }

    public void submit(Supervisor supervisor) {
        this.status = ProjectMarketStatus.SENT_FOR_APPROVAL_TO_SUPERVISOR;
        this.setModificationDate(LocalDateTime.now());
        if (this.project != null) {
            this.project.submit(supervisor);
        } else {
            // For proposals, store the supervisor ID
            this.proposalSupervisorId = supervisor.getId();
        }
    }

    public void closeByOwner() {
        this.status = ProjectMarketStatus.CLOSED_BY_OWNER;
        this.setModificationDate(LocalDateTime.now());
        if (this.project != null) {
            this.getProject().setAcceptanceStatus(AcceptanceStatus.REJECTED);
        }
    }

    public void approveBySupervisor() {
        this.status = ProjectMarketStatus.APPROVED_BY_SUPERVISOR;
        this.setModificationDate(LocalDateTime.now());
        // Project should be created in the service layer before calling this
        if (this.project != null) {
            this.getProject().accept();
        }
    }

    public void rejectBySupervisor() {
        this.status = ProjectMarketStatus.REJECTED_BY_SUPERVISOR;
        this.setModificationDate(LocalDateTime.now());
        if (this.project != null) {
            this.getProject().reject();
        }
    }
}
