package pl.edu.amu.wmi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EXTERNAL_LINK_HISTORY")
public class ExternalLinkHistory extends AbstractEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "EXTERNAL_LINK_ID")
    private ExternalLink externalLink;

    private String previousUrl;

    @Enumerated(EnumType.STRING)
    private ExternalLink.LinkType previousLinkType;

    // File related fields for historical file information
    private String previousFilePath;
    private String previousOriginalFileName;
    private String previousContentType;
    private Long previousFileSize;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "CHANGED_BY_USER_ID")
    private UserData changedByUser;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ChangeType changeType;

    private String changeDescription;

    public enum ChangeType {
        URL_UPDATED,     // External URL was changed
        FILE_UPLOADED,   // New file was uploaded
        FILE_REPLACED,   // Existing file was replaced with new one
        FILE_DELETED,    // File was deleted
        LINK_CREATED,    // Initial link creation
        LINK_TYPE_CHANGED // Link type changed between EXTERNAL/INTERNAL
    }

}
