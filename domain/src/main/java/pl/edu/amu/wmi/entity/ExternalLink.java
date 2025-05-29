package pl.edu.amu.wmi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EXTERNAL_LINK")
public class ExternalLink extends AbstractEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "EXTERNAL_LINK_DEFINITION_ID")
    private ExternalLinkDefinition externalLinkDefinition;

    private String url;

    @Enumerated(EnumType.STRING)
    private LinkType linkType = LinkType.EXTERNAL;

    // File storage fields for internal files
    private String filePath;
    private String originalFileName;
    private String contentType;
    private Long fileSize;

    public enum LinkType {
        EXTERNAL, // URL
        INTERNAL  // File
    }

}
