package pl.edu.amu.wmi.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectMarketStatus status;

    private LocalDateTime publishedAt = LocalDateTime.now();
    private LocalDateTime closedAt;

    @Column(length = 2000)
    private String descriptionOverride;

    private Integer maxMembers;

    private String contactData;

    @OneToMany(mappedBy = "projectMarket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectApplication> applications;

    public void close() {
        if (ProjectMarketStatus.CLOSED != this.status) {
            this.status = ProjectMarketStatus.CLOSED;
            this.closedAt = LocalDateTime.now();
            setModificationDate(LocalDateTime.now());
        }
    }
}
